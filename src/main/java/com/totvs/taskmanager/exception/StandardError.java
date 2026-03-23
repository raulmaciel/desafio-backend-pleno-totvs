package com.totvs.taskmanager.exception;

import lombok.*;

import java.time.LocalDateTime;


public record StandardError(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path) {
}
