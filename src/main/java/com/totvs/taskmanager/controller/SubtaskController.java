package com.totvs.taskmanager.controller;

import com.totvs.taskmanager.controller.dto.request.CreateSubTaskRequest;
import com.totvs.taskmanager.controller.dto.request.UpdateSubtaskRequest;
import com.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import com.totvs.taskmanager.exception.StandardError;
import com.totvs.taskmanager.service.SubtaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Subtasks", description = "Operations related to subtasks")
public class SubtaskController {
    private final SubtaskService subtaskService;

    @Operation(
            summary = "Create subtask",
            description = "Creates a new subtask for an existing task."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subtask created successfully",
                    content = @Content(schema = @Schema(implementation = SubtaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping("/tarefas/{taskId}/subtarefas")
    public ResponseEntity<SubtaskResponse> createSubtask(
            @Parameter(description = "Task identifier", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID taskId, @RequestBody @Valid CreateSubTaskRequest request) {
        SubtaskResponse response = subtaskService.createSubtask(taskId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Update subtask status",
            description = "Updates the status of a subtask."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subtask status updated successfully",
                    content = @Content(schema = @Schema(implementation = SubtaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Subtask not found",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PatchMapping("/subtarefas/{id}/status")
    public ResponseEntity<SubtaskResponse> updateSubtaskStatus(@PathVariable UUID id, @RequestBody @Valid UpdateSubtaskRequest request) {
        SubtaskResponse response = subtaskService.updateSubtaskStatus(id, request);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "List subtasks by task",
            description = "Returns paginated subtasks for a given task."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subtasks listed successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @GetMapping("/tarefas/{taskId}/subtarefas")
    public ResponseEntity<Page<SubtaskResponse>> listSubtask(
            @Parameter(description = "Task identifier", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID taskId,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<SubtaskResponse> page = subtaskService.listSubtasksByTask(taskId, pageable);
        return ResponseEntity.ok(page);
    }
}
