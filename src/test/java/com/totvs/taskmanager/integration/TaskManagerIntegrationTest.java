package com.totvs.taskmanager.integration;

import com.totvs.taskmanager.controller.dto.request.*;
import com.totvs.taskmanager.controller.dto.response.SubtaskResponse;
import com.totvs.taskmanager.controller.dto.response.TaskResponse;
import com.totvs.taskmanager.controller.dto.response.UserResponse;
import com.totvs.taskmanager.enums.TaskStatus;
import com.totvs.taskmanager.repository.SubtaskRepository;
import com.totvs.taskmanager.repository.TaskRepository;
import com.totvs.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TaskManagerIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SubtaskRepository subtaskRepository;


    @AfterEach
    void tearDown() {
        subtaskRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should execute full task management flow successfully")
    void shouldExecuteFullFlowSuccessfully() {
        //User
        CreateUserRequest createUserRequest = new CreateUserRequest("Raul", "raul@email.com");
        ResponseEntity<UserResponse> userResp = restTemplate.postForEntity("/usuarios", createUserRequest, UserResponse.class);
        assertEquals(HttpStatus.CREATED, userResp.getStatusCode());
        UUID userId = userResp.getBody().id();
        assertNotNull(userId);

        ResponseEntity<UserResponse> getUserResp = restTemplate.getForEntity("/usuarios/" + userId, UserResponse.class);
        assertEquals(HttpStatus.OK, getUserResp.getStatusCode());
        assertEquals("Raul", getUserResp.getBody().name());

        //Task

        CreateTaskRequest taskRequest = new CreateTaskRequest("Ler um livro", "ler um livro de programação", userId);
        ResponseEntity<TaskResponse> taskResponse = restTemplate.postForEntity("/tarefas", taskRequest, TaskResponse.class);
        assertEquals(HttpStatus.CREATED, taskResponse.getStatusCode());
        UUID taskId = taskResponse.getBody().id();
        assertEquals(TaskStatus.PENDING, taskResponse.getBody().status());

        ResponseEntity<String> getTasksResponse = restTemplate.getForEntity("/tarefas?status=PENDING&userId=" + userId, String.class);
        assertEquals(HttpStatus.OK, getTasksResponse.getStatusCode());
        assertTrue(getTasksResponse.getBody().contains("Ler um livro"));

        //Subtask

        CreateSubTaskRequest createSubtaskRequest = new CreateSubTaskRequest("Ler 10 paginas", "capitulo 1");
        ResponseEntity<SubtaskResponse> subtaskResponse = restTemplate.postForEntity("/tarefas/" + taskId + "/subtarefas", createSubtaskRequest, SubtaskResponse.class);
        assertEquals(HttpStatus.CREATED, subtaskResponse.getStatusCode());
        UUID subtaskId = subtaskResponse.getBody().id();

        ResponseEntity<String> getSubtasksResponse = restTemplate.getForEntity("/tarefas/" + taskId + "/subtarefas", String.class);
        assertEquals(HttpStatus.OK, getSubtasksResponse.getStatusCode());
        assertTrue(getSubtasksResponse.getBody().contains("Ler 10 paginas"));



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UpdateTaskStatusRequest concludeTaskParams = new UpdateTaskStatusRequest(TaskStatus.COMPLETED);
        HttpEntity<UpdateTaskStatusRequest> patchRequestEntityTask = new HttpEntity<>(concludeTaskParams, headers);


        ResponseEntity<String> failConcludeTaskResp = restTemplate.exchange("/tarefas/" + taskId + "/status", HttpMethod.PATCH, patchRequestEntityTask, String.class);
        assertEquals(HttpStatus.CONFLICT, failConcludeTaskResp.getStatusCode());


        UpdateSubtaskRequest concludeSubtaskParams = new UpdateSubtaskRequest(TaskStatus.COMPLETED);
        HttpEntity<UpdateSubtaskRequest> patchRequestEntitySub = new HttpEntity<>(concludeSubtaskParams, headers);
        ResponseEntity<SubtaskResponse> doneSubtaskResp = restTemplate.exchange("/subtarefas/" + subtaskId + "/status", HttpMethod.PATCH, patchRequestEntitySub, SubtaskResponse.class);

        assertEquals(HttpStatus.OK, doneSubtaskResp.getStatusCode());
        assertEquals(TaskStatus.COMPLETED, doneSubtaskResp.getBody().status());

        ResponseEntity<TaskResponse> doneTaskResp = restTemplate.exchange("/tarefas/" + taskId + "/status", HttpMethod.PATCH, patchRequestEntityTask, TaskResponse.class);

        assertEquals(HttpStatus.OK, doneTaskResp.getStatusCode());
        assertEquals(TaskStatus.COMPLETED, doneTaskResp.getBody().status());
        assertNotNull(doneTaskResp.getBody().concludedAt());

    }


}
