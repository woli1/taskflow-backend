package com.taskflow.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "Le nom est obligatoire") @Size(max = 100, message = "100 caractères max") String name,
        @Size(max = 500, message = "500 caractères max") String description) {
}
