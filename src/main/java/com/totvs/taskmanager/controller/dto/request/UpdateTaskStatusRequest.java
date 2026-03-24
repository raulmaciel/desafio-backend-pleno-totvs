package com.totvs.taskmanager.controller.dto.request;

import com.totvs.taskmanager.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update task status")
public record UpdateTaskStatusRequest(

        @Schema(
                description = "New status of the task",
                example = "COMPLETED",
                allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"}
        )
        @NotNull(message = "Status is required")TaskStatus status
        ) {
}
