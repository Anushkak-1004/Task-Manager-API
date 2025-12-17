package com.taskmanager.api.repository;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskPriority;
import com.taskmanager.api.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Task entity providing CRUD operations and custom queries.
 * Requirements: 2.2, 2.3, 2.4, 7.3
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks with a specific status.
     * Requirements: 2.2
     *
     * @param status the task status to filter by
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find all tasks with a specific priority.
     * Requirements: 2.3
     *
     * @param priority the task priority to filter by
     * @return list of tasks with the specified priority
     */
    List<Task> findByPriority(TaskPriority priority);

    /**
     * Find all tasks with due dates before a specified date.
     * Requirements: 2.4
     *
     * @param date the date to compare against
     * @return list of tasks with due dates before the specified date
     */
    List<Task> findByDueDateBefore(LocalDate date);
}
