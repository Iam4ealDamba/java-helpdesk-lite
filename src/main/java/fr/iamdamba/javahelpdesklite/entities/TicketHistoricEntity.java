package fr.iamdamba.javahelpdesklite.entities;

import fr.iamdamba.javahelpdesklite.entities.generic.BaseEntity;
import fr.iamdamba.javahelpdesklite.models.enums.TicketHistoryActionEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ticket_historic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketHistoricEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketHistoryActionEnum action;

    @Column
    private String oldValue;

    @Column
    private String newValue;

    // ============================
    // Constructors
    // ============================

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity performedBy;
}
