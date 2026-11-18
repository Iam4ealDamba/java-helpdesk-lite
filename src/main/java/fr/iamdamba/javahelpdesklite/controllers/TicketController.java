package fr.iamdamba.javahelpdesklite.controllers;

import fr.iamdamba.javahelpdesklite.controllers.generic.BaseController;
import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.models.dtos.historic.TicketHistoricDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.CreateTicketDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.TicketDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.UpdateTicketDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.comment.CreateTicketCommentDto;
import fr.iamdamba.javahelpdesklite.models.dtos.ticket.comment.TicketCommentDto;
import fr.iamdamba.javahelpdesklite.models.enums.TicketStatusEnum;
import fr.iamdamba.javahelpdesklite.services.TicketCommentService;
import fr.iamdamba.javahelpdesklite.services.TicketHistoricService;
import fr.iamdamba.javahelpdesklite.services.TicketService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TicketController implements BaseController<UUID, TicketDto, CreateTicketDto, UpdateTicketDto> {
    private final TicketService ticketService;
    private final TicketHistoricService ticketHistoricService;
    private final TicketCommentService ticketCommentService;

    @GetMapping
    @Override
    public ResponseEntity<List<TicketDto>> all() {
        return ticketService.all();
    }

    @GetMapping("/search")
    @Override
    public ResponseEntity<Page<TicketDto>> search(@PageableDefault(size = 10) Pageable pageable) {
        return ticketService.search(pageable);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<Page<TicketHistoricDto>> searchHistory(@PathVariable UUID id, @PageableDefault(size = 10) Pageable pageable) {
        return ticketHistoricService.search(id, pageable);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketCommentDto>> getComments(@PathVariable UUID id) {
        return ticketCommentService.findByTicketId(id);
    }

    @PostMapping
    @Override
    public ResponseEntity<CreateTicketDto> create(@RequestBody CreateTicketDto createTicketDto) throws AccessDeniedException {
        return ticketService.create(createTicketDto);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<TicketCommentDto> createComment(@PathVariable UUID id, @RequestBody CreateTicketCommentDto createTicketCommentDto) throws AccessDeniedException {
        return ticketCommentService.create(id, createTicketCommentDto);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<UpdateTicketDto> update(@PathVariable UUID id, @RequestBody UpdateTicketDto updateTicketDto) throws AccessDeniedException {
        return ticketService.update(id, updateTicketDto);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable UUID id, @RequestBody TicketStatusEnum status) throws AccessDeniedException {
        return ticketService.changeStatus(id, status);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Void> assignSelfTicket(@PathVariable UUID id) throws AccessDeniedException {
        return ticketService.assignSelfTicket(id);
    }

    @PutMapping("/{id}/assign/{memberId}")
    public ResponseEntity<Void> assignMemberTicket(@PathVariable UUID id, @PathVariable UUID memberId) throws AccessDeniedException {
        return ticketService.assignTicket(id, memberId);
    }

    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<TicketCommentDto> updateComment(@PathVariable UUID id, @PathVariable UUID commentId, @RequestBody CreateTicketCommentDto createTicketCommentDto) throws AccessDeniedException {
        return ticketCommentService.update(commentId, id, createTicketCommentDto);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws AccessDeniedException {
        return ticketService.delete(id);
    }
}
