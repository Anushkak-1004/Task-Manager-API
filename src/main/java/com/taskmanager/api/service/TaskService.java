package com.taskmanager.api.service;

import com.taskmanager.api.dto.TaskMapper;
import com.taskmanager.api.dto.TaskRequestDto;
import com.taskmanager.api.exception.TaskNotFoundException;
import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskPriority;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for task management business logic.
 * Requirements: 1.1, 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 4.1, 5.1, 7.2
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Creates a new task.
     * Requirements: 1.1, 1.2, 1.3
     *
     * @param request the task data
     * @return the created task
     */
    public Task createTask(TaskRequestDto request) {
        Task task = TaskMapper.toEntity(request);
        return taskRepository.save(task);
    }

    /**
     * Retrieves all tasks with optional filtering by status, priority, and due date.
     * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5
     *
     * @param status the status filter (optional)
     * @param priority the priority filter (optional)
     * @param dueBefore the due date filter (optional)
     * @return list of tasks matching the filter criteria
     */
    @Transactional(readOnly = true)
    public List<Task> getAllTasks(TaskStatus status, TaskPriority priority, LocalDate dueBefore) {
        // If no filters are provided, return all tasks
        if (status == null && priority == null && dueBefore == null) {
            return taskRepository.findAll();
        }

        // Start with all tasks
        List<Task> tasks = taskRepository.findAll();

        // Apply filters
        if (status != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getStatus() == status)
                    .collect(Collectors.toList());
        }

        if (priority != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .collect(Collectors.toList());
        }

        if (dueBefore != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(dueBefore))
                    .collect(Collectors.toList());
        }

        return tasks;
    }

    /**
     * Retrieves a task by its ID.
     * Requirements: 3.1, 3.2
     *
     * @param id the task ID
     * @return the task
     * @throws TaskNotFoundException if the task is not found
     */
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Updates an existing task.
     * Requirements: 4.1, 4.2, 4.3, 4.5
     *
     * @param id the task ID
     * @param request the updated task data
     * @return the updated task
     * @throws TaskNotFoundException if the task is not found
     */
    public Task updateTask(Long id, TaskRequestDto request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        // Update task fields while preserving id and createdAt
        TaskMapper.updateEntityFromDto(task, request);

        return taskRepository.save(task);
    }

    /**
     * Deletes a task by its ID.
     * Requirements: 5.1, 5.2, 5.3
     *
     * @param id the task ID
     * @throws TaskNotFoundException if the task is not found
     */
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskRepository.delete(task);
    }
}
