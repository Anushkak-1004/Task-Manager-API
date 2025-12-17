# jqwik Test Generators

This package contains arbitrary generators for property-based testing using jqwik.

## Overview

The `TaskArbitraries` class provides generators for creating random test data that can be used in property-based tests. These generators ensure comprehensive test coverage by generating a wide variety of valid and invalid inputs.

## Available Generators

### Enum Generators

- **`taskStatus()`**: Generates random `TaskStatus` enum values (TODO, IN_PROGRESS, DONE)
- **`taskPriority()`**: Generates random `TaskPriority` enum values (LOW, MEDIUM, HIGH)
- **`nullableTaskPriority()`**: Generates random `TaskPriority` values with 30% chance of null

### Field Generators

- **`validTitle()`**: Generates non-blank strings suitable for task titles (1-100 characters)
- **`description()`**: Generates optional descriptions (can be null, up to 500 characters)
- **`dueDate()`**: Generates optional dates between 2020-2030 (30% chance of null)

### DTO Generators

- **`validTaskRequest()`**: Generates valid `TaskRequestDto` objects with all required fields
  - Title: non-blank string
  - Status: valid enum value
  - Description, Priority, DueDate: optional fields

- **`invalidTaskRequest()`**: Generates invalid `TaskRequestDto` objects that violate validation constraints
  - Missing title (null)
  - Blank title (empty or whitespace)
  - Missing status (null)

## Usage Example

```java
@Property(tries = 100)
void myPropertyTest(@ForAll("validTaskRequest") TaskRequestDto taskRequest) {
    // Test implementation
}

@Provide
Arbitrary<TaskRequestDto> validTaskRequest() {
    return TaskArbitraries.validTaskRequest();
}
```

## Configuration

All property-based tests should run a minimum of 100 iterations as specified in the design document's testing strategy.

## Requirements

- Requirements: Testing Strategy
- Framework: jqwik 1.8.2
- Integration: JUnit 5
