package dev.raul.totvs.taskmanager.controller;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateSubTaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.request.UpdateSubtaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import dev.raul.totvs.taskmanager.service.SubtaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SubtaskController {
    private final SubtaskService subtaskService;

    @PostMapping("/tarefas/{taskId}/subtarefas")
    public ResponseEntity<SubtaskResponse> createSubtask(@PathVariable UUID taskId, @RequestBody @Valid CreateSubTaskRequest request){
        SubtaskResponse response = subtaskService.createSubtask(taskId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/subtarefas/{id}/status")
    public ResponseEntity<SubtaskResponse> updateSubtaskStatus(@PathVariable UUID id, @RequestBody @Valid UpdateSubtaskRequest request){
        SubtaskResponse response = subtaskService.updateSubtaskStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
