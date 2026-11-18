package fr.iamdamba.javahelpdesklite.repositories;

import fr.iamdamba.javahelpdesklite.entities.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
    Page<TicketEntity> findAllByCreatedById(UUID createdBy, Pageable pageable);

    List<TicketEntity> findAllByCreatedById(UUID createdBy);

    Page<TicketEntity> findAll(
            Specification<TicketEntity> specification,
            Pageable pageable
    );
}
