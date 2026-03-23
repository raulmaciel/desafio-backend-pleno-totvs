package dev.raul.totvs.taskmanager.service;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateSubTaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.request.UpdateSubtaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.request.UpdateTaskStatusRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import dev.raul.totvs.taskmanager.entity.SubtaskEntity;
import dev.raul.totvs.taskmanager.entity.TaskEntity;
import dev.raul.totvs.taskmanager.enums.TaskStatus;
import dev.raul.totvs.taskmanager.exception.ResourceNotFoundException;
import dev.raul.totvs.taskmanager.repository.SubtaskRepository;
import dev.raul.totvs.taskmanager.repository.TaskRepository;
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
class SubtaskServiceTest {
    @Mock
    private SubtaskRepository subtaskRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SubtaskService subtaskService;

    @Test
    @DisplayName("Should create subtask successfully")
    void shouldCreateSubtaskSuccessfully(){
        //arrange
        UUID taskId = UUID.randomUUID();
        CreateSubTaskRequest request = new CreateSubTaskRequest("Fazer rascunho", "Páginas de 1 a 5");

        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .title("Estudar livro")
                .build();
        SubtaskEntity mockedSavedSubtask = SubtaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Fazer rascunho")
                .status(TaskStatus.PENDING)
                .task(task)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(subtaskRepository.save(any(SubtaskEntity.class))).thenReturn(mockedSavedSubtask);

        //act

        SubtaskResponse response = subtaskService.createSubtask(taskId, request);

        //Assert

        assertNotNull(response);
        assertEquals("Fazer rascunho", response.title());
        assertEquals(TaskStatus.PENDING, response.status());
        assertEquals(taskId, response.taskId());

        verify(taskRepository).findById(taskId);
        verify(subtaskRepository).save(any(SubtaskEntity.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task does not exist")
    void shouldThrowResourceNotFoundExceptionWhenTaskDoesntExists(){
        UUID taskId = UUID.randomUUID();
        CreateSubTaskRequest request = new CreateSubTaskRequest("Rascunho", "");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subtaskService.createSubtask(taskId, request);
        });

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository).findById(taskId);
        verify(subtaskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update status and fill concludeAt from subtask without change task status")
    void shouldUpdateSubtaskStatusSucessfully(){
        // Arrange
        UUID subtaskId = UUID.randomUUID();
        TaskEntity task = TaskEntity.builder()
                .id(UUID.randomUUID())
                .status(TaskStatus.PENDING)
                .build();

        SubtaskEntity subtask = SubtaskEntity.builder().id(subtaskId).status(TaskStatus.IN_PROGRESS).task(task).build();
        UpdateSubtaskRequest request = new UpdateSubtaskRequest(TaskStatus.COMPLETED);

        when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.of(subtask));
        when(subtaskRepository.save(any(SubtaskEntity.class))).thenReturn(subtask);
        // Act

        SubtaskResponse response = subtaskService.updateSubtaskStatus(subtaskId, request);
        //Assure

        assertEquals(TaskStatus.COMPLETED, response.status());
        assertNotNull(response.concludedAt());
        assertEquals(TaskStatus.PENDING, task.getStatus());

        verify(subtaskRepository).save(subtask);
        verify(taskRepository, never()).save(any());

    }

    @Test
    @DisplayName("Should clear concludedAt field when subtask reopen")
    void shouldClearConcludedAtWhenReopenSubtask(){

        UUID subtaskId = UUID.randomUUID();
        SubtaskEntity subtask = SubtaskEntity.builder()
                .id(subtaskId)
                .status(TaskStatus.COMPLETED)
                .concludedAt(LocalDateTime.now())
                .build();

        UpdateSubtaskRequest request = new UpdateSubtaskRequest(TaskStatus.PENDING);

        when(subtaskRepository.findById(subtaskId)).thenReturn(Optional.of(subtask));
        when(subtaskRepository.save(any(SubtaskEntity.class))).thenReturn(subtask);


        SubtaskResponse response = subtaskService.updateSubtaskStatus(subtaskId, request);

        assertEquals(TaskStatus.PENDING, response.status());
        assertNull(response.concludedAt());
        verify(subtaskRepository).save(subtask);
    }

}