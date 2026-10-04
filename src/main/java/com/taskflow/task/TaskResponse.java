package com.taskflow.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(Long id, String title, String description, TaskStatus status,
                           TaskPriority priority, LocalDate dueDate, LocalDateTime createdAt) {
    public static TaskResponse from(Task t) {
        return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(),
                t.getPriority(), t.getDueDate(), t.getCreatedAt());
    }
}
