# Design Document

## Overview

The Task Manager is a full-stack application consisting of a Spring Boot 3.x REST API backend and a vanilla JavaScript frontend. The backend provides RESTful endpoints for managing tasks with a layered architecture pattern: controllers handle HTTP requests, services contain business logic, repositories manage data persistence, and entities represent the domain model. The frontend is a Single Page Application (SPA) that provides an intuitive web interface for task management. The API uses Spring Data JPA for database operations and supports both H2 (development) and MySQL/PostgreSQL (production) databases.

## Architecture

The application follows a client-server architecture with clear separation between frontend and backend:

### Backend Architecture (Three-Tier Layered)

1. **Presentation Layer (Controller)**: REST controllers that handle HTTP requests and responses
2. **Business Logic Layer (Service)**: Services that implement business rules and orchestrate operations
3. **Data Access Layer (Repository)**: Spring Data JPA repositories for database operations
4. **Domain Layer (Entity/Model)**: JPA entities representing the data model

### Frontend Architecture (SPA)

1. **Presentation Layer (HTML/CSS)**: User interface components and styling
2. **Application Layer (JavaScript)**: Business logic, API communication, and DOM manipulation
3. **State Management**: Client-side task state synchronized with backend via REST API

### Technology Stack

**Backend**:
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Dependencies**:
  - Spring Web (REST controllers)
  - Spring Data JPA (database access)
  - H2 Database (development)
  - MySQL or PostgreSQL Driver (production)
  - Spring Boot Validation (input validation)
- **Build Tool**: Maven
- **Database**: H2 (dev), MySQL or PostgreSQL (prod)

**Frontend**:
- **HTML5**: Semantic markup and structure
- **CSS3**: Modern styling with gradients, animations, and responsive design
- **Vanilla JavaScript (ES6+)**: No frameworks, pure JavaScript for API communication and DOM manipulation
- **Fetch API**: For HTTP requests to backend
- **No Build Tools**: Direct browser execution without compilation

### Project Structure

**Backend**:
```
com.taskmanager.api
├── controller
│   └── TaskController.java
├── service
│   └── TaskService.java
├── repository
│   └── TaskRepository.java
├── model
│   ├── Task.java
│   ├── TaskStatus.java (enum)
│   └── TaskPriority.java (enum)
├── dto
│   ├── TaskRequestDto.java
│   ├── TaskResponseDto.java
│   └── ErrorResponseDto.java
└── exception
    ├── TaskNotFoundException.java
    └── GlobalExceptionHandler.java
```

**Frontend**:
```
frontend/
├── index.html      # Main HTML structure and form
├── styles.css      # All styling and responsive design
└── app.js          # API communication and DOM manipulation
```

## Components and Interfaces

### 1. Task Entity

The core domain model representing a task:

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    
    private LocalDate dueDate;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### 2. Enums

**TaskStatus**:
- TODO
- IN_PROGRESS
- DONE

**TaskPriority**:
- LOW
- MEDIUM
- HIGH

### 3. TaskRepository

Spring Data JPA repository interface:

```java
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(TaskPriority priority);
    List<Task> findByDueDateBefore(LocalDate date);
    // Custom query methods for combined filters
}
```

### 4. TaskService

Business logic layer:

```java
@Service
public class TaskService {
    public Task createTask(TaskRequestDto request);
    public List<Task> getAllTasks(TaskStatus status, TaskPriority priority, LocalDate dueBefore);
    public Task getTaskById(Long id);
    public Task updateTask(Long id, TaskRequestDto request);
    public void deleteTask(Long id);
}
```

### 5. TaskController

