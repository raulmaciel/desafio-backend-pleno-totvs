package com.totvs.taskmanager.controller.dto.request;

import com.totvs.taskmanager.entity.TaskEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to create a new task")
public record CreateTaskRequest(
        @Schema(description = "Task title", example = "Set up project")
        @NotBlank String title,

        @Schema(description = "Task description", example = "Configure project java")
        String description,

        @Schema(description = "User identifier that owns the task", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "The user identifier is required")
        UUID userId
) {
        public TaskEntity toEntity() {
                return TaskEntity.builder()
                        .title(this.title)
                        .description(this.description)
                        .build();
        }
}
