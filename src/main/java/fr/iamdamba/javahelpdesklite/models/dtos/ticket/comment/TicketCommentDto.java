package fr.iamdamba.javahelpdesklite.models.dtos.ticket.comment;

import fr.iamdamba.javahelpdesklite.entities.TicketCommentEntity;

import java.util.UUID;

public record TicketCommentDto(
        String content,
        UUID authorId,
        UUID ticketId
) {
    public static TicketCommentDto fromEntity(TicketCommentEntity entity) {
        return new TicketCommentDto(entity.getContent(), entity.getAuthor().getId(), entity.getTicket().getId());
    }
}
