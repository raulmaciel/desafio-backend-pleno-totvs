package dev.raul.totvs.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raul.totvs.taskmanager.controller.dto.request.CreateUserRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.UserResponse;
import dev.raul.totvs.taskmanager.exception.EmailAlreadyExistsException;
import dev.raul.totvs.taskmanager.exception.GlobalExceptionHandler;
import dev.raul.totvs.taskmanager.exception.ResourceNotFoundException;
import dev.raul.totvs.taskmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @Test
    void shouldReturn201WhenUserCreated() throws Exception{
        CreateUserRequest request = new CreateUserRequest(
                "Raul",
                "raul@email.com"
        );

        UserResponse savedUser = new UserResponse(
                UUID.randomUUID(),
                "Raul",
                "raul@email.com");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(savedUser);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenUserNameIsBlank() throws Exception{
        CreateUserRequest request = new CreateUserRequest(
                "",
                "raul@email.com"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any());
    }

    @Test
    void shouldReturn400WhenEmailIsNotValid() throws Exception{
        CreateUserRequest request = new CreateUserRequest(
                "Raul",
                "raul.com"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception{
        CreateUserRequest request = new CreateUserRequest(
                "Raul",
                "raul@email.com"
        );

        when(userService.createUser(any(CreateUserRequest.class))).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
        verify(userService).createUser(any());
    }

    @Test
    void shouldReturn200AndUser() throws Exception{
        UUID userId = UUID.randomUUID();
        UserResponse foundUser = new UserResponse(userId, "Raul", "raul@email.com");

        when(userService.getUserById(userId)).thenReturn(foundUser);

        mockMvc.perform(get("/usuarios/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404NotFound() throws Exception{
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/usuarios/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(any(UUID.class));
    }

}