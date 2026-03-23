package dev.raul.totvs.taskmanager.service;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateSubTaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import dev.raul.totvs.taskmanager.entity.SubtaskEntity;
import dev.raul.totvs.taskmanager.entity.TaskEntity;
import dev.raul.totvs.taskmanager.exception.ResourceNotFoundException;
import dev.raul.totvs.taskmanager.repository.SubtaskRepository;
import dev.raul.totvs.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubtaskService {
    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public SubtaskResponse createSubtask(UUID taskId, CreateSubTaskRequest request) {
        TaskEntity task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        SubtaskEntity subtask = request.toEntity();
        subtask.setTask(task);

        SubtaskEntity savedSubtask = subtaskRepository.save(subtask);

        return SubtaskResponse.fromEntity(savedSubtask);
    }
}
