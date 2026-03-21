package dev.raul.totvs.taskmanager.service;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateUserRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.UserResponse;
import dev.raul.totvs.taskmanager.entity.UserEntity;
import dev.raul.totvs.taskmanager.exception.EmailAlreadyExistsException;
import dev.raul.totvs.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully(){
        // Arrange
        CreateUserRequest request = new CreateUserRequest("Raul", "raul@email.com");
        UserEntity savedUserEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Raul")
                .email("raul@email.com")
                .build();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // Act

        UserResponse response = userService.createUser(request);

        //Assert
        assertNotNull(response);
        assertEquals(savedUserEntity.getId(), response.id());
        assertEquals(savedUserEntity.getName(), response.name());
        assertEquals(savedUserEntity.getEmail(), response.email());
        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists and not create a user")
    void shouldThrowExceptionWhenEmailAlreadyExists(){
        // Arrange
        CreateUserRequest request = new CreateUserRequest("Raul", "raul@email.com");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(request);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(request.email());
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}