REST API endpoints:

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto request);
    
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
        @RequestParam(required = false) TaskStatus status,
        @RequestParam(required = false) TaskPriority priority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore
    );
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id);
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
        @PathVariable Long id,
        @Valid @RequestBody TaskRequestDto request
    );
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id);
}
```

### 6. DTOs

**TaskRequestDto**: Used for creating and updating tasks
```java
public class TaskRequestDto {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Status is required")
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
}
```

**TaskResponseDto**: Used for returning task data
```java
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}
```

**ErrorResponseDto**: Consistent error response structure
```java
public class ErrorResponseDto {
    private String message;
    private LocalDateTime timestamp;
    private int status;
}
```

### 7. Exception Handling

**TaskNotFoundException**: Custom exception for missing tasks

**GlobalExceptionHandler**: Centralized exception handling using @ControllerAdvice
- Handles TaskNotFoundException → 404
- Handles MethodArgumentNotValidException → 400
- Handles generic exceptions → 500

## Frontend Components and Architecture

### 1. HTML Structure (index.html)

The frontend is a single-page application with the following key sections:

```html
<div class="container">
  <header>                    <!-- Application title and branding -->
  <div id="errorMessage">     <!-- Error notification area -->
  <div id="successMessage">   <!-- Success notification area -->
  
  <section class="form-section">
    <form id="taskForm">      <!-- Task creation form -->
      - Title input (required)
      - Description textarea (optional)
      - Status dropdown (required)
      - Priority dropdown (optional)
      - Due date input (optional)
      - Submit button
    </form>
  </section>
  
  <section class="tasks-section">
    <div id="tasksList">      <!-- Dynamic task list container -->
  </section>
</div>
```

### 2. CSS Architecture (styles.css)

**Design System**:
- **Color Scheme**: Purple gradient theme (#667eea to #764ba2)
- **Typography**: System font stack for native appearance
- **Spacing**: Consistent 8px grid system
- **Animations**: Smooth transitions and fade-in effects

**Component Styles**:
- **Form Components**: Modern input fields with focus states
- **Task Cards**: Elevated cards with hover effects
- **Badges**: Color-coded status and priority indicators
  - Status: TODO (yellow), IN_PROGRESS (blue), DONE (green)
  - Priority: LOW (light blue), MEDIUM (yellow), HIGH (red)
- **Buttons**: Primary (gradient), Success (green), Danger (red)
- **Messages**: Error (red border), Success (green border)

**Responsive Design**:
- Desktop: Multi-column layout with cards
- Mobile: Single-column stacked layout
- Breakpoint: 768px

### 3. JavaScript Architecture (app.js)

**Configuration**:
```javascript
const API_BASE_URL = 'http://localhost:8080/api/tasks';
```

**Core Functions**:

**API Communication**:
- `loadTasks()`: GET /api/tasks - Fetch and render all tasks
- `createTask()`: POST /api/tasks - Create new task from form
- `updateTask(id, task)`: PUT /api/tasks/{id} - Update task status to DONE
- `deleteTask(id, title)`: DELETE /api/tasks/{id} - Delete task with confirmation

**UI Rendering**:
- `renderTasks(tasks)`: Render task list or empty state
- `createTaskCard(task)`: Generate HTML for single task card
- `updateTaskCount(count)`: Update task counter badge

**User Feedback**:
- `showError(message)`: Display error notification (5s auto-dismiss)
- `showSuccess(message)`: Display success notification (3s auto-dismiss)
- `showLoading(show)`: Toggle loading spinner

**Utility Functions**:
- `formatStatus(status)`: Convert enum to display text
- `formatPriority(priority)`: Convert enum to display text
- `formatDate(dateString)`: Format YYYY-MM-DD to readable date
- `formatDateTime(isoString)`: Format ISO datetime to readable format
- `escapeHtml(text)`: Prevent XSS attacks by escaping HTML

**Event Handling**:
- Form submission: Validates and creates task
- Mark as Done button: Updates task status
- Delete button: Confirms and deletes task
- Page load: Automatically fetches tasks

### 4. API Integration

**Request Headers**:
All API requests include:
```javascript
headers: {
  'Content-Type': 'application/json'
}
```

**Request/Response Flow**:

1. **Create Task**:
   - User fills form → Validate → POST to API → Show success → Refresh list

2. **Load Tasks**:
   - Page load → GET from API → Render cards → Update count

3. **Update Task**:
   - Click "Mark as Done" → PUT to API → Show success → Refresh list

4. **Delete Task**:
   - Click "Delete" → Confirm → DELETE to API → Show success → Refresh list

**Error Handling**:
- Network errors: Display user-friendly message
- API errors: Parse error response and display message
- Validation errors: Show inline form validation
- All errors logged to console for debugging

### 5. Security Considerations

**XSS Prevention**:
- All user-generated content is escaped using `escapeHtml()` before rendering
- Uses `textContent` instead of `innerHTML` where possible
- No `eval()` or dynamic script execution

**CORS**:
- Backend must enable CORS for frontend origin
- Frontend assumes backend handles CORS headers

**Input Validation**:
- Client-side validation for required fields
- Server-side validation is primary defense
- Frontend validation improves UX only

### 6. User Experience Features

**Visual Feedback**:
- Loading spinner during API calls
- Success/error messages with auto-dismiss
- Hover states on all interactive elements
- Smooth animations for content changes

**Empty States**:
- Friendly message when no tasks exist
- Encourages user to create first task

**Confirmation Dialogs**:
- Delete operations require confirmation
- Prevents accidental data loss

**Responsive Behavior**:
- Adapts to screen size automatically
- Touch-friendly on mobile devices
- Maintains usability across devices

## Data Models

### Task Entity Schema

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key, Auto-increment | Unique task identifier |
| title | String | Not null, Not blank | Task title |
| description | String | Nullable | Detailed task description |
| status | TaskStatus (enum) | Not null | Current status (TODO/IN_PROGRESS/DONE) |
| priority | TaskPriority (enum) | Nullable | Priority level (LOW/MEDIUM/HIGH) |
| dueDate | LocalDate | Nullable | Task due date |
| createdAt | LocalDateTime | Not null, Not updatable | Task creation timestamp |

### Database Configuration

**Development (H2)**:
```properties
spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

