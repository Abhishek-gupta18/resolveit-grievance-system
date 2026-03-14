# 🚀 Quick Start Guide - ResolveIT Authentication System

## Step-by-Step Setup (5 minutes)

### Step 1: Start MySQL Database
```bash
# Make sure MySQL is running on port 3306
# Create database if not exists:
mysql -u root -p
CREATE DATABASE students_db;
exit;
```

### Step 2: Start Backend Server
```bash
# Navigate to project root
cd "c:\Users\Asus\OneDrive\Desktop\rajjo bday\demo"

# Run Spring Boot application
mvn spring-boot:run
```

Wait for the message: **"Started MavennnApplication in X seconds"**

Backend is now running on: **http://localhost:8008**

### Step 3: Start Frontend Application
```bash
# Open a new terminal
# Navigate to frontend directory
cd "c:\Users\Asus\OneDrive\Desktop\rajjo bday\demo\frontend"

# Set execution policy (if needed)
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass

# Start React app
npm start
```

Frontend is now running on: **http://localhost:3000**

### Step 4: Test the Application

#### ✅ Test Registration
1. Browser will auto-open to http://localhost:3000
2. Click **"Login / Register"** button
3. Click **"Register"** text at bottom
4. Fill the form:
   - Name: `John Doe`
   - Email: `john@example.com`
   - Password: `password123`
   - Confirm Password: `password123`
5. Click **"Register"**
6. ✅ Success message appears: "Registration successful! Please login to continue."

#### ✅ Test Login
1. Form automatically switches to login mode
2. Fill credentials:
   - Email: `john@example.com`
   - Password: `password123`
3. Click **"Login"**
4. ✅ Success message: "Welcome back, John Doe! Redirecting to Dashboard..."

#### ✅ Verify Token Storage
1. Press **F12** to open Developer Tools
2. Go to **Application** tab
3. Click **Local Storage** → **http://localhost:3000**
4. You should see:
   - `token`: Your JWT token
   - `userId`: 1
   - `userName`: John Doe
   - `userEmail`: john@example.com
   - `userRole`: USER

---

## 🧪 Test API Endpoints (Optional)

### Using Postman or Browser REST Client

#### 1. Health Check
```
GET http://localhost:8008/api/auth/health
```
Expected Response: `"Auth service is running"`

#### 2. Register User
```
POST http://localhost:8008/api/auth/register
Content-Type: application/json

Body:
{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "password": "jane123"
}
```

#### 3. Login User
```
POST http://localhost:8008/api/auth/login
Content-Type: application/json

Body:
{
  "email": "jane@example.com",
  "password": "jane123"
}
```
Copy the `token` from response

#### 4. Access Protected Endpoint
```
GET http://localhost:8008/api/protected/user-info
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## 📊 Check Database

### Verify User Created in MySQL
```sql
USE students_db;

-- View users table
SELECT * FROM users;

-- You should see your registered user with encrypted password
```

Expected output:
```
id | name      | email              | password (encrypted)        | role | is_active | created_at
1  | John Doe  | john@example.com   | $2a$10$encrypted_hash...      | USER | 1         | 2024-...
```

---

## 🎯 What Was Implemented

### ✅ Backend Components
- ✅ User Entity with BCrypt password encryption
- ✅ JWT Utility for token generation and validation
- ✅ Authentication Service (register & login logic)
- ✅ Authentication Controller (API endpoints)
- ✅ Security Configuration with JWT filter
- ✅ Spring Security with CORS enabled
- ✅ MySQL database integration

### ✅ Frontend Components
- ✅ Authentication Service (API calls with axios)
- ✅ Login/Register form with validation
- ✅ JWT token storage in localStorage
- ✅ Automatic token attachment to API requests
- ✅ Loading states and error handling

### ✅ Security Features
- ✅ BCrypt password encryption
- ✅ JWT token with Base64 encoding
- ✅ Token contains: userId, email, role
- ✅ Token expiration (1 hour)
- ✅ CORS configuration for React frontend
- ✅ Protected routes with authentication

---

## 🔧 Common Commands

### Backend
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run
```

### Frontend
```bash
# Install dependencies
npm install

# Start dev server
npm start

# Build for production
npm run build

# Run tests
npm test
```

---

## 📝 Default Configuration

| Setting | Value |
|---------|-------|
| Backend Port | 8008 |
| Frontend Port | 3000 |
| Database | students_db |
| Database Port | 3306 |
| JWT Expiration | 1 hour |
| Default Role | USER |
| Password Min Length | 6 characters |

---

## 🐛 Troubleshooting

### Backend won't start
- ✅ Check MySQL is running
- ✅ Verify database `students_db` exists
- ✅ Check port 8008 is not in use

### Frontend won't start
- ✅ Delete `node_modules` and run `npm install`
- ✅ Check port 3000 is not in use
- ✅ Run: `Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`

### Login fails
- ✅ Check backend is running
- ✅ Verify email and password are correct
- ✅ Check browser console for errors
- ✅ Verify database has the user

### Token not working
- ✅ Token expires after 1 hour - login again
- ✅ Clear localStorage and login again
- ✅ Check token format in Authorization header

---

## 🎓 Next Steps

1. **Create Dashboard Page**
   - Show user information
   - Display userRole and userName from localStorage

2. **Add Logout Functionality**
   - Clear localStorage
   - Redirect to login page

3. **Implement Grievance Management**
   - Create Grievance entity
   - Add CRUD operations
   - Link grievances to authenticated user

4. **Add React Router**
   - Protect routes
   - Redirect unauthenticated users
   - Create different pages based on roles

---

## 📞 Support

If you encounter any issues:
1. Check the detailed [AUTHENTICATION_GUIDE.md](./AUTHENTICATION_GUIDE.md)
2. Review error messages in browser console (F12)
3. Check Spring Boot console logs
4. Verify MySQL database connection

---

**Success! Your authentication system is ready! 🎉**

You can now:
- ✅ Register new users
- ✅ Login with credentials
- ✅ Store JWT tokens
- ✅ Access protected routes
- ✅ Build your grievance management features

Happy Coding! 🚀
