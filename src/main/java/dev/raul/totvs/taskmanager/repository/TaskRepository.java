package dev.raul.totvs.taskmanager.repository;

import dev.raul.totvs.taskmanager.entity.TaskEntity;
import dev.raul.totvs.taskmanager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("SELECT t FROM TaskEntity t WHERE "+
            "(:status IS NULL OR t.status = :status) AND "+
            "(:userId IS NULL OR t.user.id = :userId)")
    Page<TaskEntity> findTasksWithFilters(@Param("status")TaskStatus status,
                                          @Param("userId") UUID userId,
                                          Pageable pageable);
}
