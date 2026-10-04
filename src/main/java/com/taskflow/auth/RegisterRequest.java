package com.taskflow.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le nom est obligatoire") @Size(max = 100) String name,
        @NotBlank(message = "L'email est obligatoire") @Email(message = "Email invalide") String email,
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, max = 72, message = "8 à 72 caractères") String password) {
}
