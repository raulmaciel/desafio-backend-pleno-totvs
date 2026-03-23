package com.totvs.taskmanager.service;

import com.totvs.taskmanager.controller.dto.request.CreateTaskRequest;
import com.totvs.taskmanager.controller.dto.request.UpdateTaskStatusRequest;
import com.totvs.taskmanager.controller.dto.response.TaskResponse;
import com.totvs.taskmanager.entity.TaskEntity;
import com.totvs.taskmanager.entity.UserEntity;
import com.totvs.taskmanager.enums.TaskStatus;
import com.totvs.taskmanager.exception.BusinessRuleException;
import com.totvs.taskmanager.exception.ResourceNotFoundException;
import com.totvs.taskmanager.repository.SubtaskRepository;
import com.totvs.taskmanager.repository.TaskRepository;
import com.totvs.taskmanager.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SubtaskRepository subtaskRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        UserEntity user = userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaskEntity task = request.toEntity();
        task.setUser(user);

        TaskEntity savedTask = taskRepository.save(task);


        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse updateTaskStatus(UUID id, UpdateTaskStatusRequest request) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskStatus requestStatus = request.status();

        if (requestStatus == TaskStatus.COMPLETED){
            boolean hasPendingSubtasks = subtaskRepository.existsByTaskIdAndStatusNot(id, TaskStatus.COMPLETED);
            if (hasPendingSubtasks){
                throw new BusinessRuleException("Cannot complete a task with pending subtasks.");
            }
            task.setConcludedAt(LocalDateTime.now());
        } else {
            task.setConcludedAt(null);
        }

        task.setStatus(requestStatus);
        TaskEntity updatedTask = taskRepository.save(task);

        return TaskResponse.fromEntity(updatedTask);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> listTasks(TaskStatus status, UUID userId, Pageable pageable) {
        return taskRepository.findTasksWithFilters(status, userId, pageable)
                .map(TaskResponse::fromEntity);
    }
}
