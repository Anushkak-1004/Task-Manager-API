# Requirements Document

## Introduction

The Task Manager is a full-stack application that enables users to manage tasks through a web interface backed by a RESTful API. The system consists of a Spring Boot backend service providing full CRUD (Create, Read, Update, Delete) operations for tasks, with support for filtering, status tracking, priority management, and due date handling, and a vanilla JavaScript frontend providing an intuitive user interface. This application demonstrates core full-stack development concepts including REST API design, data persistence, layered architecture, and modern frontend development.

## Glossary

- **Task Manager API**: The backend REST service that manages task operations
- **Task Manager Frontend**: The web-based user interface for interacting with tasks
- **Task**: A work item with properties including identifier, title, description, status, priority, due date, and creation timestamp
- **CRUD Operations**: Create, Read, Update, and Delete operations on tasks
- **REST Endpoint**: An HTTP API endpoint following REST architectural principles
- **Status**: The current state of a task (TODO, IN_PROGRESS, or DONE)
- **Priority**: The importance level of a task (LOW, MEDIUM, or HIGH)
- **Filter**: Query parameters used to narrow down task retrieval results
- **SPA**: Single Page Application - a web application that loads a single HTML page and dynamically updates content

## Requirements

### Requirement 1

**User Story:** As a user, I want to create new tasks with all required properties, so that I can track work items I need to complete.

#### Acceptance Criteria

1. WHEN a user sends a POST request to /api/tasks with valid task data THEN the Task Manager API SHALL create a new task with a unique identifier and return the created task with HTTP status 201
2. WHEN a task is created THEN the Task Manager API SHALL automatically set the createdAt timestamp to the current date and time
3. WHEN a user provides title, description, status, priority, and dueDate in the request body THEN the Task Manager API SHALL store all provided fields in the database
4. IF a user sends a POST request with missing required fields (title or status) THEN the Task Manager API SHALL reject the request and return HTTP status 400 with error details
5. IF a user sends a POST request with invalid enum values for status or priority THEN the Task Manager API SHALL reject the request and return HTTP status 400 with error details

### Requirement 2

**User Story:** As a user, I want to retrieve all tasks with optional filtering, so that I can view and manage my task list effectively.

#### Acceptance Criteria

1. WHEN a user sends a GET request to /api/tasks without filters THEN the Task Manager API SHALL return all tasks with HTTP status 200
2. WHERE a status filter is provided, WHEN a user sends a GET request to /api/tasks THEN the Task Manager API SHALL return only tasks matching the specified status
3. WHERE a priority filter is provided, WHEN a user sends a GET request to /api/tasks THEN the Task Manager API SHALL return only tasks matching the specified priority
4. WHERE a dueBefore filter is provided, WHEN a user sends a GET request to /api/tasks THEN the Task Manager API SHALL return only tasks with due dates before the specified date
5. WHERE multiple filters are provided, WHEN a user sends a GET request to /api/tasks THEN the Task Manager API SHALL return only tasks matching all specified filter criteria

### Requirement 3

**User Story:** As a user, I want to retrieve a specific task by its identifier, so that I can view detailed information about a single task.

#### Acceptance Criteria

1. WHEN a user sends a GET request to /api/tasks/{id} with a valid task identifier THEN the Task Manager API SHALL return the task with HTTP status 200
2. IF a user sends a GET request to /api/tasks/{id} with a non-existent identifier THEN the Task Manager API SHALL return HTTP status 404 with an error message

### Requirement 4

**User Story:** As a user, I want to update existing tasks, so that I can modify task details as work progresses.

#### Acceptance Criteria

1. WHEN a user sends a PUT request to /api/tasks/{id} with valid update data THEN the Task Manager API SHALL update the specified task and return the updated task with HTTP status 200
2. WHEN a task is updated THEN the Task Manager API SHALL preserve the original createdAt timestamp
3. IF a user sends a PUT request to /api/tasks/{id} with a non-existent identifier THEN the Task Manager API SHALL return HTTP status 404 with an error message
4. IF a user sends a PUT request with invalid data THEN the Task Manager API SHALL reject the request and return HTTP status 400 with error details
5. WHEN a user updates a task THEN the Task Manager API SHALL allow modification of title, description, status, priority, and dueDate fields

### Requirement 5

**User Story:** As a user, I want to delete tasks, so that I can remove completed or cancelled work items from my list.

#### Acceptance Criteria

1. WHEN a user sends a DELETE request to /api/tasks/{id} with a valid task identifier THEN the Task Manager API SHALL remove the task from the database and return HTTP status 204
2. IF a user sends a DELETE request to /api/tasks/{id} with a non-existent identifier THEN the Task Manager API SHALL return HTTP status 404 with an error message
3. WHEN a task is deleted THEN the Task Manager API SHALL ensure the task is permanently removed and cannot be retrieved

### Requirement 6

**User Story:** As a developer, I want the API to follow REST principles and return appropriate HTTP status codes, so that client applications can handle responses correctly.

#### Acceptance Criteria

1. WHEN an operation succeeds THEN the Task Manager API SHALL return appropriate success status codes (200 for retrieval/update, 201 for creation, 204 for deletion)
2. WHEN validation fails THEN the Task Manager API SHALL return HTTP status 400 with descriptive error messages
3. WHEN a resource is not found THEN the Task Manager API SHALL return HTTP status 404 with an appropriate error message
4. WHEN an internal error occurs THEN the Task Manager API SHALL return HTTP status 500 with a generic error message
5. WHEN the API returns error responses THEN the Task Manager API SHALL include consistent error response structure with message and timestamp

