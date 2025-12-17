package com.taskmanager.api.controller;

import com.taskmanager.api.dto.TaskMapper;
import com.taskmanager.api.dto.TaskRequestDto;
import com.taskmanager.api.dto.TaskResponseDto;
import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskPriority;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for task management endpoints.
 * Requirements: 1.1, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 3.2, 4.1, 4.3, 4.4, 5.1, 5.2, 6.1, 6.2, 6.3, 7.1
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a new task.
     * Requirements: 1.1, 1.4, 1.5, 6.1, 6.2
     *
     * @param request the task data
     * @return the created task with HTTP status 201
     */
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto request) {
        Task task = taskService.createTask(request);
        TaskResponseDto response = TaskMapper.toResponseDto(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all tasks with optional filtering.
     * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 6.1
     *
     * @param status the status filter (optional)
     * @param priority the priority filter (optional)
     * @param dueBefore the due date filter (optional)
     * @return list of tasks matching the filter criteria with HTTP status 200
     */
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore) {
        
        List<Task> tasks = taskService.getAllTasks(status, priority, dueBefore);
        List<TaskResponseDto> response = tasks.stream()
                .map(TaskMapper::toResponseDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a specific task by ID.
     * Requirements: 3.1, 3.2, 6.1, 6.3
     *
     * @param id the task ID
     * @return the task with HTTP status 200, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        TaskResponseDto response = TaskMapper.toResponseDto(task);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing task.
     * Requirements: 4.1, 4.3, 4.4, 6.1, 6.2, 6.3
     *
     * @param id the task ID
     * @param request the updated task data
     * @return the updated task with HTTP status 200, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto request) {
        
        Task task = taskService.updateTask(id, request);
        TaskResponseDto response = TaskMapper.toResponseDto(task);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a task by ID.
     * Requirements: 5.1, 5.2, 6.1, 6.3
     *
     * @param id the task ID
     * @return HTTP status 204 on success, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
