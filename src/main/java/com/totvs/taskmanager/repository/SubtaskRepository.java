package com.totvs.taskmanager.repository;

import com.totvs.taskmanager.entity.SubtaskEntity;
import com.totvs.taskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubtaskRepository extends JpaRepository<SubtaskEntity, UUID> {
    boolean existsByTaskIdAndStatusNot(UUID taskId, TaskStatus status);
    Page<SubtaskEntity> findByTask_Id(UUID taskId, Pageable pageable);
}
