# ✅ Setup Verification Checklist

Use this checklist to verify all components are correctly installed and configured.

---

## 🔧 Pre-Setup Requirements

- [ ] Java 17 or higher installed
  ```bash
  java -version
  ```

- [ ] Maven installed
  ```bash
  mvn -v
  ```

- [ ] Node.js 14+ and npm installed
  ```bash
  node -v
  npm -v
  ```

- [ ] MySQL Server running
  ```bash
  mysql -u root -p
  CREATE DATABASE students_db;
  ```

---

## 📦 Backend Verification

### 1. Dependencies Check

- [ ] pom.xml has JWT dependencies
  - [ ] jjwt-api (0.11.5)
  - [ ] jjwt-impl (0.11.5)
  - [ ] jjwt-jackson (0.11.5)
  - [ ] spring-boot-starter-security

### 2. Java Files Created/Updated

- [ ] `User.java` exists and has password field
  ```
  src/main/java/com/example/demo/model/User.java
  ```

- [ ] DTOs created:
  ```
  ├── LoginRequest.java
  ├── RegisterRequest.java
  └── AuthResponse.java
  src/main/java/com/example/demo/dto/
  ```

- [ ] UserRepository created
  ```
  src/main/java/com/example/demo/repository/UserRepository.java
  ```

- [ ] AuthService created with methods:
  - [ ] register()
  - [ ] login()
  - [ ] validateToken()
  ```
  src/main/java/com/example/demo/service/AuthService.java
  ```

- [ ] JwtUtil updated with Base64 encoding
  ```
  src/main/java/com/example/demo/util/JwtUtil.java
  ```

- [ ] AuthController created with endpoints:
  - [ ] POST /api/auth/register
  - [ ] POST /api/auth/login
  - [ ] GET /api/auth/validate
  - [ ] GET /api/auth/health
  ```
  src/main/java/com/example/demo/controller/AuthController.java
  ```

- [ ] ProtectedController created (example endpoints)
  ```
  src/main/java/com/example/demo/controller/ProtectedController.java
  ```

- [ ] JwtAuthenticationFilter created
  ```
  src/main/java/com/example/demo/config/JwtAuthenticationFilter.java
  ```

- [ ] SecurityConfig created with CORS and filter chain
  ```
  src/main/java/com/example/demo/config/SecurityConfig.java
  ```

### 3. Configuration Check

- [ ] application.properties updated:
  - [ ] `jwt.secret` = Base64 encoded key
  - [ ] `jwt.expiration` = 3600000 (1 hour)
  - [ ] Database URL, username, password correct
  - [ ] Application name = "ResolveIT-Grievance-System"

---

## 📱 Frontend Verification

### 1. Dependencies Check

- [ ] axios installed in package.json
  ```bash
  npm list axios
  ```

### 2. JavaScript Files

- [ ] authService.js created with methods:
  - [ ] register()
  - [ ] login()
  - [ ] logout()
  - [ ] getToken()
  - [ ] getCurrentUser()
  - [ ] isAuthenticated()
  - [ ] validateToken()
  ```
  frontend/src/services/authService.js
  ```

- [ ] Login.jsx updated:
  - [ ] Imports authService functions
  - [ ] API calls implemented
  - [ ] Loading state added
  - [ ] Error messages displayed
  ```
  frontend/src/pages/Login.jsx
  ```

---

## 🗄️ Database Verification

- [ ] Database created: `students_db`

- [ ] After first run, verify users table:
  ```sql
  USE students_db;
  DESCRIBE users;
  ```

- [ ] Expected columns:
  - [ ] id (BIGINT AUTO_INCREMENT)
  - [ ] name (VARCHAR 255)
  - [ ] email (VARCHAR 255 UNIQUE)
  - [ ] password (VARCHAR 255) - BCrypt encrypted
  - [ ] role (VARCHAR 50)
  - [ ] is_active (BOOLEAN)
  - [ ] created_at (TIMESTAMP)
  - [ ] updated_at (TIMESTAMP)

---

## 🚀 Runtime Verification

### Step 1: Start Backend

- [ ] Navigate to project root directory
  ```bash
  cd "c:\Users\Asus\OneDrive\Desktop\rajjo bday\demo"
  ```

- [ ] Run Spring Boot
  ```bash
  mvn spring-boot:run
  ```

- [ ] Wait for startup message:
  ```
  Started MavennnApplication in X seconds
  ```

- [ ] Backend running on http://localhost:8008

- [ ] Test health endpoint:
  ```bash
  curl http://localhost:8008/api/auth/health
  # Expected: "Auth service is running"
  ```

### Step 2: Start Frontend

- [ ] Open new terminal

- [ ] Navigate to frontend directory
  ```bash
  cd "c:\Users\Asus\OneDrive\Desktop\rajjo bday\demo\frontend"
  ```

- [ ] Set execution policy (if needed)
  ```bash
  Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
  ```

- [ ] Start React
  ```bash
  npm start
  ```

- [ ] Browser opens to http://localhost:3000

- [ ] Login/Register page displays

---

## ✅ Functional Testing

### Registration Tests

- [ ] Can see Register form when clicking toggle
- [ ] All fields present:
  - [ ] Name input
  - [ ] Email input
  - [ ] Password input
  - [ ] Confirm Password input
  - [ ] Register button

- [ ] Form validation works:
  - [ ] Empty name shows error
  - [ ] Invalid email shows error
  - [ ] Password < 6 chars shows error
  - [ ] Passwords don't match shows error

- [ ] Successful registration:
  - [ ] User registered with valid data
  - [ ] Success message displays
  - [ ] Form switches to login mode automatically

