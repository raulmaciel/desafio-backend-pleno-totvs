package com.totvs.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totvs.taskmanager.controller.dto.request.CreateSubTaskRequest;
import com.totvs.taskmanager.controller.dto.request.UpdateSubtaskRequest;
import com.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import com.totvs.taskmanager.enums.TaskStatus;
import com.totvs.taskmanager.exception.GlobalExceptionHandler;
import com.totvs.taskmanager.exception.ResourceNotFoundException;
import com.totvs.taskmanager.service.SubtaskService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = SubtaskController.class)
@Import(GlobalExceptionHandler.class)
class SubtaskControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SubtaskService subtaskService;

    @Test
    void shouldReturn201WhenSubtaskIsCreated() throws Exception {
        UUID taskId = UUID.randomUUID();

        CreateSubTaskRequest request = new CreateSubTaskRequest(
                "Subtask 1",
                "Subtask 1 description"
        );

        SubtaskResponse response = new SubtaskResponse(
                UUID.randomUUID(),
                "Subtask 1",
                "Subtask description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                taskId
        );

        when(subtaskService.createSubtask(eq(taskId), any(CreateSubTaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/tarefas/{taskId}/subtarefas", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(subtaskService).createSubtask(eq(taskId), any(CreateSubTaskRequest.class));
    }

    @Test
    void shouldReturn400WhenSubtaskTitleIsBlank() throws Exception {
        UUID taskId = UUID.randomUUID();

        CreateSubTaskRequest request = new CreateSubTaskRequest(
                "",
                "Subtask 1 description"
        );


        mockMvc.perform(post("/tarefas/{taskId}/subtarefas", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(subtaskService, never()).createSubtask(any(), any());
    }

    @Test
    void shouldReturn404WhenTaskDoesNotExist() throws Exception {
        UUID taskId = UUID.randomUUID();

        CreateSubTaskRequest request = new CreateSubTaskRequest(
                "Subtask 1",
                "Subtask 1 description"
        );

        when(subtaskService.createSubtask(eq(taskId), any(CreateSubTaskRequest.class)))
                .thenThrow(new ResourceNotFoundException(("Task not found")));

        mockMvc.perform(post("/tarefas/{taskId}/subtarefas", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(subtaskService).createSubtask(eq(taskId), any(CreateSubTaskRequest.class));
    }

    @Test
    void shouldReturn200WhenSubtaskStatusIsUpdated() throws Exception {
        UUID subtaskId = UUID.randomUUID();

        UpdateSubtaskRequest request = new UpdateSubtaskRequest(
                TaskStatus.COMPLETED
        );

        SubtaskResponse response = new SubtaskResponse(
                subtaskId,
                "Subtask 1",
                "description",
                TaskStatus.COMPLETED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
        );

        when(subtaskService.updateSubtaskStatus(eq(subtaskId), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/subtarefas/{id}/status", subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(subtaskService).updateSubtaskStatus(eq(subtaskId), any());
    }

    @Test
    void shouldReturn400WhenStatusIsNull() throws Exception {
        UUID subtaskId = UUID.randomUUID();

        UpdateSubtaskRequest request = new UpdateSubtaskRequest(null);

        mockMvc.perform(patch("/subtarefas/{id}/status", subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(subtaskService, never()).updateSubtaskStatus(any(), any());
    }

    @Test
    void shouldReturn404WhenSubtaskDoesNotExist() throws Exception {
        UUID subtaskId = UUID.randomUUID();

        UpdateSubtaskRequest request = new UpdateSubtaskRequest(
                TaskStatus.COMPLETED
        );

        when(subtaskService.updateSubtaskStatus(eq(subtaskId), any()))
                .thenThrow(new ResourceNotFoundException("Subtask not found"));

        mockMvc.perform(patch("/subtarefas/{id}/status", subtaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(subtaskService).updateSubtaskStatus(eq(subtaskId), any());
    }

    @Test
    void shouldReturn200WhenListingSubtasksByTask() throws Exception {
        UUID taskId = UUID.randomUUID();

        SubtaskResponse response = new SubtaskResponse(
                UUID.randomUUID(),
                "Subtask 1",
                "Subtask 1 description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                taskId
        );

        Page<SubtaskResponse> page = new PageImpl<>(List.of(response));

        when(subtaskService.listSubtasksByTask(eq(taskId), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/tarefas/{taskId}/subtarefas", taskId))
                .andExpect(status().isOk());

        verify(subtaskService).listSubtasksByTask(eq(taskId), any(Pageable.class));
    }

    @Test
    void shouldReturn200WhenListingSubtasksWithPagination() throws Exception {
        UUID taskId = UUID.randomUUID();

        SubtaskResponse response = new SubtaskResponse(
                UUID.randomUUID(),
                "Subtask 1",
                "Subtask 1 description",
                TaskStatus.PENDING,
                LocalDateTime.now(),
                null,
                taskId
        );

        Page<SubtaskResponse> page = new PageImpl<>(List.of(response));

        when(subtaskService.listSubtasksByTask(eq(taskId), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/tarefas/{taskId}/subtarefas", taskId)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "createdAt,asc")
                )
                .andExpect(status().isOk());


        verify(subtaskService).listSubtasksByTask(eq(taskId), any(Pageable.class));
    }

    @Test
    void shouldReturn404WhenTaskDoesNotExistWhenListingSubtasks() throws Exception {
        UUID taskId = UUID.randomUUID();

        when(subtaskService.listSubtasksByTask(eq(taskId), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(get("/tarefas/{taskId}/subtarefas", taskId))
                .andExpect(status().isNotFound());

        verify(subtaskService).listSubtasksByTask(eq(taskId), any(Pageable.class));
    }
}