**Production (MySQL example)**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskdb
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property 1: Task creation round-trip
*For any* valid task data (with required title and status), creating a task via POST /api/tasks and then retrieving it via GET /api/tasks/{id} should return a task with all the same field values, a unique ID, a createdAt timestamp within a few seconds of creation time, and the creation should return HTTP status 201.
**Validates: Requirements 1.1, 1.2, 1.3, 3.1**

### Property 2: Invalid task creation rejection
*For any* task data missing required fields (title or status) or containing invalid enum values, attempting to create the task via POST /api/tasks should return HTTP status 400 with an error response containing a message and timestamp.
**Validates: Requirements 1.4, 1.5, 6.5**

### Property 3: Status filter correctness
*For any* set of tasks with various statuses, when filtering by a specific status via GET /api/tasks?status={status}, all returned tasks should have exactly that status and no tasks with that status should be excluded.
**Validates: Requirements 2.2**

### Property 4: Priority filter correctness
*For any* set of tasks with various priorities, when filtering by a specific priority via GET /api/tasks?priority={priority}, all returned tasks should have exactly that priority and no tasks with that priority should be excluded.
**Validates: Requirements 2.3**

### Property 5: Due date filter correctness
*For any* set of tasks with various due dates, when filtering by a specific date via GET /api/tasks?dueBefore={date}, all returned tasks should have due dates strictly before that date and no tasks with due dates before that date should be excluded.
**Validates: Requirements 2.4**

### Property 6: Combined filter correctness
*For any* set of tasks and any combination of filters (status, priority, dueBefore), all returned tasks should match all specified filter criteria simultaneously.
**Validates: Requirements 2.5**

### Property 7: Task update round-trip
*For any* existing task and any valid update data, updating the task via PUT /api/tasks/{id} should return HTTP status 200, and retrieving the task should show all updated fields with their new values.
**Validates: Requirements 4.1, 4.5**

### Property 8: CreatedAt immutability
*For any* existing task, updating any fields (title, description, status, priority, dueDate) via PUT /api/tasks/{id} should preserve the original createdAt timestamp unchanged.
**Validates: Requirements 4.2**

### Property 9: Invalid update rejection
*For any* task update data that is invalid (missing required fields or invalid enum values), attempting to update via PUT /api/tasks/{id} should return HTTP status 400 with an error response.
**Validates: Requirements 4.4**

### Property 10: Task deletion completeness
*For any* existing task, deleting it via DELETE /api/tasks/{id} should return HTTP status 204, and subsequent GET requests to /api/tasks/{id} should return HTTP status 404.
**Validates: Requirements 5.1, 5.3**

## Error Handling

The application implements centralized exception handling using Spring's @ControllerAdvice:

### Exception Types

1. **TaskNotFoundException**: Thrown when a task with the specified ID doesn't exist
   - HTTP Status: 404 NOT FOUND
   - Response: ErrorResponseDto with message and timestamp

2. **MethodArgumentNotValidException**: Thrown by Spring validation when request data is invalid
   - HTTP Status: 400 BAD REQUEST
   - Response: ErrorResponseDto with validation error details

3. **Generic Exceptions**: Catch-all for unexpected errors
   - HTTP Status: 500 INTERNAL SERVER ERROR
   - Response: ErrorResponseDto with generic error message

