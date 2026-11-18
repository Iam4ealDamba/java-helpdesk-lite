package fr.iamdamba.javahelpdesklite.models.dtos.ticket;

import fr.iamdamba.javahelpdesklite.models.enums.TicketPriorityEnum;
import fr.iamdamba.javahelpdesklite.models.enums.TicketStatusEnum;

import java.util.UUID;

public record UpdateTicketDto(String title, String description, TicketStatusEnum status,
                              TicketPriorityEnum priority) {

}
