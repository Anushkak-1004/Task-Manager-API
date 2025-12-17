# Implementation Plan

- [x] 1. Set up Spring Boot project structure and dependencies





  - Create Maven project with Spring Boot 3.x
  - Add dependencies: Spring Web, Spring Data JPA, H2, MySQL driver, Validation, jqwik
  - Configure application.properties for H2 development profile
  - Create package structure: controller, service, repository, model, dto, exception
  - _Requirements: 7.1, 8.1, 8.3_

- [x] 2. Implement domain model and enums






  - [x] 2.1 Create TaskStatus enum with values TODO, IN_PROGRESS, DONE

    - _Requirements: 1.3_

  - [x] 2.2 Create TaskPriority enum with values LOW, MEDIUM, HIGH

    - _Requirements: 1.3_

  - [x] 2.3 Create Task entity with JPA annotations

    - Add fields: id, title, description, status, priority, dueDate, createdAt
    - Add @PrePersist method to set createdAt timestamp
    - Add validation annotations (@NotBlank, @NotNull)
    - _Requirements: 1.1, 1.2, 1.3, 4.2_

- [x] 3. Implement data access layer




  - [x] 3.1 Create TaskRepository interface extending JpaRepository


    - Add custom query methods: findByStatus, findByPriority, findByDueDateBefore
    - _Requirements: 2.2, 2.3, 2.4, 7.3_
  - [ ]* 3.2 Write unit tests for repository query methods
    - Test findByStatus returns correct tasks
    - Test findByPriority returns correct tasks
    - Test findByDueDateBefore returns correct tasks
    - Use @DataJpaTest for repository testing
    - _Requirements: 2.2, 2.3, 2.4_

- [x] 4. Implement DTOs and mappers




  - [x] 4.1 Create TaskRequestDto with validation annotations


    - Add fields: title, description, status, priority, dueDate
    - Add @NotBlank and @NotNull validation
    - _Requirements: 1.3, 1.4_
  - [x] 4.2 Create TaskResponseDto for API responses


    - Add all Task fields including id and createdAt
    - _Requirements: 1.1, 3.1_
  - [x] 4.3 Create ErrorResponseDto for error responses


    - Add fields: message, timestamp, status
    - _Requirements: 6.5_
  - [x] 4.4 Create mapper utility methods to convert between Task and DTOs


    - Implement toEntity(TaskRequestDto) → Task
    - Implement toResponseDto(Task) → TaskResponseDto
    - _Requirements: 7.5_

- [-] 5. Implement service layer with business logic


  - [x] 5.1 Create TaskService class with CRUD operations



    - Implement createTask(TaskRequestDto) method
    - Implement getAllTasks(status, priority, dueBefore) with filtering logic
    - Implement getTaskById(Long id) method with exception handling
    - Implement updateTask(Long id, TaskRequestDto) method
    - Implement deleteTask(Long id) method with exception handling
    - _Requirements: 1.1, 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 4.1, 5.1, 7.2_
  - [ ]* 5.2 Write unit tests for TaskService
    - Test createTask with valid data
    - Test getAllTasks with various filter combinations
    - Test getTaskById with valid and invalid IDs
    - Test updateTask with valid data
    - Test deleteTask with valid and invalid IDs
    - Mock TaskRepository for service tests
    - _Requirements: 1.1, 2.1, 2.5, 3.1, 4.1, 5.1_

- [x] 6. Implement exception handling






  - [x] 6.1 Create TaskNotFoundException custom exception

    - _Requirements: 3.2, 4.3, 5.2_

  - [x] 6.2 Create GlobalExceptionHandler with @ControllerAdvice

    - Handle TaskNotFoundException → return 404 with ErrorResponseDto
    - Handle MethodArgumentNotValidException → return 400 with ErrorResponseDto
    - Handle generic Exception → return 500 with ErrorResponseDto
    - _Requirements: 1.4, 1.5, 3.2, 4.3, 4.4, 5.2, 6.2, 6.3, 6.5_

