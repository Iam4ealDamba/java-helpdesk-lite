package fr.iamdamba.javahelpdesklite.repositories;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByEmailAndIsActive(String email, boolean isActive);

    Optional<MemberEntity> findByRole(MemberRoleEnum role);
}
