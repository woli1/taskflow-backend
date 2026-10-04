package com.taskflow.task;

import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull(message = "Le statut est obligatoire") TaskStatus status) {
}
