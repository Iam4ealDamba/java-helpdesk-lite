package fr.iamdamba.javahelpdesklite.repositories;

import fr.iamdamba.javahelpdesklite.entities.TicketHistoricEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketHistoricRepository extends JpaRepository<TicketHistoricEntity, UUID> {
    Page<TicketHistoricEntity> findAllByTicketId(UUID ticketId, Pageable pageable);
}
