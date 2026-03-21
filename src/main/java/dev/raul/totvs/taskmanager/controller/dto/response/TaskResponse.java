package dev.raul.totvs.taskmanager.controller.dto.response;

import dev.raul.totvs.taskmanager.entity.TaskEntity;
import dev.raul.totvs.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime concludedAt,
        UserResponse user
) {
    public static TaskResponse fromEntity(TaskEntity entity) {
        return new TaskResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getConcludedAt(),
                entity.getUser() != null ? UserResponse.fromEntity(entity.getUser()) : null
        );
    }
}
