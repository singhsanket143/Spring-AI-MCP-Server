package com.example.SpringMCP.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.SpringMCP.tools.TodoManagementTools;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider todoManagementToolCallbackProvider(TodoManagementTools todoManagementTools) {
        return MethodToolCallbackProvider.builder()
                    .toolObjects(todoManagementTools)
                    .build();
    }
    
}
