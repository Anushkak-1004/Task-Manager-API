package com.taskmanager.api.generators;

import com.taskmanager.api.dto.TaskRequestDto;
import com.taskmanager.api.model.TaskPriority;
import com.taskmanager.api.model.TaskStatus;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.time.api.Dates;

import java.time.LocalDate;

/**
 * Arbitrary generators for property-based testing with jqwik.
 * Provides generators for valid and invalid task data.
 * Requirements: Testing Strategy
 */
public class TaskArbitraries {

    /**
     * Generates arbitrary TaskStatus enum values.
     */
    public static Arbitrary<TaskStatus> taskStatus() {
        return Arbitraries.of(TaskStatus.class);
    }

    /**
     * Generates arbitrary TaskPriority enum values.
     */
    public static Arbitrary<TaskPriority> taskPriority() {
        return Arbitraries.of(TaskPriority.class);
    }

    /**
     * Generates arbitrary nullable TaskPriority enum values.
     */
    public static Arbitrary<TaskPriority> nullableTaskPriority() {
        return Arbitraries.of(TaskPriority.class)
                .injectNull(0.3); // 30% chance of null
    }

    /**
     * Generates arbitrary valid task titles (non-blank strings).
     */
    public static Arbitrary<String> validTitle() {
        return Arbitraries.strings()
                .alpha()
                .numeric()
                .withChars(' ', '-', '_')
                .ofMinLength(1)
                .ofMaxLength(100)
                .filter(s -> !s.isBlank()); // Ensure not blank
    }

    /**
     * Generates arbitrary descriptions (can be null or any string).
     */
    public static Arbitrary<String> description() {
        return Arbitraries.strings()
                .alpha()
                .numeric()
                .withChars(' ', '.', ',', '!', '?', '-')
                .ofMaxLength(500)
                .injectNull(0.2); // 20% chance of null
    }

    /**
     * Generates arbitrary due dates (can be null or any reasonable date).
     */
    public static Arbitrary<LocalDate> dueDate() {
        return Dates.dates()
                .between(
                        LocalDate.of(2020, 1, 1),
                        LocalDate.of(2030, 12, 31)
                )
                .injectNull(0.3); // 30% chance of null
    }

    /**
     * Generates arbitrary valid TaskRequestDto objects.
     * All required fields are present and valid.
     */
    public static Arbitrary<TaskRequestDto> validTaskRequest() {
        return Combinators.combine(
                validTitle(),
                description(),
                taskStatus(),
                nullableTaskPriority(),
                dueDate()
        ).as((title, desc, status, priority, due) -> {
            TaskRequestDto dto = new TaskRequestDto();
            dto.setTitle(title);
            dto.setDescription(desc);
            dto.setStatus(status);
            dto.setPriority(priority);
            dto.setDueDate(due);
            return dto;
        });
    }

    /**
     * Generates arbitrary invalid TaskRequestDto objects.
     * These violate validation constraints (missing title or status).
     */
    public static Arbitrary<TaskRequestDto> invalidTaskRequest() {
        return Arbitraries.oneOf(
                invalidTaskRequestMissingTitle(),
                invalidTaskRequestMissingStatus(),
                invalidTaskRequestBlankTitle()
        );
    }

    /**
     * Generates TaskRequestDto with null title (invalid).
     */
    private static Arbitrary<TaskRequestDto> invalidTaskRequestMissingTitle() {
        return Combinators.combine(
                description(),
                taskStatus(),
                nullableTaskPriority(),
                dueDate()
        ).as((desc, status, priority, due) -> {
            TaskRequestDto dto = new TaskRequestDto();
            dto.setTitle(null); // Invalid: null title
            dto.setDescription(desc);
            dto.setStatus(status);
            dto.setPriority(priority);
            dto.setDueDate(due);
            return dto;
        });
    }

    /**
     * Generates TaskRequestDto with blank title (invalid).
     */
    private static Arbitrary<TaskRequestDto> invalidTaskRequestBlankTitle() {
        return Combinators.combine(
                Arbitraries.of("", "   ", "\t", "\n", "  \t  "), // Blank strings
                description(),
                taskStatus(),
                nullableTaskPriority(),
                dueDate()
        ).as((title, desc, status, priority, due) -> {
            TaskRequestDto dto = new TaskRequestDto();
            dto.setTitle(title); // Invalid: blank title
            dto.setDescription(desc);
            dto.setStatus(status);
            dto.setPriority(priority);
            dto.setDueDate(due);
            return dto;
        });
    }

    /**
     * Generates TaskRequestDto with null status (invalid).
     */
    private static Arbitrary<TaskRequestDto> invalidTaskRequestMissingStatus() {
        return Combinators.combine(
                validTitle(),
                description(),
                nullableTaskPriority(),
                dueDate()
        ).as((title, desc, priority, due) -> {
            TaskRequestDto dto = new TaskRequestDto();
            dto.setTitle(title);
            dto.setDescription(desc);
            dto.setStatus(null); // Invalid: null status
            dto.setPriority(priority);
            dto.setDueDate(due);
            return dto;
        });
    }
}
