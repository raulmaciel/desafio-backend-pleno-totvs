package com.totvs.taskmanager.controller.dto.response;

import com.totvs.taskmanager.entity.SubtaskEntity;
import com.totvs.taskmanager.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Subtask response data")
public record SubtaskResponse(
        @Schema(
                description = "Unique identifier of the subtask",
                example = "550e8400-e29b-41d4-a716-446655440002"
        )
        UUID id,

        @Schema(
                description = "Subtask title",
                example = "Open the book"
        )
        String title,

        @Schema(
                description = "Subtask description",
                example = "Open the book at page 1"
        )
        String description,

        @Schema(
                description = "Current status of the subtask",
                example = "PENDING",
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"}
        )
        TaskStatus status,

        @Schema(
                description = "Date and time when the subtask was created",
                example = "2026-03-24T10:20:00"
        )
        LocalDateTime createdAt,

        @Schema(
                description = "Date and time when the subtask was completed",
                example = "2026-03-24T10:30:00"
        )
        LocalDateTime concludedAt,

        @Schema(
                description = "Identifier of the parent task",
                example = "550e8400-e29b-41d4-a716-446655440001"
        )
        UUID taskId
) {
    public static SubtaskResponse fromEntity(SubtaskEntity entity) {
        return new SubtaskResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getConcludedAt(),
                entity.getTask() != null ? entity.getTask().getId() : null
        );
    }
}
