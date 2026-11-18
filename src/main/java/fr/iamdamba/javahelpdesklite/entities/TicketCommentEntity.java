package fr.iamdamba.javahelpdesklite.entities;

import fr.iamdamba.javahelpdesklite.entities.generic.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ticket_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCommentEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private TicketEntity ticket;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity author;
}