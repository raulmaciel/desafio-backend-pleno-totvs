package dev.raul.totvs.taskmanager.repository;

import dev.raul.totvs.taskmanager.entity.SubtaskEntity;
import dev.raul.totvs.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubtaskRepository extends JpaRepository<SubtaskEntity, UUID> {
    boolean existsByTaskIdAndStatusNot(UUID taskId, TaskStatus status);
}
