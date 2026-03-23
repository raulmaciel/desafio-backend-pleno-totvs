package com.totvs.taskmanager.service;

import com.totvs.taskmanager.controller.dto.request.CreateSubTaskRequest;
import com.totvs.taskmanager.controller.dto.request.UpdateSubtaskRequest;
import com.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import com.totvs.taskmanager.entity.SubtaskEntity;
import com.totvs.taskmanager.entity.TaskEntity;
import com.totvs.taskmanager.enums.TaskStatus;
import com.totvs.taskmanager.exception.ResourceNotFoundException;
import com.totvs.taskmanager.repository.SubtaskRepository;
import com.totvs.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    public SubtaskResponse updateSubtaskStatus(UUID id, UpdateSubtaskRequest request) {
        SubtaskEntity subtask = subtaskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subtask not found"));
        TaskStatus requestStatus = request.status();

        if (requestStatus == TaskStatus.COMPLETED){
            subtask.setConcludedAt(LocalDateTime.now());
        }else{
            subtask.setConcludedAt(null);
        }

        subtask.setStatus(requestStatus);
        SubtaskEntity updatedSubtask = subtaskRepository.save(subtask);

        return SubtaskResponse.fromEntity(updatedSubtask);
    }

    @Transactional(readOnly = true)
    public Page<SubtaskResponse> listSubtasksByTask(UUID taskId, Pageable pageable) {
        TaskEntity task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return subtaskRepository.findByTask_Id(taskId, pageable).map(SubtaskResponse::fromEntity);
    }
}
