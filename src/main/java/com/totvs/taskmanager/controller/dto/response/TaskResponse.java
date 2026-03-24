package com.totvs.taskmanager.controller.dto.response;

import com.totvs.taskmanager.entity.TaskEntity;
import com.totvs.taskmanager.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Task response data")
public record TaskResponse(
        @Schema(
                description = "Unique identifier of the task",
                example = "550e8400-e29b-41d4-a716-446655440001"
        )
        UUID id,

        @Schema(
                description = "Task title",
                example = "Read a book"
        )
        String title,

        @Schema(
                description = "Task description",
                example = "Read 20 pages of a programming book"
        )
        String description,

        @Schema(
                description = "Current status of the task",
                example = "IN_PROGRESS",
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"}
        )
        TaskStatus status,

        @Schema(
                description = "Date and time when the task was created",
                example = "2026-03-24T10:15:30"
        )
        LocalDateTime createdAt,

        @Schema(
                description = "Date and time only when the task was completed",
                example = "2026-03-24T12:00:00"
        )
        LocalDateTime concludedAt,

        @Schema(
                description = "User who owns the task"
        )
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
