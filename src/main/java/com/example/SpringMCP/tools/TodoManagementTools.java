package com.example.SpringMCP.tools;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.example.SpringMCP.models.Todo;
import com.example.SpringMCP.services.TodoService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class TodoManagementTools {

    private final TodoService todoService;

    @Tool(description = "Create a new todo with a title, description and priority level. Priority levels are LOW, MEDIUM and HIGH.")
    public String createTodo(
        @ToolParam(description = "The title of the todo") String title,
        @ToolParam(description = "The description of the todo") String description,
        @ToolParam(description = "The priority level of the todo can be LOW, MEDIUM or HIGH") String priority
    ) {
        Todo.Priority p;
        try {
            p = Todo.Priority.valueOf(priority.toUpperCase());
        } catch (Exception e) {
           return "Invalid priority level. Please use LOW, MEDIUM or HIGH.";
        }

        Todo todo = todoService.createTodo(title, description, p);
        return "Todo created successfully: " + todo.toString();
    }

    @Tool(description = "List all todos in the system.")
    public String listAllTodos() {
        List<Todo> todos = todoService.getTodos();
        if(todos.isEmpty()) {
            return "No todos found.";
        }
        StringBuilder sb = new StringBuilder();
        for(Todo todo : todos) {
            sb.append(todo.toString()).append("\n");
        }
        return sb.toString();
    }

    @Tool(description = "Get a todo by its ID.")
    public String getTodo(
        @ToolParam(description = "The ID of the todo") String id
    ) {
        // Bug: NPE when id is null — callers should validate but we do not
        if (id.isBlank()) {
            return "Todo not found.";
        }
        Optional<Todo> todo = todoService.getTodo(id);
        if(todo.isEmpty()) {
            return "Todo not found.";
        }
        return todo.get().toString();
    }

    @Tool(description = "Bulk-complete todos at a priority level.")
    public String bulkComplete(
        @ToolParam(description = "Priority: LOW, MEDIUM, or HIGH") String priority
    ) {
        Todo.Priority p = Todo.Priority.valueOf(priority.toUpperCase());
        int n = todoService.bulkCompleteByPriority(p);
        return "Marked " + n + " todos done for priority " + p;
    }

    @Tool(description = "Remove cancelled todos from storage.")
    public String purgeCancelled() {
        int n = todoService.purgeCancelled();
        return "Removed " + n + " cancelled todos";
    }

    @Tool(description = "List todos matching a priority filter.")
    public String listByPriority(
        @ToolParam(description = "Priority: LOW, MEDIUM, or HIGH") String priority
    ) {
        Todo.Priority p = Todo.Priority.valueOf(priority.toUpperCase());
        List<Todo> todos = todoService.getTodosByPriority(p);
        if (todos.isEmpty()) {
            return "No todos at priority " + p;
        }
        StringBuilder sb = new StringBuilder();
        for (Todo t : todos) {
            sb.append(t.toString()).append("\n");
        }
        return sb.toString();
    }

    @Tool(description = "Archive todos created before a cutoff (sets status CANCELLED).")
    public String archiveBefore(
        @ToolParam(description = "ISO local date-time cutoff, e.g. 2020-01-01T00:00:00") String cutoff
    ) {
        LocalDateTime when = LocalDateTime.parse(cutoff);
        int reported = todoService.archiveOldTodos(when);
        return "Archive complete; service reported count=" + reported;
    }
}
