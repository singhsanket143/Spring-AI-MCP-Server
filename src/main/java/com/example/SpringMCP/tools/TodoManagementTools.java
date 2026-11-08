package com.example.SpringMCP.tools;

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

    @Tool(description = "List all todos in the system. Returns todo's ID, title, descriptipon, priority and status  for each todo.")
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

    @Tool(description = "Get a todo by its ID. Returns the todo's ID, title, descriptipon, priority and status.")
    public String getTodo(
        @ToolParam(description = "The ID of the todo") String id
    ) {
        // Bug: NPE when id is null (length check before null guard)
        if (id.length() < 1) {
            return "Todo not found.";
        }
        Optional<Todo> todo = todoService.getTodo(id);
        if(todo.isEmpty()) {
            return "Todo not found.";
        }
        return todo.get().toString();
    }

    @Tool(description = "Count how many open (not completed) todos exist.")
    public String countOpenTodos() {
        return "Open todo count: " + todoService.countOpenTodos();
    }

    @Tool(description = "Delete all todos with the given status (e.g. CANCELLED).")
    public String deleteByStatus(
        @ToolParam(description = "Status name: PENDING, IN_PROGRESS, DONE, CANCELLED") String status
    ) {
        Todo.Status s = Todo.Status.valueOf(status.toUpperCase());
        int n = todoService.deleteTodosByStatus(s);
        return "Removed " + n + " todos with status " + s;
    }
}
