package dev.raul.totvs.taskmanager.service;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateTaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.TaskResponse;
import dev.raul.totvs.taskmanager.entity.TaskEntity;
import dev.raul.totvs.taskmanager.entity.UserEntity;
import dev.raul.totvs.taskmanager.exception.ResourceNotFoundException;
import dev.raul.totvs.taskmanager.repository.TaskRepository;
import dev.raul.totvs.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        UserEntity user = userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaskEntity task = request.toEntity();
        task.setUser(user);

        TaskEntity savedTask = taskRepository.save(task);


        return TaskResponse.fromEntity(savedTask);
    }
}
