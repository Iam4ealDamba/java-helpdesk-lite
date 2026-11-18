package fr.iamdamba.javahelpdesklite.services;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketHistoricEntity;
import fr.iamdamba.javahelpdesklite.models.dtos.historic.CreateTicketHistoricDto;
import fr.iamdamba.javahelpdesklite.models.dtos.historic.TicketHistoricDto;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import fr.iamdamba.javahelpdesklite.models.helpers.UtilsHelper;
import fr.iamdamba.javahelpdesklite.repositories.MemberRepository;
import fr.iamdamba.javahelpdesklite.repositories.TicketHistoricRepository;
import fr.iamdamba.javahelpdesklite.repositories.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketHistoricService {
    private final TicketHistoricRepository historicRepository;
    private final TicketRepository ticketRepository;

    public ResponseEntity<Page<TicketHistoricDto>> search(UUID id, Pageable pageable) {
        TicketEntity ticket = ticketRepository.findById(id).orElse(null);

        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }

        MemberEntity authMember = UtilsHelper.getAuthenticatedMember();

        if (authMember == null) {
            return ResponseEntity.status(403).build();
        }

        if (authMember.getRole() == MemberRoleEnum.EMPLOYEE
                && !ticket.getCreatedBy().getId().equals(authMember.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                historicRepository.findAllByTicketId(id, pageable)
                        .map(TicketHistoricDto::fromEntity)
        );
    }

    public void createNewHistoric(CreateTicketHistoricDto dto) throws AccessDeniedException {
        TicketEntity ticket = ticketRepository.findById(dto.ticketId()).orElse(null);
        if (ticket == null) {
            throw new EntityNotFoundException("Error occurred while creating ticket historic");
        }

        MemberEntity member = UtilsHelper.getAuthenticatedMember();
        if (member == null) {
            throw new AccessDeniedException("Error occurred while creating ticket historic");
        }

        TicketHistoricEntity entity = TicketHistoricEntity.builder()
                .ticket(ticket)
                .performedBy(member)
                .action(dto.action())
                .oldValue(dto.oldValue())
                .newValue(dto.newValue())
                .build();

        historicRepository.save(entity);
    }
}
