package dev.raul.totvs.taskmanager.controller.dto.request;

import dev.raul.totvs.taskmanager.entity.TaskEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTaskRequest(
        @NotBlank String title,

        String description,

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