### Requirement 7

**User Story:** As a developer, I want the application to use a layered architecture, so that the codebase is maintainable and follows best practices.

#### Acceptance Criteria

1. WHEN the application is structured THEN the Task Manager API SHALL separate concerns into controller, service, repository, and entity layers
2. WHEN handling requests THEN the Task Manager API SHALL process business logic in the service layer, not in controllers
3. WHEN accessing data THEN the Task Manager API SHALL use repository interfaces that extend Spring Data JPA repositories
4. WHEN representing data THEN the Task Manager API SHALL use JPA entities for database mapping
5. WHERE DTOs are used, WHEN transferring data THEN the Task Manager API SHALL use separate DTO classes for request and response objects

### Requirement 8

**User Story:** As a developer, I want the application to use H2 for development and support MySQL or PostgreSQL for production, so that I can develop quickly and deploy reliably.

#### Acceptance Criteria

1. WHEN running in development mode THEN the Task Manager API SHALL use H2 in-memory database for data storage
2. WHEN running in production mode THEN the Task Manager API SHALL support connection to MySQL or PostgreSQL database
3. WHEN the application starts THEN the Task Manager API SHALL automatically create database schema based on JPA entities
4. WHEN using H2 THEN the Task Manager API SHALL enable the H2 console for database inspection during development
5. WHEN database configuration changes THEN the Task Manager API SHALL support profile-based configuration for different environments

### Requirement 9

**User Story:** As a user, I want to access the task manager through a web interface, so that I can manage my tasks without using API tools.

#### Acceptance Criteria

1. WHEN a user opens the web application THEN the Task Manager Frontend SHALL display a form to create new tasks with inputs for title, description, status, priority, and due date
2. WHEN the page loads THEN the Task Manager Frontend SHALL automatically fetch and display all existing tasks from the API
3. WHEN a user submits the create task form with valid data THEN the Task Manager Frontend SHALL send a POST request to the API and display the newly created task
4. WHEN a user submits the form with invalid data THEN the Task Manager Frontend SHALL display appropriate validation error messages
5. WHEN tasks are displayed THEN the Task Manager Frontend SHALL show all task properties including title, description, status, priority, due date, and creation timestamp

### Requirement 10

**User Story:** As a user, I want to update and delete tasks from the web interface, so that I can manage my task list efficiently.

#### Acceptance Criteria

1. WHEN a user clicks a "Mark as Done" button on a task THEN the Task Manager Frontend SHALL send a PUT request to update the task status to DONE and refresh the task list
2. WHEN a user clicks a "Delete" button on a task THEN the Task Manager Frontend SHALL prompt for confirmation before sending a DELETE request
3. WHEN a task is successfully deleted THEN the Task Manager Frontend SHALL remove the task from the display and show a success message
4. WHEN a task is already marked as DONE THEN the Task Manager Frontend SHALL not display the "Mark as Done" button for that task
5. WHEN any task operation completes THEN the Task Manager Frontend SHALL automatically refresh the task list to show current data

### Requirement 11

**User Story:** As a user, I want clear visual feedback on task operations, so that I understand the status of my actions and any errors that occur.

#### Acceptance Criteria

1. WHEN a task operation succeeds THEN the Task Manager Frontend SHALL display a success message that automatically dismisses after 3 seconds
2. WHEN a task operation fails THEN the Task Manager Frontend SHALL display an error message with details that automatically dismisses after 5 seconds
3. WHEN tasks are being loaded THEN the Task Manager Frontend SHALL display a loading indicator
4. WHEN no tasks exist THEN the Task Manager Frontend SHALL display an empty state message encouraging users to create their first task
5. WHEN displaying tasks THEN the Task Manager Frontend SHALL use color-coded badges to visually distinguish between different statuses and priorities

### Requirement 12

**User Story:** As a user, I want the web interface to be responsive and visually appealing, so that I can use it comfortably on different devices.

#### Acceptance Criteria

1. WHEN the application is viewed on desktop THEN the Task Manager Frontend SHALL display tasks in a card-based layout with proper spacing
2. WHEN the application is viewed on mobile devices THEN the Task Manager Frontend SHALL adapt the layout to fit smaller screens
3. WHEN a user hovers over interactive elements THEN the Task Manager Frontend SHALL provide visual feedback through hover states
4. WHEN tasks are displayed THEN the Task Manager Frontend SHALL use consistent styling with a modern color scheme and typography
5. WHEN new content appears THEN the Task Manager Frontend SHALL use smooth animations for a polished user experience

### Requirement 13

**User Story:** As a developer, I want the frontend to handle API communication securely and reliably, so that the application is robust and maintainable.

#### Acceptance Criteria

1. WHEN making API requests THEN the Task Manager Frontend SHALL include proper Content-Type headers set to application/json
2. WHEN receiving API responses THEN the Task Manager Frontend SHALL handle both success and error cases appropriately
3. WHEN displaying user-generated content THEN the Task Manager Frontend SHALL escape HTML to prevent XSS attacks
4. WHEN the API is unavailable THEN the Task Manager Frontend SHALL display user-friendly error messages instead of technical errors
5. WHEN the frontend code is structured THEN the Task Manager Frontend SHALL separate concerns with distinct functions for API calls, rendering, and event handling