- [ ] Duplicate email handling:
  - [ ] Try to register same email twice
  - [ ] Error message displays: "Email already registered"

### Login Tests

- [ ] Can see Login form by default

- [ ] All fields present:
  - [ ] Email input
  - [ ] Password input
  - [ ] Login button

- [ ] Form validation works:
  - [ ] Empty email shows error
  - [ ] Empty password shows error

- [ ] Successful login:
  - [ ] Login with registered credentials
  - [ ] Success message displays: "Welcome back, [name]!"
  - [ ] No error message

- [ ] Failed login:
  - [ ] Try wrong password
  - [ ] Error message displays: "Invalid email or password"
  - [ ] Try non-existent email
  - [ ] Error message displays

### localStorage Verification

- [ ] After successful login:
  - [ ] Press F12 to open Developer Tools
  - [ ] Go to Application > Local Storage > http://localhost:3000
  - [ ] Verify presence of:
    - [ ] token (contains "eyJ")
    - [ ] userId (numeric value)
    - [ ] userName (correct name)
    - [ ] userEmail (correct email)
    - [ ] userRole (should be "USER")

### API Testing (Postman/curl)

- [ ] Register endpoint responds:
  ```bash
  curl -X POST http://localhost:8008/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"name":"Test","email":"test@example.com","password":"test123"}'
  # Expected: 201 Created with user data
  ```

- [ ] Login endpoint responds:
  ```bash
  curl -X POST http://localhost:8008/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"test123"}'
  # Expected: 200 OK with JWT token
  ```

- [ ] Protected endpoint with token:
  ```bash
  curl -X GET http://localhost:8008/api/protected/user-info \
    -H "Authorization: Bearer YOUR_TOKEN_HERE"
  # Expected: 200 OK with user info
  ```

- [ ] Protected endpoint without token:
  ```bash
  curl -X GET http://localhost:8008/api/protected/user-info
  # Expected: 401 Unauthorized or 403 Forbidden
  ```

---

## 🔒 Security Verification

- [ ] Passwords are encrypted (check database):
  ```sql
  SELECT email, password FROM users;
  -- Password should start with $2a$ (BCrypt hash)
  ```

- [ ] JWT token is Base64 encoded:
  - [ ] Token has 3 parts separated by dots
  - [ ] First part (header): eyJ... (decodes to `{"alg":"HS256"}`)
  - [ ] Second part (payload): eyJ... (contains userId, email, role)
  - [ ] Third part (signature): TJVA... (HMAC signature)

- [ ] CORS working:
  - [ ] React frontend can communicate with backend
  - [ ] No CORS errors in browser console

- [ ] Session is stateless:
  - [ ] No JSESSIONID cookie in responses
  - [ ] Authentication only via JWT token

---

## 🐛 Troubleshooting Verification

If tests fail, verify:

- [ ] Backend compilation errors?
  ```bash
  mvn clean compile
  ```

- [ ] Port conflict?
  - [ ] Check nothing else running on 8008
  - [ ] Check nothing else running on 3000

- [ ] Database connection error?
  - [ ] MySQL running? `services.msc`
  - [ ] Database `students_db` exists?
  - [ ] Credentials correct in application.properties?

- [ ] Frontend axios not installed?
  ```bash
  cd frontend
  npm install axios
  ```

- [ ] CORS error in frontend?
  - [ ] Check SecurityConfig allows frontend URL
  - [ ] Check backend is running on correct port

- [ ] JWT token invalid?
  - [ ] Check jwt.secret in application.properties is set
  - [ ] Try logging out (clear localStorage) and logging in again

- [ ] TypeError in frontend?
  - [ ] Open browser DevTools > Console
  - [ ] Look for errors
  - [ ] Check if authService is imported correctly

---

## 📊 Final Verification Summary

### Backend Components Status
- [ ] All Java files created/updated
- [ ] Maven dependencies downloaded
- [ ] Spring Boot compiles without errors
- [ ] Server starts successfully
- [ ] All API endpoints respond

### Frontend Components Status
- [ ] All JavaScript files created/updated
- [ ] npm dependencies installed
- [ ] React compiles without errors
- [ ] App loads in browser
- [ ] Components render correctly

### Integration Status
- [ ] Frontend communicates with backend
- [ ] Registration flow works end-to-end
- [ ] Login flow works end-to-end
- [ ] JWT tokens generated correctly
- [ ] Tokens stored in localStorage
- [ ] Protected endpoints work with tokens

### Security Status
- [ ] Passwords encrypted with BCrypt
- [ ] Tokens signed with HMAC-SHA256
- [ ] CORS configured properly
- [ ] Stateless authentication working
- [ ] Token expiration implemented

### Database Status
- [ ] Database created
- [ ] Users table created
- [ ] User records saved correctly
- [ ] Unique email constraint working

---

## ✨ Success Criteria

Your authentication system is **COMPLETE** when all these items are checked:

- ✅ All backend components created and compiling
- ✅ All frontend components created and running
- ✅ Database tables created with users
- ✅ Can register new user
- ✅ Can login with registered user
- ✅ JWT token generated and stored
- ✅ Protected API endpoints work with token
- ✅ No console errors
- ✅ All security measures in place

---

## 🎉 You're All Set!

Once all items are checked, your authentication system is:
- ✅ Production-ready
- ✅ Secure
- ✅ Fully functional
- ✅ Ready for next features

Proceed to:
1. Create Grievance model and API
2. Build Dashboard component
3. Implement grievance management features
4. Deploy to production

**Congratulations! 🎊**
