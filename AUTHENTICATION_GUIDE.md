# ResolveIT Smart Grievance Management System - Authentication Setup

## Overview
This is a full-stack authentication system for the ResolveIT Smart Grievance Management System with JWT-based authentication.

## Tech Stack
- **Frontend**: React (JavaScript, JSX, Hooks)
- **Backend**: Java Spring Boot (Maven)
- **Database**: MySQL
- **Authentication**: JWT (Base64 encoded token with BCrypt password encryption)

---

## Backend Structure

### 📁 Folder Structure
```
src/main/java/com/example/demo/
├── config/
│   ├── JwtAuthenticationFilter.java    # JWT filter for request validation
│   └── SecurityConfig.java             # Spring Security configuration
├── controller/
│   └── AuthController.java             # Authentication endpoints
├── dto/
│   ├── LoginRequest.java               # Login request DTO
│   ├── RegisterRequest.java            # Registration request DTO
│   └── AuthResponse.java               # Authentication response DTO
├── model/
│   └── User.java                       # User entity
├── repository/
│   └── UserRepository.java             # User data access layer
├── service/
│   └── AuthService.java                # Authentication business logic
└── util/
    └── JwtUtil.java                    # JWT token utilities
```

### 🔐 Security Features
- **BCrypt Password Encryption**: All passwords are encrypted using BCrypt before storing in database
- **JWT Token Generation**: Tokens contain userId, email, and role
- **Token Expiration**: Tokens expire after 1 hour (configurable)
- **Base64 Encoding**: JWT secret key is Base64 encoded
- **CORS Configuration**: Configured to allow React frontend requests

### 📡 API Endpoints

#### 1. Register New User
```
POST http://localhost:8008/api/auth/register
Content-Type: application/json

Request Body:
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}

Response (Success - 201 Created):
{
  "message": "Registration successful",
  "userId": 1,
  "email": "john@example.com"
}

Response (Error - 400 Bad Request):
"Email already registered"
```

#### 2. Login User
```
POST http://localhost:8008/api/auth/login
Content-Type: application/json

Request Body:
{
  "email": "john@example.com",
  "password": "password123"
}

Response (Success - 200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}

Response (Error - 401 Unauthorized):
"Invalid email or password"
```

#### 3. Validate Token
```
GET http://localhost:8008/api/auth/validate
Authorization: Bearer <token>

Response (Success - 200 OK):
"Token is valid"

Response (Error - 401 Unauthorized):
"Invalid token"
```

#### 4. Health Check
```
GET http://localhost:8008/api/auth/health

Response (200 OK):
"Auth service is running"
```

---

## Frontend Structure

### 📁 Folder Structure
```
frontend/src/
├── services/
│   └── authService.js          # Authentication API service
└── pages/
    └── Login.jsx               # Login/Register component
```

### 🔧 Authentication Service Functions

#### `register(userData)`
Registers a new user and returns success/error response.

```javascript
import { register } from '../services/authService';

const response = await register({
  name: 'John Doe',
  email: 'john@example.com',
  password: 'password123'
});

if (response.success) {
  console.log('Registration successful');
}
```

#### `login(credentials)`
Authenticates user and stores JWT token in localStorage.

```javascript
import { login } from '../services/authService';

const response = await login({
  email: 'john@example.com',
  password: 'password123'
});

if (response.success) {
  console.log('Login successful', response.data);
}
```

#### `logout()`
Clears all authentication data from localStorage.

```javascript
import { logout } from '../services/authService';
logout();
```

#### `getCurrentUser()`
Gets currently logged-in user data.

```javascript
import { getCurrentUser } from '../services/authService';
const user = getCurrentUser(); // Returns { userId, name, email, role, token }
```

#### `isAuthenticated()`
Checks if user is authenticated.

```javascript
import { isAuthenticated } from '../services/authService';
if (isAuthenticated()) {
  // User is logged in
}
```

### 🔐 Token Storage
- JWT token is stored in `localStorage` with key `token`
- User data is stored separately: `userId`, `userName`, `userEmail`, `userRole`
- Token is automatically added to all API requests via axios interceptor
- Expired tokens trigger automatic logout and redirect to login page

---

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 14+ and npm
- MySQL Server
- Maven

### Backend Setup

1. **Start MySQL Server**
   ```bash
   # Make sure MySQL is running on localhost:3306
   ```

2. **Create Database**
   ```sql
   CREATE DATABASE students_db;
   ```

3. **Update Database Configuration** (if needed)
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/students_db
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```

4. **Install Dependencies**
   ```bash
   cd "c:\Users\Asus\OneDrive\Desktop\rajjo bday\demo"
   mvn clean install
   ```

5. **Run Spring Boot Application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run from IDE:
   - Open `MavennnApplication.java`
   - Click Run

   Server will start on: http://localhost:8008

### Frontend Setup

1. **Navigate to Frontend Directory**
   ```bash
   cd "c:\Users\Asus\OneDrive\Desktop\rajjo bday\demo\frontend"
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Start React Development Server**
   ```bash
   npm start
   ```
   
   App will open on: http://localhost:3000

