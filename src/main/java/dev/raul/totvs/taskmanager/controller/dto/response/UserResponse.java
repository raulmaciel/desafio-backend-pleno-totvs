package dev.raul.totvs.taskmanager.controller.dto.response;

import dev.raul.totvs.taskmanager.entity.UserEntity;
import org.apache.catalina.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email
) {
    public static UserResponse fromEntity(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail()
        );
    }
}
