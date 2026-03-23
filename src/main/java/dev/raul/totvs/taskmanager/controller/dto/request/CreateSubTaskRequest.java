package dev.raul.totvs.taskmanager.controller.dto.request;

import dev.raul.totvs.taskmanager.entity.SubtaskEntity;
import jakarta.validation.constraints.NotBlank;

public record CreateSubTaskRequest(
        @NotBlank(message = "The title is required") String title,
        String description
) {
    public SubtaskEntity toEntity() {
        return SubtaskEntity.builder()
                .title(this.title)
                .description(this.description)
                .build();
    }
}