- [x] 7. Implement REST controller layer










  - [x] 7.1 Create TaskController with REST endpoints


    - Implement POST /api/tasks endpoint with @Valid validation
    - Implement GET /api/tasks endpoint with optional query parameters
    - Implement GET /api/tasks/{id} endpoint
    - Implement PUT /api/tasks/{id} endpoint with @Valid validation
    - Implement DELETE /api/tasks/{id} endpoint
    - Return appropriate HTTP status codes (201, 200, 204, 400, 404)
    - _Requirements: 1.1, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 3.2, 4.1, 4.3, 4.4, 5.1, 5.2, 6.1, 6.2, 6.3, 7.1_
  - [ ]* 7.2 Write unit tests for TaskController using MockMvc
    - Test POST /api/tasks with valid and invalid data
    - Test GET /api/tasks with and without filters
    - Test GET /api/tasks/{id} with valid and invalid IDs
    - Test PUT /api/tasks/{id} with valid and invalid data
    - Test DELETE /api/tasks/{id} with valid and invalid IDs
    - Verify HTTP status codes and response bodies
    - Mock TaskService for controller tests
    - _Requirements: 1.1, 1.4, 2.1, 2.5, 3.1, 4.1, 5.1, 6.1, 6.2, 6.3_

- [-] 8. Implement property-based tests for correctness properties


  - [x] 8.1 Set up jqwik test infrastructure



    - Create arbitrary generators for TaskRequestDto with valid data
    - Create arbitrary generators for TaskStatus and TaskPriority enums
    - Create arbitrary generators for invalid task data
    - _Requirements: Testing Strategy_
  - [ ]* 8.2 Write property test for Property 1: Task creation round-trip
    - **Property 1: Task creation round-trip**
    - **Validates: Requirements 1.1, 1.2, 1.3, 3.1**
    - Generate random valid task data, create via POST, retrieve via GET, verify all fields match
  - [ ]* 8.3 Write property test for Property 2: Invalid task creation rejection
    - **Property 2: Invalid task creation rejection**
    - **Validates: Requirements 1.4, 1.5, 6.5**
    - Generate random invalid task data, verify POST returns 400 with error structure
  - [ ]* 8.4 Write property test for Property 3: Status filter correctness
    - **Property 3: Status filter correctness**
    - **Validates: Requirements 2.2**
    - Generate random tasks with various statuses, filter by status, verify all results match
  - [ ]* 8.5 Write property test for Property 4: Priority filter correctness
    - **Property 4: Priority filter correctness**
    - **Validates: Requirements 2.3**
    - Generate random tasks with various priorities, filter by priority, verify all results match
  - [ ]* 8.6 Write property test for Property 5: Due date filter correctness
    - **Property 5: Due date filter correctness**
    - **Validates: Requirements 2.4**
    - Generate random tasks with various due dates, filter by date, verify all results are before date
  - [ ]* 8.7 Write property test for Property 6: Combined filter correctness
    - **Property 6: Combined filter correctness**
    - **Validates: Requirements 2.5**
    - Generate random tasks and filter combinations, verify all results match all filters
  - [ ]* 8.8 Write property test for Property 7: Task update round-trip
    - **Property 7: Task update round-trip**
    - **Validates: Requirements 4.1, 4.5**
    - Generate random tasks and updates, apply via PUT, retrieve via GET, verify updates applied
  - [ ]* 8.9 Write property test for Property 8: CreatedAt immutability
    - **Property 8: CreatedAt immutability**
    - **Validates: Requirements 4.2**
    - Generate random tasks and updates, apply via PUT, verify createdAt unchanged
  - [ ]* 8.10 Write property test for Property 9: Invalid update rejection
    - **Property 9: Invalid update rejection**
    - **Validates: Requirements 4.4**
    - Generate random invalid update data, verify PUT returns 400
  - [ ]* 8.11 Write property test for Property 10: Task deletion completeness
    - **Property 10: Task deletion completeness**
    - **Validates: Requirements 5.1, 5.3**
    - Generate random tasks, delete via DELETE, verify 204 response and subsequent GET returns 404

- [x] 9. Checkpoint - Ensure all tests pass






  - Ensure all tests pass, ask the user if questions arise.

- [x] 10. Configure production database profile






  - [x] 10.1 Create application-prod.properties for MySQL/PostgreSQL

    - Configure datasource URL, username, password (use environment variables)
    - Set hibernate.ddl-auto to update
    - _Requirements: 8.2, 8.5_

  - [x] 10.2 Add profile-specific configuration documentation

    - Document how to run with dev vs prod profiles
    - Document required environment variables for production
    - _Requirements: 8.5_

- [ ]* 11. Create integration tests for end-to-end scenarios
  - Use @SpringBootTest for full application context
  - Test complete workflows: create → retrieve → update → delete
  - Test application startup and schema creation
  - Verify H2 console accessibility in dev mode
  - _Requirements: 2.1, 8.3, 8.4_

- [x] 12. Final checkpoint - Ensure all tests pass








  - Ensure all tests pass, ask the user if questions arise.
