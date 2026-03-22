package dev.raul.totvs.taskmanager.controller;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateTaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.request.UpdateTaskStatusRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.TaskResponse;
import dev.raul.totvs.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid CreateTaskRequest request){
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable UUID id,
                                                         @RequestBody @Valid UpdateTaskStatusRequest request){
        TaskResponse response = taskService.updateTaskStatus(id,request);
        return ResponseEntity.ok(response);
    }
}
