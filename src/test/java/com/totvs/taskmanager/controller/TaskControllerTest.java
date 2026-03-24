package com.totvs.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totvs.taskmanager.controller.dto.request.CreateTaskRequest;
import com.totvs.taskmanager.controller.dto.request.UpdateTaskStatusRequest;
import com.totvs.taskmanager.controller.dto.response.TaskResponse;
import com.totvs.taskmanager.enums.TaskStatus;
import com.totvs.taskmanager.exception.BusinessRuleException;
import com.totvs.taskmanager.exception.GlobalExceptionHandler;
import com.totvs.taskmanager.exception.ResourceNotFoundException;
import com.totvs.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TaskService taskService;

    @Test
    void shouldReturn201WhenTaskIsCreated() throws Exception {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request = new CreateTaskRequest(
                "Task 1",
                "Task 1 description",
                userId
        );

        TaskResponse response = new TaskResponse(
                UUID.randomUUID(),
                "Task 1",
                "Task 1 description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                null
        );

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        verify(taskService).createTask(any(CreateTaskRequest.class));
    }

    @Test
    void shouldReturn400WhenTaskTitleIsBlank() throws Exception{
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request = new CreateTaskRequest(
                "",
                "Task 1 description",
                userId
        );

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test
    void shouldReturn400WhenUserIdIsNull() throws Exception {
        UUID userId = null;

        CreateTaskRequest request = new CreateTaskRequest(
                "Task 1",
                "Task 1 description",
                null
        );

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());

    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        UUID userId = UUID.randomUUID();

        CreateTaskRequest request = new CreateTaskRequest(
                "Task 1",
                "Task 1 description",
                userId
        );

        when(taskService.createTask(any()))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(taskService).createTask(any());
    }


    @Test
    void shouldReturn200WhenListingTasksWithoutFilters() throws Exception{
        TaskResponse response = new TaskResponse(
                UUID.randomUUID(),
                "Task 1",
                "Task 1 description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                null
        );

        Page<TaskResponse> page = new PageImpl<>(List.of(response));

        when(taskService.listTasks(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isOk());

        verify(taskService).listTasks(any(), any(), any(Pageable.class));
    }

    @Test
    void shouldReturn200WhenListingTasksByStatus() throws Exception {
        TaskResponse response = new TaskResponse(
                UUID.randomUUID(),
                "Task 1",
                "Task 1 description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                null
        );

        Page<TaskResponse> page = new PageImpl<>(List.of(response));

        when(taskService.listTasks(eq(TaskStatus.PENDING), isNull(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tarefas")
                        .param("status", "PENDING"))
                .andExpect(status().isOk());

        verify(taskService).listTasks(eq(TaskStatus.PENDING), isNull(), any(Pageable.class));
    }

    @Test
    void shouldReturn200WhenListingTasksByUser() throws Exception {
        UUID userId = UUID.randomUUID();

        TaskResponse response = new TaskResponse(
                UUID.randomUUID(),
                "Task 1",
                "Task 1 description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                null
        );

        Page<TaskResponse> page = new PageImpl<>(List.of(response));

        when(taskService.listTasks(isNull(), eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tarefas")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk());

        verify(taskService).listTasks(isNull(), eq(userId), any(Pageable.class));
    }

    @Test
    void shouldReturn200WhenListingTasksWithPagination() throws Exception {
        TaskResponse response = new TaskResponse(
                UUID.randomUUID(),
                "Task 1",
                "Task 1 description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                null
        );

        Page<TaskResponse> page = new PageImpl<>(List.of(response));

        when(taskService.listTasks(isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tarefas")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());

        verify(taskService).listTasks(isNull(), isNull(), any(Pageable.class));
    }


    @Test
    void shouldReturn200WhenTaskStatusIsUpdated() throws Exception {
        UUID taskId = UUID.randomUUID();

        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        TaskResponse response = new TaskResponse(
                taskId,
                "Task 1",
                "Task 1 description",
                TaskStatus.COMPLETED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        when(taskService.updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/tarefas/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(taskService).updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class));
    }

    @Test
    void shouldReturn400WhenStatusIsNull() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(null);

        mockMvc.perform(patch("/tarefas/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTaskStatus(any(), any());
    }

    @Test
    void shouldReturn404WhenTaskDoesNotExist() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        when(taskService.updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(patch("/tarefas/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(taskService).updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class));
    }

    @Test
    void shouldReturn409WhenTaskHasPendingSubtasks() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);

        when(taskService.updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class)))
                .thenThrow(new BusinessRuleException("Cannot complete a task with pending subtasks."));

        mockMvc.perform(patch("/tarefas/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(taskService).updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequest.class));

    }
}