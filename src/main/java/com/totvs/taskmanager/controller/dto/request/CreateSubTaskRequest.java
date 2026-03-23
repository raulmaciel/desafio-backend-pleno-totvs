package com.totvs.taskmanager.controller.dto.request;

import com.totvs.taskmanager.entity.SubtaskEntity;
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
