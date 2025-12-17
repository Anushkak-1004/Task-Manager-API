package com.taskmanager.api.dto;

import com.taskmanager.api.model.Task;

/**
 * Utility class for mapping between Task entities and DTOs.
 * Requirements: 7.5
 */
public class TaskMapper {

    private TaskMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts a TaskRequestDto to a Task entity.
     * 
     * @param dto the TaskRequestDto to convert
     * @return a new Task entity with data from the DTO
     */
    public static Task toEntity(TaskRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        
        return task;
    }

    /**
     * Converts a Task entity to a TaskResponseDto.
     * 
     * @param task the Task entity to convert
     * @return a new TaskResponseDto with data from the entity
     */
    public static TaskResponseDto toResponseDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        
        return dto;
    }

    /**
     * Updates an existing Task entity with data from a TaskRequestDto.
     * This method preserves the id and createdAt fields.
     * 
     * @param task the Task entity to update
     * @param dto the TaskRequestDto containing the new data
     */
    public static void updateEntityFromDto(Task task, TaskRequestDto dto) {
        if (task == null || dto == null) {
            return;
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
    }
}
