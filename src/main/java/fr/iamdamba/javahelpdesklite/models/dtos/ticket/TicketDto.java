package fr.iamdamba.javahelpdesklite.models.dtos.ticket;

import fr.iamdamba.javahelpdesklite.entities.TicketEntity;
import fr.iamdamba.javahelpdesklite.models.enums.TicketPriorityEnum;
import fr.iamdamba.javahelpdesklite.models.enums.TicketStatusEnum;

import java.util.UUID;

public record TicketDto(UUID id, String title, String description, TicketStatusEnum status,
                        TicketPriorityEnum priority) {

    public static TicketDto fromEntity(TicketEntity ticket) {
        return new TicketDto(ticket.getId(), ticket.getTitle(), ticket.getDescription(), ticket.getStatus(), ticket.getPriority());
    }

}
