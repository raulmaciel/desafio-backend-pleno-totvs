package dev.raul.totvs.taskmanager.controller.dto.request;

import dev.raul.totvs.taskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSubtaskRequest(
        @NotNull(message = "Status is required")
        TaskStatus status
) {
}
