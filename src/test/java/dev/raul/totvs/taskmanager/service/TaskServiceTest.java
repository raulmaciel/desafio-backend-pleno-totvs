package dev.raul.totvs.taskmanager.service;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateTaskRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.TaskResponse;
import dev.raul.totvs.taskmanager.entity.TaskEntity;
import dev.raul.totvs.taskmanager.entity.UserEntity;
import dev.raul.totvs.taskmanager.enums.TaskStatus;
import dev.raul.totvs.taskmanager.exception.ResourceNotFoundException;
import dev.raul.totvs.taskmanager.repository.TaskRepository;
import dev.raul.totvs.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

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

}