package com.taskmanager.api.generators;

import com.taskmanager.api.dto.TaskRequestDto;
import com.taskmanager.api.model.TaskPriority;
import com.taskmanager.api.model.TaskStatus;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Arbitrary;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests to verify the arbitrary generators work correctly.
 * This ensures our test infrastructure is properly set up.
 */
class TaskArbitrariesTest {

    @Provide
    Arbitrary<TaskStatus> taskStatus() {
        return TaskArbitraries.taskStatus();
    }

    @Provide
    Arbitrary<TaskPriority> taskPriority() {
        return TaskArbitraries.taskPriority();
    }

    @Provide
    Arbitrary<TaskRequestDto> validTaskRequest() {
        return TaskArbitraries.validTaskRequest();
    }

    @Provide
    Arbitrary<TaskRequestDto> invalidTaskRequest() {
        return TaskArbitraries.invalidTaskRequest();
    }

    @Property(tries = 100)
    void taskStatusGeneratorProducesValidValues(@ForAll("taskStatus") TaskStatus status) {
        assertThat(status).isNotNull();
        assertThat(status).isIn(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.DONE);
    }

    @Property(tries = 100)
    void taskPriorityGeneratorProducesValidValues(@ForAll("taskPriority") TaskPriority priority) {
        assertThat(priority).isNotNull();
        assertThat(priority).isIn(TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH);
    }

    @Property(tries = 100)
    void validTaskRequestHasRequiredFields(@ForAll("validTaskRequest") TaskRequestDto taskRequest) {
        // Valid task requests must have non-blank title and non-null status
        assertThat(taskRequest).isNotNull();
        assertThat(taskRequest.getTitle()).isNotBlank();
        assertThat(taskRequest.getStatus()).isNotNull();
    }

    @Property(tries = 100)
    void invalidTaskRequestViolatesConstraints(@ForAll("invalidTaskRequest") TaskRequestDto taskRequest) {
        // Invalid task requests must violate at least one constraint
        assertThat(taskRequest).isNotNull();
        
        boolean hasBlankTitle = taskRequest.getTitle() == null || taskRequest.getTitle().isBlank();
        boolean hasMissingStatus = taskRequest.getStatus() == null;
        
        // At least one constraint must be violated
        assertThat(hasBlankTitle || hasMissingStatus).isTrue();
    }
}
