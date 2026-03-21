CREATE TABLE subtasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    concluded_at TIMESTAMP,
    task_id UUID NOT NULL,
    CONSTRAINT fk_subtasks_tasks FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);