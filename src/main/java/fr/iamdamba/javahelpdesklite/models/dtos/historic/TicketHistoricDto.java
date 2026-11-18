package fr.iamdamba.javahelpdesklite.models.dtos.historic;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketEntity;
import fr.iamdamba.javahelpdesklite.entities.TicketHistoricEntity;
import fr.iamdamba.javahelpdesklite.models.enums.TicketHistoryActionEnum;

import java.util.UUID;

public record TicketHistoricDto(UUID id, TicketHistoryActionEnum action, String oldValue, String newValue,
                                TicketEntity ticketId, MemberEntity memberId) {
    public static TicketHistoricDto fromEntity(TicketHistoricEntity historic) {
        return new TicketHistoricDto(historic.getId(), historic.getAction(), historic.getOldValue(), historic.getNewValue(), historic.getTicket(), historic.getPerformedBy());
    }
}
