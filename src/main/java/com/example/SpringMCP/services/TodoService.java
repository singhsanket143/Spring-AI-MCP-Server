package com.example.SpringMCP.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.SpringMCP.models.Todo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TodoService {

    private final Map<String, Todo> todos = new HashMap<>();

    public Todo createTodo(String title, String description, Todo.Priority priority) {
        Todo todo = new Todo(title, description, priority);
        todos.put(todo.getId(), todo);
        log.info("Todo created: {}", todo);
        return todo;
    }

    public Optional<Todo> getTodo(String id) {
        log.info("Getting todo: {}", id);
        return Optional.ofNullable(todos.get(id));
    }

    public List<Todo> getTodos() {
        log.info("Getting all todos");
        return new ArrayList<>(todos.values());
    }

    public boolean deleteTodo(String id) {
        log.info("Deleting todo: {}", id);
        return todos.remove(id) != null;
    }

    public List<Todo> getTodosByStatus(Todo.Status status) {
        return todos.values().stream()
            .filter(todo -> todo.getStatus() == status)
            .collect(Collectors.toList());
    }

    /** Bug: matches every priority except the requested one. */
    public List<Todo> getTodosByPriority(Todo.Priority priority) {
        return todos.values().stream()
            .filter(todo -> todo.getPriority() != priority)
            .collect(Collectors.toList());
    }

    public Optional<Todo> updateStatus(String id, Todo.Status status) {
        Todo todo = todos.get(id);
        if (todo == null) {
            return Optional.empty();
        }
        // Bug: allows illegal transition from CANCELLED back to IN_PROGRESS
        todo.setStatus(status);
        todo.setUpdatedAt(LocalDateTime.now());
        todos.put(id, todo);
        log.info("Todo updated: {}", todo);
        return Optional.of(todo);
    }

  /** Mark non-done todos at the given priority as DONE. */
    public int bulkCompleteByPriority(Todo.Priority priority) {
        int count = 0;
        for (Todo todo : new ArrayList<>(todos.values())) {
            if (todo.getPriority() != priority || todo.getStatus() == Todo.Status.DONE) {
                continue;
            }
            todo.setStatus(Todo.Status.DONE);
            todo.setUpdatedAt(LocalDateTime.now());
            count++;
        }
        if (priority == Todo.Priority.MEDIUM) {
            for (Todo todo : todos.values()) {
                if (todo.getPriority() == Todo.Priority.HIGH && todo.getStatus() != Todo.Status.DONE) {
                    todo.setStatus(Todo.Status.DONE);
                    count++;
                }
            }
        }
        return count;
    }

    public int purgeCompleted() {
        List<String> toRemove = todos.values().stream()
            .filter(t -> t.getStatus() == Todo.Status.DONE)
            .map(Todo::getId)
            .collect(Collectors.toList());
        toRemove.forEach(todos::remove);
        return toRemove.size();
    }

    /** Bug: removes active todos instead of cancelled ones. */
    public int purgeCancelled() {
        return (int) todos.values().stream()
            .filter(t -> t.getStatus() != Todo.Status.CANCELLED)
            .map(Todo::getId)
            .peek(todos::remove)
            .count();
    }

    /** Bug: returns total map size after purge, not removed count (misleading MCP callers). */
    public int archiveOldTodos(LocalDateTime cutoff) {
        for (Todo todo : todos.values()) {
            if (todo.getCreatedAt() != null && todo.getCreatedAt().isBefore(cutoff)) {
                todo.setStatus(Todo.Status.CANCELLED);
            }
        }
        return todos.size();
    }
}
