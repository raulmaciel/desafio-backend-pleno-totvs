package com.totvs.taskmanager.controller;

import com.totvs.taskmanager.controller.dto.request.CreateSubTaskRequest;
import com.totvs.taskmanager.controller.dto.request.UpdateSubtaskRequest;
import com.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import com.totvs.taskmanager.service.SubtaskService;
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
public class SubtaskController {
    private final SubtaskService subtaskService;

    @PostMapping("/tarefas/{taskId}/subtarefas")
    public ResponseEntity<SubtaskResponse> createSubtask(@PathVariable UUID taskId, @RequestBody @Valid CreateSubTaskRequest request) {
        SubtaskResponse response = subtaskService.createSubtask(taskId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/subtarefas/{id}/status")
    public ResponseEntity<SubtaskResponse> updateSubtaskStatus(@PathVariable UUID id, @RequestBody @Valid UpdateSubtaskRequest request) {
        SubtaskResponse response = subtaskService.updateSubtaskStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tarefas/{taskId}/subtarefas")
    public ResponseEntity<Page<SubtaskResponse>> listTasks(
            @PathVariable UUID taskId,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<SubtaskResponse> page = subtaskService.listSubtasksByTask(taskId, pageable);
        return ResponseEntity.ok(page);
    }
}