### Error Response Format

All error responses follow a consistent structure:
```json
{
  "message": "Error description",
  "timestamp": "2025-12-15T10:30:00",
  "status": 404
}
```

### Validation Rules

- **Title**: Required, not blank
- **Status**: Required, must be valid enum (TODO, IN_PROGRESS, DONE)
- **Priority**: Optional, must be valid enum if provided (LOW, MEDIUM, HIGH)
- **DueDate**: Optional, must be valid date format if provided
- **Description**: Optional

## Testing Strategy

The Task Manager API will use a dual testing approach combining unit tests and property-based tests to ensure comprehensive correctness validation.

### Property-Based Testing

**Framework**: We will use **jqwik** (https://jqwik.net/), a property-based testing framework for Java that integrates with JUnit 5.

**Configuration**: Each property-based test will run a minimum of 100 iterations to ensure thorough coverage of the input space.

**Test Tagging**: Each property-based test must include a comment explicitly referencing the correctness property from this design document using the format:
```java
// Feature: task-manager-api, Property 1: Task creation round-trip
```

**Property Implementation**: Each correctness property listed in this document must be implemented by a single property-based test. These tests will:
- Generate random valid and invalid task data
- Test operations across a wide range of inputs
- Verify universal properties hold for all generated inputs
- Use jqwik's `@Property` annotation and `Arbitrary` generators

**Example Property Test Structure**:
```java
@Property(tries = 100)
// Feature: task-manager-api, Property 1: Task creation round-trip
void taskCreationRoundTrip(@ForAll("validTasks") TaskRequestDto taskData) {
    // Create task
    // Retrieve task
    // Verify all fields match and constraints hold
}
```

### Unit Testing

Unit tests will complement property-based tests by covering:
- Specific examples that demonstrate correct behavior (e.g., creating a task with all fields populated)
- Edge cases identified in the prework (e.g., 404 responses for non-existent IDs)
- Integration between layers (controller → service → repository)
- Specific scenarios like application startup and schema creation

**Framework**: JUnit 5 with Spring Boot Test

**Test Organization**:
- Controller tests: Use MockMvc for HTTP endpoint testing
- Service tests: Test business logic with mocked repositories
- Repository tests: Use @DataJpaTest for database operations
- Integration tests: Use @SpringBootTest for end-to-end scenarios

### Test Coverage Goals

- All REST endpoints must have corresponding tests
- All service methods must be tested
- All validation rules must be verified
- All error handling paths must be covered
- All correctness properties must have property-based tests

### Testing Tools

- **JUnit 5**: Test framework
- **jqwik**: Property-based testing
- **Spring Boot Test**: Integration testing support
- **MockMvc**: REST API testing
- **Mockito**: Mocking framework
- **H2**: In-memory database for tests

## Implementation Notes

### Spring Boot Configuration

The application will use Spring profiles for environment-specific configuration:
- `dev` profile: H2 in-memory database, H2 console enabled
- `prod` profile: MySQL/PostgreSQL, production settings

### CORS Configuration

To enable the frontend to communicate with the backend, CORS must be configured:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "file://")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

### Database Schema Management

Spring Data JPA will handle schema generation:
- Development: `spring.jpa.hibernate.ddl-auto=create-drop` (recreate on each run)
- Production: `spring.jpa.hibernate.ddl-auto=update` (update schema as needed)

### Frontend Deployment

**Development**:
- Open `frontend/index.html` directly in browser
- Or use a simple HTTP server: `python -m http.server 3000`
- Backend must be running on `http://localhost:8080`

**Production**:
- Serve frontend files from static web server (nginx, Apache)
- Update `API_BASE_URL` in `app.js` to production backend URL
- Ensure backend CORS allows production frontend origin

### API Documentation

Consider adding SpringDoc OpenAPI (Swagger) for automatic API documentation generation.

### Future Enhancements

**Backend**:
- Task grouping by project or list
- User authentication and authorization
- Task assignment to users
- Task comments or activity history
- Pagination for task lists
- Sorting options for task retrieval
- WebSocket support for real-time updates

**Frontend**:
- Task filtering UI (by status, priority, date)
- Task search functionality
- Drag-and-drop task reordering
- Inline task editing
- Dark mode toggle
- Keyboard shortcuts
- Offline support with service workers
- Progressive Web App (PWA) capabilities
