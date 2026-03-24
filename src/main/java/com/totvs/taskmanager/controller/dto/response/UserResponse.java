package com.totvs.taskmanager.controller.dto.response;

import com.totvs.taskmanager.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "User response data")
public record UserResponse(
        @Schema(
                description = "Unique identifier of the user",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "User name",
                example = "Raul Maciel"
        )
        String name,

        @Schema(
                description = "User email address",
                example = "raul@email.com"
        )
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
