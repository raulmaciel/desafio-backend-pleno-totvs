package com.totvs.taskmanager.controller.dto.request;

import com.totvs.taskmanager.entity.SubtaskEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "Request to create a new subtask")
public record CreateSubTaskRequest(
        @Schema(description = "Subtask title", example = "Read 10 pages of the yellow book")
        @NotBlank(message = "The title is required") String title,

        @Schema(description = "Subtask description", example = "Read the initial chapter")
        String description
) {
    public SubtaskEntity toEntity() {
        return SubtaskEntity.builder()
                .title(this.title)
                .description(this.description)
                .build();
    }
}