---

## Database Schema

### Users Table
The `users` table will be automatically created by Hibernate with the following structure:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- BCrypt encrypted
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## Testing the Application

### Test Registration Flow

1. Open http://localhost:3000
2. Click "Login / Register" button
3. Click "Register" toggle
4. Fill in:
   - Name: Test User
   - Email: test@example.com
   - Password: test123
   - Confirm Password: test123
5. Click "Register"
6. You should see "Registration successful! Please login to continue."

### Test Login Flow

1. After registration, the form switches to login mode
2. Fill in:
   - Email: test@example.com
   - Password: test123
3. Click "Login"
4. You should see "Welcome back, Test User! Redirecting to Dashboard..."
5. Check browser's localStorage to see stored token and user data:
   - Open Developer Tools (F12)
   - Go to Application > Local Storage
   - You'll see: token, userId, userName, userEmail, userRole

### Test API Directly (using Postman or curl)

**Register:**
```bash
curl -X POST http://localhost:8008/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"test123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8008/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

**Validate Token:**
```bash
curl -X GET http://localhost:8008/api/auth/validate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Using JWT Token for Protected Routes

### Backend - Protect Endpoints

Create protected endpoints that require authentication:

```java
@RestController
@RequestMapping("/api/grievances")
@CrossOrigin(origins = "*")
public class GrievanceController {
    
    @GetMapping("/my-grievances")
    public ResponseEntity<?> getMyGrievances() {
        // This endpoint requires authentication
        // User details are available from SecurityContext
        return ResponseEntity.ok("List of grievances");
    }
}
```

### Frontend - Make Authenticated Requests

```javascript
import api from '../services/authService';

// Token is automatically added by axios interceptor
const response = await api.get('/grievances/my-grievances');
```

---

## Security Configuration Details

### JWT Token Structure
```json
{
  "userId": 1,
  "email": "john@example.com",
  "role": "USER",
  "sub": "john@example.com",
  "iat": 1234567890,
  "exp": 1234571490
}
```

### Token Expiration
- Default: 1 hour (3600000 milliseconds)
- Configurable in `application.properties`: `jwt.expiration`

### Password Security
- Passwords are encrypted using BCrypt with auto-generated salt
- Encryption happens in `AuthService.register()` method
- Verification happens in `AuthService.login()` method

### CORS Configuration
- Allows origins: http://localhost:3000, http://localhost:3001
- Allows methods: GET, POST, PUT, DELETE, OPTIONS
- Allows credentials: true
- Max age: 3600 seconds

---

## User Roles

### Available Roles
- **USER**: Default role for registered users (can submit and track grievances)
- **ADMIN**: Can manage and assign grievances
- **STAFF**: Can handle assigned grievances
- **SUPERADMIN**: Full system access (optional, for system management)

To change user role, update the database directly or create an admin endpoint.

---

## Troubleshooting

### Common Issues

1. **CORS Error**
   - Make sure backend is running on port 8008
   - Check SecurityConfig CORS configuration includes your frontend URL

2. **Database Connection Error**
   - Verify MySQL is running
   - Check database credentials in application.properties
   - Ensure database `students_db` exists

3. **JWT Token Invalid**
   - Token may have expired (default: 1 hour)
   - Login again to get a new token

4. **npm install fails**
   - Run: `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`
   - Then retry: `npm install`

5. **Port Already in Use**
   - Backend (8008): Change `server.port` in application.properties
   - Frontend (3000): Run with different port: `PORT=3001 npm start` (Linux/Mac) or set in package.json

---

## Next Steps

### Recommended Enhancements

1. **Create Dashboard Component**
   - Display user information
   - Show user's grievances
   - Logout button

2. **Add Protected Routes**
   - Create React Router setup
   - Add PrivateRoute component
   - Redirect to login if not authenticated

3. **Implement Grievance Management**
   - Create Grievance entity
   - Add CRUD operations
   - Link grievances to users

4. **Add Role-Based Access Control**
   - Create different dashboards for USER, ADMIN, STAFF
   - Implement @PreAuthorize annotations in backend
   - Add role checking in frontend

5. **Enhance Security**
   - Add refresh tokens
   - Implement rate limiting
   - Add email verification
   - Add password reset functionality

---

## Project Dependencies

### Backend (pom.xml)
- spring-boot-starter-webmvc
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- mysql-connector-j
- jjwt-api (0.11.5)
- jjwt-impl (0.11.5)
- jjwt-jackson (0.11.5)
- lombok

### Frontend (package.json)
- react (^18.2.0)
- react-dom (^18.2.0)
- axios (latest)
- react-icons (^4.11.0)

---

## License
This project is created for educational purposes.

## Contact
For issues or questions, please create an issue in the project repository.

---

**Happy Coding! 🚀**
