package fr.iamdamba.javahelpdesklite.services;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketEntity;
import fr.iamdamba.javahelpdesklite.models.dtos.historic.CreateTicketHistoricDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.CreateTicketDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.TicketDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.UpdateTicketDto;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import fr.iamdamba.javahelpdesklite.models.enums.TicketHistoryActionEnum;
import fr.iamdamba.javahelpdesklite.models.enums.TicketStatusEnum;
import fr.iamdamba.javahelpdesklite.models.helpers.UtilsHelper;
import fr.iamdamba.javahelpdesklite.repositories.MemberRepository;
import fr.iamdamba.javahelpdesklite.repositories.TicketRepository;
import fr.iamdamba.javahelpdesklite.services.generic.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService implements BaseService<UUID, TicketDto, CreateTicketDto, UpdateTicketDto> {
    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;
    private final TicketHistoricService ticketHistoricService;

    // Assignation par ADMIN
    @Transactional
    public ResponseEntity<Void> assignTicket(UUID ticketId, UUID memberId) throws AccessDeniedException {

        MemberEntity authMember = UtilsHelper.getAuthenticatedMember();

        if (authMember == null || authMember.getRole() != MemberRoleEnum.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        TicketEntity ticket = ticketRepository.findById(ticketId).orElse(null);

        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }

        MemberEntity member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return ResponseEntity.notFound().build();
        }

        // Seuls les SUPPORT peuvent recevoir un ticket
        if (member.getRole() != MemberRoleEnum.SUPPORT) {
            return ResponseEntity.badRequest().build();
        }

        return saveAssignment(ticket, member);
    }


    // Prise en charge par SUPPORT
    @Transactional
    public ResponseEntity<Void> assignSelfTicket(UUID ticketId) throws AccessDeniedException {

        MemberEntity authMember = UtilsHelper.getAuthenticatedMember();

        if (authMember == null || authMember.getRole() != MemberRoleEnum.SUPPORT) {
            return ResponseEntity.status(403).build();
        }

        TicketEntity ticket = ticketRepository.findById(ticketId).orElse(null);

        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }

        // Impossible de prendre un ticket déjà assigné
        if (ticket.getAssignedTo() != null) {
            return ResponseEntity.badRequest().build();
        }

        return saveAssignment(ticket, authMember);
    }


    // Méthode privée commune
    private ResponseEntity<Void> saveAssignment(TicketEntity ticket, MemberEntity member) throws AccessDeniedException {

        MemberEntity previousMember = ticket.getAssignedTo();

        ticket.setAssignedTo(member);
        ticketRepository.save(ticket);

        ticketHistoricService.createNewHistoric(
                new CreateTicketHistoricDto(
                        TicketHistoryActionEnum.ASSIGNED,
                        previousMember != null ? previousMember.getId().toString() : null,
                        member.getId().toString(),
                        ticket.getId()
                )
        );

        return ResponseEntity.ok().build();
    }


    @Transactional
    public ResponseEntity<Void> changeStatus(UUID id, TicketStatusEnum newStatus) throws AccessDeniedException {
        TicketEntity entity = ticketRepository.findById(id).orElse(null);
        if (entity == null)
            return ResponseEntity.notFound().build();

        MemberEntity member = UtilsHelper.getAuthenticatedMember();
        if (member == null) {
            return ResponseEntity.status(403).build();
        }
        if (member.getRole() == MemberRoleEnum.SUPPORT
                && (entity.getAssignedTo() == null
                || !entity.getAssignedTo().getId().equals(member.getId()))) {
            return ResponseEntity.status(403).build();
        }

        boolean isValid = this.isValidTransition(entity.getStatus(), newStatus);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }

        // save old status
        TicketStatusEnum oldStatus = entity.getStatus();

        // update ticket status
        entity.setStatus(newStatus);
        ticketRepository.save(entity);

        // create ticket's historic
        ticketHistoricService.createNewHistoric(
                new CreateTicketHistoricDto(
                        TicketHistoryActionEnum.STATUS_CHANGED,
                        oldStatus.name(),
                        newStatus.name(),
                        entity.getId()
                )
        );


        return ResponseEntity.ok().build();
    }

    private boolean isValidTransition(
            TicketStatusEnum current,
            TicketStatusEnum next
    ) {
        return switch (current) {
            case OPEN -> next == TicketStatusEnum.IN_PROGRESS;
            case IN_PROGRESS -> next == TicketStatusEnum.WAITING
                    || next == TicketStatusEnum.RESOLVED;
            case WAITING -> next == TicketStatusEnum.IN_PROGRESS
                    || next == TicketStatusEnum.RESOLVED;
            case RESOLVED -> next == TicketStatusEnum.CLOSED
                    || next == TicketStatusEnum.IN_PROGRESS;
            case CLOSED -> false;
        };
    }

    // =====================
    // Overrides Methods
    // =====================

    @Override
    public ResponseEntity<List<TicketDto>> all() {
        return null;
    }

    @Override
    public ResponseEntity<Page<TicketDto>> search(Pageable pageable) {
        MemberEntity authMember = UtilsHelper.getAuthenticatedMember();
        Page<TicketEntity> tickets = switch (authMember.getRole()) {
            case ADMIN, SUPPORT -> ticketRepository.findAll(pageable);
            case EMPLOYEE -> ticketRepository.findAllByCreatedById(authMember.getId(), pageable);
        };

        return ResponseEntity.ok(tickets.map(TicketDto::fromEntity));
    }

    @Transactional
    @Override
    public ResponseEntity<CreateTicketDto> create(CreateTicketDto dto) throws AccessDeniedException {
        MemberEntity authMember = UtilsHelper.getAuthenticatedMember();

        if (authMember == null) {
            return ResponseEntity.badRequest().build();
        }

        TicketEntity entity = TicketEntity.builder()
                .createdBy(authMember)
                .title(dto.title())
                .description(dto.description())
                .status(dto.status())
                .priority(dto.priority())
                .build();
        ticketRepository.save(entity);

        // Create the historic
        CreateTicketHistoricDto newHistoric = new CreateTicketHistoricDto(TicketHistoryActionEnum.CREATED, null, null, entity.getId());
        ticketHistoricService.createNewHistoric(newHistoric);

        ticketHistoricService.createNewHistoric(newHistoric);

        return ResponseEntity.ok(dto);
    }

    @Transactional
    @Override
    public ResponseEntity<UpdateTicketDto> update(UUID id, UpdateTicketDto dto) throws AccessDeniedException {
        TicketEntity occurence = ticketRepository.findById(id).orElse(null);
        if (occurence == null) {
            return ResponseEntity.notFound().build();
        }

        occurence.setTitle(dto.title());
        occurence.setDescription(dto.description());
        occurence.setStatus(dto.status());
        occurence.setPriority(dto.priority());

        ticketRepository.save(occurence);

        // Create the historic
        CreateTicketHistoricDto newHistoric = new CreateTicketHistoricDto(TicketHistoryActionEnum.UPDATED, null, null, occurence.getId());
        ticketHistoricService.createNewHistoric(newHistoric);

        return ResponseEntity.ok(dto);
    }

    @Transactional
    @Override
    public ResponseEntity<Void> delete(UUID id) throws AccessDeniedException {
        TicketEntity entity = ticketRepository.findById(id).orElse(null);

        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        if (entity.getStatus() != TicketStatusEnum.OPEN) {
            return ResponseEntity.badRequest().build();
        }

        TicketStatusEnum oldStatus = entity.getStatus();

        entity.setStatus(TicketStatusEnum.CLOSED);
        ticketRepository.save(entity);

        ticketHistoricService.createNewHistoric(
                new CreateTicketHistoricDto(
                        TicketHistoryActionEnum.STATUS_CHANGED,
                        oldStatus.name(),
                        TicketStatusEnum.CLOSED.name(),
                        entity.getId()
                )
        );

        return ResponseEntity.ok().build();
    }
}
