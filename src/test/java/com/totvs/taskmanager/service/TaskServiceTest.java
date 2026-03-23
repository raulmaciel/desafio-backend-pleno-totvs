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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubtaskRepository subtaskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Should create task Successfully")
    void shouldCreateTaskSuccessfully(){
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest("Ler livro", "Ler 20 páginas", userId);

        UserEntity user = UserEntity.builder()
                .id(userId)
                .name("Raul")
                .email("raul@email.com")
                .build();

        TaskEntity mockedSavedTask = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Ler livro")
                .status(TaskStatus.PENDING)
                .user(user)
                .build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(mockedSavedTask);

        //Act

        TaskResponse response = taskService.createTask(request);

        //assert
        assertNotNull(response);
        assertEquals("Ler livro", response.title());
        assertEquals(TaskStatus.PENDING, response.status());
        verify(userRepository).findById(userId);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void shouldThrowWhenUserDoesntExists(){
        //arrange
        UUID userId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest("Ler livro", "Ler 20 páginas", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //act & assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(request);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should update status and set concludedAt when task can be completed")
    void shouldUpdateStatusAndSetConcludedAtWhenTaskCanBeCompleted(){
        //arrang
        UUID taskId = UUID.randomUUID();
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(subtaskRepository.existsByTaskIdAndStatusNot(taskId, TaskStatus.COMPLETED)).thenReturn(false);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

        //act
        TaskResponse response = taskService.updateTaskStatus(taskId, request);

        //assert

        assertEquals(TaskStatus.COMPLETED, response.status());
        assertNotNull(response.concludedAt());
        verify(subtaskRepository).existsByTaskIdAndStatusNot(taskId, TaskStatus.COMPLETED);
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when task has pending subtasks")
    void shouldThrowBusinessRuleExceptionWhenTaskHasPendingSubtasks(){

        //arrneg
        UUID taskId = UUID.randomUUID();
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .status(TaskStatus.PENDING)
                .build();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(subtaskRepository.existsByTaskIdAndStatusNot(taskId, TaskStatus.COMPLETED)).thenReturn(true);

        // act assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            taskService.updateTaskStatus(taskId, request);
        });

        assertEquals("Cannot complete a task with pending subtasks.", exception.getMessage());
        verify(subtaskRepository).existsByTaskIdAndStatusNot(taskId, TaskStatus.COMPLETED);
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should clear ConcludedAt when status is not completed")
    void shouldClearConcludedAtWhenStatusIsNotCompleted(){
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .status(TaskStatus.COMPLETED)
                .concludedAt(LocalDateTime.now())
                .build();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.PENDING);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

        // Act
        TaskResponse response = taskService.updateTaskStatus(taskId, request);

        // Assert
        assertEquals(TaskStatus.PENDING, response.status());
        assertNull(response.concludedAt());
        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task does not exist")
    void shouldThrowResourceNotFoundExceptionWhenTaskDoesNotExist(){
        //arrange
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        //act and assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTaskStatus(taskId, request);
        });

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository).findById(taskId);
        verify(taskRepository,never()).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Should list task with filters")
    void shoulListTasksWithFilters(){
        //arrange
        UUID userId = UUID.randomUUID();
        TaskStatus status = TaskStatus.PENDING;
        PageRequest pageable = PageRequest.of(0, 10);

        TaskEntity task = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Tarefa 1")
                .status(status)
                .build();

        PageImpl<TaskEntity> page = new PageImpl<>(List.of(task));
        when(taskRepository.findTasksWithFilters(status, userId, pageable)).thenReturn(page);

        //actf

        Page<TaskResponse> response = taskService.listTasks(status, userId, pageable);

        //assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Tarefa 1", response.getContent().get(0).title());
        verify(taskRepository).findTasksWithFilters(status, userId, pageable);
    }


}