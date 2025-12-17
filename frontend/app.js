// API Configuration
const API_BASE_URL = 'http://localhost:8080/api/tasks';

// DOM Elements
const taskForm = document.getElementById('taskForm');
const tasksList = document.getElementById('tasksList');
const loadingSpinner = document.getElementById('loadingSpinner');
const errorMessage = document.getElementById('errorMessage');
const successMessage = document.getElementById('successMessage');
const taskCount = document.getElementById('taskCount');

// Initialize app on page load
document.addEventListener('DOMContentLoaded', () => {
    loadTasks();
    setupFormSubmission();
});

/**
 * Set up form submission handler
 */
function setupFormSubmission() {
    taskForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await createTask();
    });
}

/**
 * Load all tasks from the API
 */
async function loadTasks() {
    try {
        showLoading(true);
        hideMessages();

        const response = await fetch(API_BASE_URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to load tasks: ${response.status} ${response.statusText}`);
        }

        const tasks = await response.json();
        renderTasks(tasks);
        updateTaskCount(tasks.length);
    } catch (error) {
        showError(`Error loading tasks: ${error.message}`);
        tasksList.innerHTML = '';
    } finally {
        showLoading(false);
    }
}

/**
 * Create a new task
 */
async function createTask() {
    try {
        const formData = new FormData(taskForm);
        const taskData = {
            title: formData.get('title').trim(),
            description: formData.get('description').trim() || null,
            status: formData.get('status'),
            priority: formData.get('priority') || null,
            dueDate: formData.get('dueDate') || null
        };

        // Validate required fields
        if (!taskData.title) {
            showError('Title is required');
            return;
        }

        if (!taskData.status) {
            showError('Status is required');
            return;
        }

        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(taskData)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Failed to create task: ${response.status}`);
        }

        const createdTask = await response.json();
        showSuccess(`Task "${createdTask.title}" created successfully!`);
        taskForm.reset();
        await loadTasks();
    } catch (error) {
        showError(`Error creating task: ${error.message}`);
    }
}

/**
 * Update a task (mark as done)
 */
async function updateTask(taskId, currentTask) {
    try {
        const updatedData = {
            title: currentTask.title,
            description: currentTask.description,
            status: 'DONE',
            priority: currentTask.priority,
            dueDate: currentTask.dueDate
        };

        const response = await fetch(`${API_BASE_URL}/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Failed to update task: ${response.status}`);
        }

        showSuccess('Task marked as done!');
        await loadTasks();
    } catch (error) {
        showError(`Error updating task: ${error.message}`);
    }
}

/**
 * Delete a task
 */
async function deleteTask(taskId, taskTitle) {
    if (!confirm(`Are you sure you want to delete "${taskTitle}"?`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${taskId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Failed to delete task: ${response.status}`);
        }

        showSuccess(`Task "${taskTitle}" deleted successfully!`);
        await loadTasks();
    } catch (error) {
        showError(`Error deleting task: ${error.message}`);
    }
}

/**
 * Render tasks in the UI
 */
function renderTasks(tasks) {
    if (!tasks || tasks.length === 0) {
        tasksList.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-text">No tasks yet. Create your first task above!</div>
            </div>
        `;
        return;
    }

    tasksList.innerHTML = tasks.map(task => createTaskCard(task)).join('');
}

/**
 * Create HTML for a single task card
 */
function createTaskCard(task) {
    const statusClass = task.status || 'TODO';
    const priorityClass = task.priority || '';
    const priorityBadge = task.priority 
        ? `<span class="badge badge-priority ${priorityClass}">${formatPriority(task.priority)}</span>`
        : '';
    
    const description = task.description 
        ? `<div class="task-description">${escapeHtml(task.description)}</div>`
        : '';
    
    const dueDate = task.dueDate 
        ? `<div class="task-meta-item"><strong>Due:</strong> ${formatDate(task.dueDate)}</div>`
        : '';
    
    const createdAt = task.createdAt 
        ? `<div class="task-meta-item"><strong>Created:</strong> ${formatDateTime(task.createdAt)}</div>`
        : '';

    const markDoneButton = task.status !== 'DONE'
        ? `<button class="btn btn-success" onclick="updateTask(${task.id}, ${escapeHtml(JSON.stringify(task))})">
               ✓ Mark as Done
           </button>`
        : '';

    return `
        <div class="task-card">
            <div class="task-header">
                <div>
                    <div class="task-title">${escapeHtml(task.title)}</div>
                </div>
                <div class="task-badges">
                    <span class="badge badge-status ${statusClass}">${formatStatus(task.status)}</span>
                    ${priorityBadge}
                </div>
            </div>
            ${description}
            <div class="task-meta">
                ${dueDate}
                ${createdAt}
            </div>
            <div class="task-actions">
                ${markDoneButton}
                <button class="btn btn-danger" onclick="deleteTask(${task.id}, '${escapeHtml(task.title)}')">
                    🗑️ Delete
                </button>
            </div>
        </div>
    `;
}

/**
 * Format status for display
 */
function formatStatus(status) {
    const statusMap = {
        'TODO': 'To Do',
        'IN_PROGRESS': 'In Progress',
        'DONE': 'Done'
    };
    return statusMap[status] || status;
}

/**
 * Format priority for display
 */
function formatPriority(priority) {
    const priorityMap = {
        'LOW': 'Low',
        'MEDIUM': 'Medium',
        'HIGH': 'High'
    };
    return priorityMap[priority] || priority;
}

/**
 * Format date (YYYY-MM-DD)
 */
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

/**
 * Format datetime (ISO string)
 */
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    if (typeof text !== 'string') return text;
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Update task count display
 */
function updateTaskCount(count) {
    taskCount.textContent = `${count} ${count === 1 ? 'task' : 'tasks'}`;
}

/**
 * Show/hide loading spinner
 */
function showLoading(show) {
    loadingSpinner.style.display = show ? 'block' : 'none';
}

/**
 * Show error message
 */
function showError(message) {
    errorMessage.textContent = message;
    errorMessage.classList.remove('hidden');
    successMessage.classList.add('hidden');
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        errorMessage.classList.add('hidden');
    }, 5000);
}

/**
 * Show success message
 */
function showSuccess(message) {
    successMessage.textContent = message;
    successMessage.classList.remove('hidden');
    errorMessage.classList.add('hidden');
    
    // Auto-hide after 3 seconds
    setTimeout(() => {
        successMessage.classList.add('hidden');
    }, 3000);
}

/**
 * Hide all messages
 */
function hideMessages() {
    errorMessage.classList.add('hidden');
    successMessage.classList.add('hidden');
}
