package com.totvs.taskmanager.service;

import com.totvs.taskmanager.controller.dto.request.CreateUserRequest;
import com.totvs.taskmanager.controller.dto.response.UserResponse;
import com.totvs.taskmanager.entity.UserEntity;
import com.totvs.taskmanager.exception.EmailAlreadyExistsException;
import com.totvs.taskmanager.exception.ResourceNotFoundException;
import com.totvs.taskmanager.repository.UserRepository;
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

    @Test
    @DisplayName("Should return a user when ID exists")
    void shouldReturnUserWhenIdExists(){
        // Arrange
        UUID userId = UUID.randomUUID();
        UserEntity existingEntity = UserEntity.builder()
                .id(userId)
                .name("Raul")
                .email("raul@email.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingEntity));
        // Act
        UserResponse response = userService.getUserById(userId);

        //Assert
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("Raul", response.name());
        assertEquals("raul@email.com", response.email());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void shouldThrowResourceNotFoundExceptionWhenUserDoesNotExist(){
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
    }

}