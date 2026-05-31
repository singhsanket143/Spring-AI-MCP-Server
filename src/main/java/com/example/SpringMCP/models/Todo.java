package com.example.SpringMCP.models;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Todo {

    public enum Status {
        TODO,
        IN_PROGRESS,
        DONE, 
        CANCELLED
    }
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    private String id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Status status;
    private Priority priority;

    public Todo() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = Status.TODO;
        this.priority = Priority.LOW;
    }

    public Todo(String title, String description, Priority priority) {
        this();
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String toString() {
        return "Todo{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", status=" + status +
            ", priority=" + priority +
            '}';
    }
   
    
}
