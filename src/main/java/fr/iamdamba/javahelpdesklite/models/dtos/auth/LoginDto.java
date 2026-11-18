package fr.iamdamba.javahelpdesklite.models.dtos.auth;

import fr.iamdamba.javahelpdesklite.models.enums.MemberRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginDto(@NotNull @Email(message = "Email is not valid") String email,
                       @NotNull @Size(min = 8, message = "Password must be at least 8 characters long") String password) {
}
