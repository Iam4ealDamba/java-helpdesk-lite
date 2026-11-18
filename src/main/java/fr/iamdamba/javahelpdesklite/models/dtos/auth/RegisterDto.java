package fr.iamdamba.javahelpdesklite.models.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotNull
        @Email(message = "Email is not valid")
        String email,
        @NotNull
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        @NotNull
        String firstName,
        @NotNull
        String lastName
) {
}
