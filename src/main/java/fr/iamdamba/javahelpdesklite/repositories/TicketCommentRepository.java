package fr.iamdamba.javahelpdesklite.repositories;

import fr.iamdamba.javahelpdesklite.entities.TicketCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketCommentEntity, UUID> {
    Optional<TicketCommentEntity> findById(UUID id);

    List<TicketCommentEntity> findAllByTicketIdOrderByCreatedAtAsc(UUID ticketId);
}
