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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
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


    @Test
    @DisplayName("Should return paginated subtasks successfully")
    void shouldReturnPaginatedSubtasksSuccessfully() {
        UUID taskId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        TaskEntity task = TaskEntity.builder()
                .id(taskId)
                .build();

        SubtaskEntity subtask = SubtaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Subtask 1")
                .task(task)
                .build();

        Page<SubtaskEntity> subtaskPage = new PageImpl<>(List.of(subtask));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(subtaskRepository.findByTask_Id(taskId, pageable)).thenReturn(subtaskPage);

        //act
        Page<SubtaskResponse> response = subtaskService.listSubtasksByTask(taskId, pageable);

        // assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(taskRepository).findById(taskId);
        verify(subtaskRepository).findByTask_Id(taskId, pageable);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task does not exist")
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            subtaskService.listSubtasksByTask(taskId, pageable);
        });

        assertEquals("Task not found", exception.getMessage());

        verify(taskRepository).findById(taskId);
        verify(subtaskRepository, never()).findByTask_Id(any(), any());

    }


}