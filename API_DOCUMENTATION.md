# API Documentation - ResolveIT Authentication & Protected Endpoints

## Table of Contents
1. [Authentication Endpoints](#authentication-endpoints)
2. [Protected Endpoints](#protected-endpoints)
3. [Error Responses](#error-responses)
4. [Frontend Usage Examples](#frontend-usage-examples)

---

## Authentication Endpoints

### Base URL: `http://localhost:8008/api/auth`

### 1. Register New User
**POST** `/register`

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Validations:**
- Name: Required, cannot be empty
- Email: Required, must be valid format
- Password: Minimum 6 characters

**Response (201 Created):**
```json
{
  "message": "Registration successful",
  "userId": 1,
  "email": "john@example.com"
}
```

**Error Responses:**
```json
// Email already exists (400 Bad Request)
"Email already registered"

// Invalid input (400 Bad Request)
"Name is required"
"Email is required"
"Password must be at least 6 characters"
```

---

### 2. Login User
**POST** `/login`

**Request:**
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Validations:**
- Email: Required
- Password: Required

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoiam9obkBleGFtcGxlLmNvbSIsInJvbGUiOiJVU0VSIiwic3ViIjoiam9obkBleGFtcGxlLmNvbSIsImlhdCI6MTcwOTcyMDAwMCwiZXhwIjoxNzA5NzIzNjAwfQ.xxx",
  "type": "Bearer",
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Error Responses:**
```json
// Invalid credentials (401 Unauthorized)
"Invalid email or password"

// Inactive user (401 Unauthorized)
"User account is inactive"
```

---

### 3. Validate Token
**GET** `/validate`

**Headers:**
```
Authorization: Bearer <your_jwt_token>
```

**Response (200 OK):**
```
"Token is valid"
```

**Error Response (401 Unauthorized):**
```
"Invalid token"
```

---

### 4. Health Check
**GET** `/health`

**Response (200 OK):**
```
"Auth service is running"
```

---

## Protected Endpoints

### Base URL: `http://localhost:8008/api/protected`

All protected endpoints require JWT authentication via Authorization header.

### Request Format (All Protected Endpoints)
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### 1. Get User Info
**GET** `/user-info`

**Required:** Valid JWT token

**Response (200 OK):**
```json
{
  "email": "john@example.com",
  "role": "ROLE_USER",
  "message": "You are authenticated!"
}
```

**Error Response (401 Unauthorized):**
```json
"Not authenticated"
```

---

### 2. Admin Only Endpoint
**GET** `/admin-only`

**Required:** Valid JWT token with ADMIN role

**Response (200 OK) - If User is ADMIN:**
```json
"Welcome Admin! This is a protected admin-only endpoint."
```

**Error Response (403 Forbidden) - If User is not ADMIN:**
```json
"Access denied. Admin role required."
```

---

## Error Responses

### HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Successful login, token valid |
| 201 | Created | User successfully registered |
| 400 | Bad Request | Invalid input, validation error |
| 401 | Unauthorized | Invalid token, invalid credentials |
| 403 | Forbidden | Insufficient permissions |
| 500 | Internal Server Error | Database error, server error |

### Error Response Format

**Default Error Format:**
```json
"Error message describing what went wrong"
```

**Exception Handling Examples:**

```java
// When email already registered
Response.status(400).entity("Email already registered").build()

// When credentials invalid
Response.status(401).entity("Invalid email or password").build()

// When token invalid
Response.status(401).entity("Invalid token").build()

// When insufficient permissions
Response.status(403).entity("Access denied. Admin role required.").build()
```

---

## Frontend Usage Examples

### Example 1: Register New User

```javascript
import { register } from '../services/authService';

async function handleRegister() {
  const response = await register({
    name: 'Jane Smith',
    email: 'jane@example.com',
    password: 'securePass123'
  });

  if (response.success) {
    console.log('User registered:', response.data);
    // Redirect to login or switch to login form
  } else {
    console.error('Registration failed:', response.message);
  }
}
```

### Example 2: Login User

```javascript
import { login, getCurrentUser } from '../services/authService';

async function handleLogin() {
  const response = await login({
    email: 'jane@example.com',
    password: 'securePass123'
  });

  if (response.success) {
    const user = getCurrentUser();
    console.log('Logged in as:', user.name, user.email);
    // Redirect to dashboard
  } else {
    console.error('Login failed:', response.message);
  }
}
```

### Example 3: Call Protected Endpoint

```javascript
import api from '../services/authService';
import { isAuthenticated } from '../services/authService';

async function fetchUserInfo() {
  if (!isAuthenticated()) {
    console.log('User not authenticated');
    return;
  }

  try {
    const response = await api.get('/protected/user-info');
    console.log('User info:', response.data);
  } catch (error) {
    console.error('Failed to fetch user info:', error);
  }
}
```

### Example 4: Logout User

```javascript
import { logout } from '../services/authService';

function handleLogout() {
  logout();
  // Redirect to home page
  window.location.href = '/';
}
```

### Example 5: Create Protected Route Component

```javascript
import React from 'react';
import { isAuthenticated, getCurrentUser } from '../services/authService';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  const user = getCurrentUser();
  
  return (
    <>
      <div>Welcome, {user.name}!</div>
      {children}
    </>
  );
};

export default ProtectedRoute;
```

### Example 6: Add Role-Based Access

```javascript
import { getCurrentUser } from '../services/authService';

function AdminPanel() {
  const user = getCurrentUser();

  if (user?.role !== 'ADMIN') {
    return <div>Access Denied. Admin role required.</div>;
  }

  return <div>Welcome to Admin Panel</div>;
}
```

---

## Using Auth Service in Components

### Complete Login Component Example

```javascript
import React, { useState } from 'react';
import { login, isAuthenticated } from '../services/authService';

function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const response = await login({ email, password });
    setLoading(false);

    if (response.success) {
      // Redirect to dashboard
      window.location.href = '/dashboard';
    } else {
      setError(response.message);
    }
  };

  if (isAuthenticated()) {
    return <div>Already logged in</div>;
  }

  return (
    <form onSubmit={handleSubmit}>
      <input 
        type="email" 
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required
      />
      <input 
        type="password" 
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        required
      />
      {error && <div className="error">{error}</div>}
      <button type="submit" disabled={loading}>
        {loading ? 'Logging in...' : 'Login'}
      </button>
    </form>
  );
}

export default LoginForm;
```

---

## API Request/Response Flow Diagram

```
REGISTRATION FLOW:
┌─────────────────┐
│  React Frontend │
└────────┬────────┘
         │ POST /register
         │ {name, email, password}
         ▼
┌─────────────────────────┐
│  Spring Boot Backend    │
│  AuthController         │
└────────┬────────────────┘
         │ Validate input
         ▼
┌─────────────────────────┐
│  AuthService            │
│  - Check if email exists│
│  - Encrypt password     │
│  - Save to database     │
└────────┬────────────────┘
         │ Response
         ▼
┌─────────────────┐
│  React Frontend │
│  Show status    │
└─────────────────┘

LOGIN FLOW:
┌─────────────────┐
│  React Frontend │
└────────┬────────┘
         │ POST /login
         │ {email, password}
         ▼
┌─────────────────────────┐
│  Spring Boot Backend    │
│  AuthController         │
└────────┬────────────────┘
         │ Validate input
         ▼
┌─────────────────────────┐
│  AuthService            │
│  - Find user by email   │
│  - Verify password      │
│  - Generate JWT token   │
└────────┬────────────────┘
         │ Response with token
         ▼
┌─────────────────┐
│  React Frontend │
│  Store token    │
│  in localStorage│
└─────────────────┘

PROTECTED API CALL:
┌─────────────────┐
│  React Frontend │
└────────┬────────┘
         │ GET /protected/user-info
         │ Header: Authorization: Bearer <token>
         ▼
┌─────────────────────────┐
│  JWT Filter             │
│  Validates token        │
└────────┬────────────────┘
         │ If valid
         ▼
┌─────────────────────────┐
│  Protected Controller   │
│  Returns user info      │
└────────┬────────────────┘
         │ Response
         ▼
┌─────────────────┐
│  React Frontend │
│  Display data   │
└─────────────────┘
```

---

## Creating Custom Protected Endpoints

### Backend Example: Create Grievance Controller

```java
@RestController
@RequestMapping("/api/grievances")
@CrossOrigin(origins = "*")
public class GrievanceController {

    @Autowired
    private GrievanceService grievanceService;

    /**
     * Get all grievances for current user
     * Requires: Valid JWT token
     */
    @GetMapping("/my-grievances")
    public ResponseEntity<?> getMyGrievances() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        
        return ResponseEntity.ok(grievanceService.getUserGrievances(userEmail));
    }

    /**
     * Create new grievance
     * Requires: Valid JWT token with USER role
     */
    @PostMapping("/create")
    public ResponseEntity<?> createGrievance(@RequestBody GrievanceRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        
        Grievance grievance = grievanceService.createGrievance(userEmail, request);
        return ResponseEntity.status(201).body(grievance);
    }

    /**
     * Admin only: Get all grievances
     * Requires: Valid JWT token with ADMIN role
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllGrievances() {
        boolean isAdmin = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            return ResponseEntity.status(403)
                .body("Admin access required");
        }
        
        return ResponseEntity.ok(grievanceService.getAllGrievances());
    }
}
```

### Frontend Usage: Call Grievance Endpoints

```javascript
import api from '../services/authService';

// Get my grievances
async function fetchMyGrievances() {
  const response = await api.get('/grievances/my-grievances');
  return response.data;
}

// Create new grievance
async function submitGrievance(grievanceData) {
  const response = await api.post('/grievances/create', grievanceData);
  return response.data;
}

// Admin: Get all grievances
async function fetchAllGrievances() {
  const response = await api.get('/grievances/all');
  return response.data;
}
```

---

## Security Best Practices

1. **HTTPS in Production**: Always use HTTPS, not HTTP
2. **Token Refresh**: Implement token refresh endpoint for long-lived sessions
3. **Token Revocation**: Implement logout token blacklist
4. **Rate Limiting**: Add rate limiting to prevent brute force attacks
5. **Input Validation**: Validate all inputs on frontend and backend
6. **CORS**: Configure CORS only for trusted domains
7. **Password Policy**: Enforce strong password requirements
8. **Email Verification**: Send verification email after registration
9. **2FA**: Consider adding two-factor authentication
10. **Audit Logging**: Log all authentication events

---

**End of API Documentation**
