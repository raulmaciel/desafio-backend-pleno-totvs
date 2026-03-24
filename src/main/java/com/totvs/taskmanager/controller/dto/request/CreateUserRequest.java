package com.totvs.taskmanager.controller.dto.request;

import com.totvs.taskmanager.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new user")
public record CreateUserRequest(

        @Schema(description = "User name", example = "Raul")
        @NotBlank String name,

        @Schema(description = "User email", example = "raul@email.com")
        @NotBlank @Email String email
) {
    public UserEntity toEntity() {
        return UserEntity.builder()
                .name(this.name)
                .email(this.email)
                .build();
    }
}
