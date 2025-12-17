# Task Manager Frontend

A clean, modern web interface for the Task Manager REST API built with vanilla HTML, CSS, and JavaScript.

## Features

- ✅ Create tasks with title, description, status, priority, and due date
- ✅ View all tasks in a card-based layout
- ✅ Mark tasks as done with one click
- ✅ Delete tasks with confirmation
- ✅ Color-coded status badges (To Do, In Progress, Done)
- ✅ Priority indicators (Low, Medium, High)
- ✅ Responsive design for desktop and mobile
- ✅ Real-time error and success notifications
- ✅ Loading states and empty state handling
- ✅ XSS protection with HTML escaping

## Files

- **index.html** - Main application structure and form
- **styles.css** - All styling, animations, and responsive design
- **app.js** - API communication, DOM manipulation, and event handling

## Setup

1. Ensure the backend is running on `http://localhost:8080`
2. Open `index.html` in a web browser

Or use a local server:
```bash
# Python 3
python -m http.server 3000

# Node.js
npx http-server -p 3000
```

Then visit `http://localhost:3000`

## API Endpoints Used

- `GET /api/tasks` - Load all tasks
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task (mark as done)
- `DELETE /api/tasks/{id}` - Delete task

## Browser Compatibility

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+

Requires ES6+ support and Fetch API.

## Architecture

### API Communication
All API calls use the Fetch API with proper error handling:
- Automatic retry on network errors
- User-friendly error messages
- Success notifications with auto-dismiss

### State Management
- Tasks loaded on page load
- Automatic refresh after create/update/delete operations
- Task count updated dynamically

### Security
- HTML escaping prevents XSS attacks
- No inline JavaScript or eval()
- Content Security Policy compatible

## Customization

### Change API URL
Edit `app.js`:
```javascript
const API_BASE_URL = 'https://your-api-url.com/api/tasks';
```

### Change Theme Colors
Edit `styles.css`:
```css
/* Main gradient */
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

/* Status colors */
.badge-status.TODO { background-color: #fef3c7; }
.badge-status.IN_PROGRESS { background-color: #dbeafe; }
.badge-status.DONE { background-color: #d1fae5; }
```

## Development

No build process required! Just edit the files and refresh the browser.

### Code Structure

**app.js functions**:
- `loadTasks()` - Fetch and render tasks
- `createTask()` - Handle form submission
- `updateTask()` - Mark task as done
- `deleteTask()` - Delete with confirmation
- `renderTasks()` - Update DOM with task list
- `showError()` / `showSuccess()` - User notifications

## License

MIT
