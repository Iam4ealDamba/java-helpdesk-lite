package fr.iamdamba.javahelpdesklite.entities;

import fr.iamdamba.javahelpdesklite.entities.generic.BaseEntity;
import fr.iamdamba.javahelpdesklite.models.enums.TicketPriorityEnum;
import fr.iamdamba.javahelpdesklite.models.enums.TicketStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketEntity extends BaseEntity {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private TicketStatusEnum status;

    @Column(name = "priority", nullable = false)
    private TicketPriorityEnum priority;

    // ======================
    // Relationships
    // ======================
    @OneToMany(mappedBy = "ticket")
    private List<TicketCommentEntity> comments;

    @OneToMany(mappedBy = "ticket")
    private List<TicketHistoricEntity> ticketHistorics;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private MemberEntity createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private MemberEntity assignedTo;
}
