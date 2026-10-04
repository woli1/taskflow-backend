package com.taskflow.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Le titre est obligatoire") @Size(max = 150, message = "150 caractères max") String title,
        @Size(max = 1000, message = "1000 caractères max") String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate) {
}
