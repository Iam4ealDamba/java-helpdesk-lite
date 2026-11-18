package fr.iamdamba.javahelpdesklite.models.dtos.member;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MemberDto(
        @NotNull
        UUID id,
        @NotNull
        @Email(message = "Email must be valid")
        String email,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        MemberRoleEnum role,
        @NotNull
        boolean isActive) {

    public static MemberDto fromEntity(MemberEntity entity) {
        return new MemberDto(entity.getId(), entity.getEmail(), entity.getFirstName(), entity.getLastName(), entity.getRole(), entity.isActive());
    }
}
