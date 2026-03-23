package com.totvs.taskmanager.entity;

import com.totvs.taskmanager.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name = "subtasks")
public class SubtaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "concluded_at")
    private LocalDateTime concludedAt;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @PrePersist
    public void prePersist(){
        if(this.createdAt == null){
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null){
            this.status = TaskStatus.PENDING;
        }
    }
}
