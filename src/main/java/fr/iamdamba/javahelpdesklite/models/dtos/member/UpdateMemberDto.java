package fr.iamdamba.javahelpdesklite.models.dtos.member;

import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateMemberDto(
        @NotNull
        @Email(message = "Email is not valid")
        String email,
        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        MemberRoleEnum role,
        boolean isActive
) {
}
