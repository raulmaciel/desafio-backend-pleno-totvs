package dev.raul.totvs.taskmanager.service;

import dev.raul.totvs.taskmanager.controller.dto.request.CreateUserRequest;
import dev.raul.totvs.taskmanager.controller.dto.response.UserResponse;
import dev.raul.totvs.taskmanager.entity.UserEntity;
import dev.raul.totvs.taskmanager.exception.EmailAlreadyExistsException;
import dev.raul.totvs.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        if (userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserEntity userEntity = request.toEntity();
        UserEntity savedUser = userRepository.save(userEntity);

        return UserResponse.fromEntity(savedUser);
    }
}
