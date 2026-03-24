package com.totvs.taskmanager.controller;

import com.totvs.taskmanager.controller.dto.request.CreateTaskRequest;
import com.totvs.taskmanager.controller.dto.request.UpdateTaskStatusRequest;
import com.totvs.taskmanager.controller.dto.response.TaskResponse;
import com.totvs.taskmanager.enums.TaskStatus;
import com.totvs.taskmanager.exception.StandardError;
import com.totvs.taskmanager.service.TaskService;
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
@RequestMapping("/tarefas")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Operations related to tasks")
public class TaskController {
    private final TaskService taskService;

    @Operation(
            summary = "Create task",
            description = "Creates a new task for an existing user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update task status",
            description = "Updates the status of a task. A task cannot be completed while it has pending subtasks."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "409", description = "Business rule violation",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@Parameter(description = "Task identifier", example = "550e8400-e29b-41d4-a716-446655440000")
                                                             @PathVariable UUID id, @RequestBody @Valid UpdateTaskStatusRequest request) {
        TaskResponse response = taskService.updateTaskStatus(id, request);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "List tasks",
            description = "Returns paginated tasks with optional filters by status and user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks listed successfully")
    })
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> listTasks(
            @Parameter(description = "Optional task status filter")
            @RequestParam(required = false)TaskStatus status,

            @Parameter(description = "Optional user identifier filter", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID userId,

            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ){
        Page<TaskResponse> page = taskService.listTasks(status, userId, pageable);
        return ResponseEntity.ok(page);
    }


}
