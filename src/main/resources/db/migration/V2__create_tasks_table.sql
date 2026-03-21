CREATE TABLE tasks(
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    concluded_at TIMESTAMP,
    user_id UUID NOT NULL,
    CONSTRAINT fk_tasks_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);