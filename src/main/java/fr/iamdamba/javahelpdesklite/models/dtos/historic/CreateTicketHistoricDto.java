package fr.iamdamba.javahelpdesklite.models.dtos.historic;

import fr.iamdamba.javahelpdesklite.models.enums.TicketHistoryActionEnum;

import java.util.UUID;

public record CreateTicketHistoricDto(TicketHistoryActionEnum action, String oldValue, String newValue, UUID ticketId) {
}
