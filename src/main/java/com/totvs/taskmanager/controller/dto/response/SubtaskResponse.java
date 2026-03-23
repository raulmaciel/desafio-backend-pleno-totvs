package com.totvs.taskmanager.controller.dto.response;

import com.totvs.taskmanager.entity.SubtaskEntity;
import com.totvs.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubtaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime concludedAt,
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
