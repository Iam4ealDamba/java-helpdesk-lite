package fr.iamdamba.javahelpdesklite.services;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketCommentEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketHistoricEntity;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.comment.CreateTicketCommentDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.comment.TicketCommentDto;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import fr.iamdamba.javahelpdesklite.models.enums.TicketHistoryActionEnum;
import fr.iamdamba.javahelpdesklite.models.helpers.UtilsHelper;
import fr.iamdamba.javahelpdesklite.repositories.TicketCommentRepository;
import fr.iamdamba.javahelpdesklite.repositories.TicketHistoricRepository;
import fr.iamdamba.javahelpdesklite.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketCommentService {
    private final TicketRepository ticketRepository;
    private final TicketHistoricRepository historicRepository;
    private final TicketCommentRepository commentRepo;

    public ResponseEntity<List<TicketCommentDto>> findByTicketId(UUID ticketId) {

        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        MemberEntity member = UtilsHelper.getAuthenticatedMember();

        if (member == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        if (member.getRole() == MemberRoleEnum.EMPLOYEE
                && !ticket.getCreatedBy().getId().equals(member.getId())) {
            throw new AccessDeniedException("User not authorized");
        }

        List<TicketCommentDto> comments = commentRepo
                .findAllByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(TicketCommentDto::fromEntity)
                .toList();

        return ResponseEntity.ok(comments);
    }

    @Transactional
    public ResponseEntity<TicketCommentDto> create(UUID ticketId, CreateTicketCommentDto dto) throws EntityNotFoundException {
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        MemberEntity member = UtilsHelper.getAuthenticatedMember();
        if (member == null)
            throw new AccessDeniedException("User not authenticated");

        TicketCommentEntity comment = TicketCommentEntity.builder()
                .content(dto.content())
                .ticket(ticket)
                .author(member)
                .build();

        commentRepo.save(comment);

        // Add history
        historicRepository.save(TicketHistoricEntity.builder()
                .action(TicketHistoryActionEnum.COMMENT_ADDED)
                .oldValue(null)
                .newValue("Comment added")
                .performedBy(member)
                .ticket(ticket)
                .build()
        );

        return ResponseEntity.ok(TicketCommentDto.fromEntity(comment));
    }

    @Transactional
    public ResponseEntity<TicketCommentDto> update(UUID id, UUID ticketId, CreateTicketCommentDto dto) throws AccessDeniedException {
        TicketEntity ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new AccessDeniedException("Ticket not found"));
        MemberEntity member = UtilsHelper.getAuthenticatedMember();
        if (member == null)
            throw new AccessDeniedException("User not authorized");

        TicketCommentEntity comment = commentRepo.findById(id).orElseThrow(() -> new AccessDeniedException("Comment not found"));
        if (!comment.getTicket().getId().equals(ticket.getId()) || member.getRole() != MemberRoleEnum.ADMIN && !comment.getAuthor().getId().equals(member.getId()))
            throw new AccessDeniedException("User not authorized");

        String oldValue = comment.getContent();

        // Update comment content
        comment.setContent(dto.content());

        // Save updated comment
        commentRepo.save(comment);

        // Add history
        historicRepository.save(TicketHistoricEntity.builder()
                .action(TicketHistoryActionEnum.COMMENT_UPDATED)
                .oldValue(oldValue)
                .newValue(dto.content())
                .performedBy(member)
                .ticket(ticket)
                .build()
        );

        return ResponseEntity.ok(TicketCommentDto.fromEntity(comment));
    }
}
