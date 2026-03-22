package dev.raul.totvs.taskmanager.service;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateTaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.request.UpdateTaskStatusRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.TaskResponse;
import dev.raul.totvs.taskmanager.entity.TaskEntity;
import dev.raul.totvs.taskmanager.entity.UserEntity;
import dev.raul.totvs.taskmanager.enums.TaskStatus;
import dev.raul.totvs.taskmanager.exception.BusinessRuleException;
import dev.raul.totvs.taskmanager.exception.ResourceNotFoundException;
import dev.raul.totvs.taskmanager.repository.SubtaskRepository;
import dev.raul.totvs.taskmanager.repository.TaskRepository;
import dev.raul.totvs.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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


}