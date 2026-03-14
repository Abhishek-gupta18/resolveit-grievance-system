# System Architecture - ResolveIT Authentication

## 📐 Complete System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           FRONTEND (React)                              │
│                        http://localhost:3000                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌──────────────────────────┐          ┌──────────────────────────┐     │
│  │  Login/Register Form     │          │  Protected Components    │     │
│  │  ├─ Email Input          │          │  ├─ Dashboard           │     │
│  │  ├─ Password Input       │          │  ├─ User Profile        │     │
│  │  ├─ Name Input (Register)│          │  └─ Grievances List     │     │
│  │  └─ Submit Button        │          └──────────────────────────┘     │
│  └──────┬───────────────────┘                      ▲                     │
│         │                                          │                     │
│  ┌──────▼──────────────────────────────────────────┴──────┐              │
│  │             authService.js (API Layer)                 │              │
│  │  ├─ register(userData)                                 │              │
│  │  ├─ login(email, password)                             │              │
│  │  ├─ logout()                                           │              │
│  │  ├─ getToken()                                         │              │
│  │  ├─ getCurrentUser()                                   │              │
│  │  ├─ isAuthenticated()                                  │              │
│  │  └─ validateToken()                                    │              │
│  │                                                        │              │
│  │  Axios Interceptors:                                   │              │
│  │  ├─ Request: Add Bearer token to headers              │              │
│  │  └─ Response: Handle 401 errors (logout & redirect)    │              │
│  └──────┬───────────────────────────────────────────────┬─┘              │
│         │                                               │                 │
│  ┌──────▼────────────────────────────────────────────────┴──┐            │
│  │          Local Storage (Token & User Data)              │            │
│  │  ├─ token: "Bearer eyJhbGciOiJIUzI1NiJ9..."           │            │
│  │  ├─ userId: "1"                                        │            │
│  │  ├─ userName: "John Doe"                               │            │
│  │  ├─ userEmail: "john@example.com"                      │            │
│  │  └─ userRole: "USER"                                   │            │
│  └──────────────────────────────────────────────────────────┘            │
│                                                                           │
└─────────────────────────────────────────────────────────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   HTTP/CORS Requests   │
                    │  Authorization Header  │
                    └────────────┬────────────┘
                                 │
┌─────────────────────────────────────────────────────────────────────────┐
│                     BACKEND (Spring Boot)                               │
│                   http://localhost:8008/api                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │              HTTP Request Interceptor Chain                      │    │
│  │                                                                  │    │
│  │  1. Spring Security Filter Chain                               │    │
│  │  2. JwtAuthenticationFilter (Custom)                           │    │
│  │     └─ Extract JWT from Authorization header                   │    │
│  │     └─ Validate JWT signature and expiration                   │    │
│  │     └─ Extract claims (userId, email, role)                    │    │
│  │     └─ Set Authentication in SecurityContext                   │    │
│  └──────────┬────────────────────────────────────────────────────┘    │
│             │                                                           │
│  ┌──────────▼────────────────────────────────────────────────────────┐  │
│  │         REST Controllers (API Endpoints)                          │  │
│  │                                                                    │  │
│  │  AuthController:                                                  │  │
│  │  ├─ POST /auth/register          → Register new user            │  │
│  │  ├─ POST /auth/login             → Login & get JWT token        │  │
│  │  ├─ GET  /auth/validate          → Validate JWT token           │  │
│  │  └─ GET  /auth/health            → Health check                 │  │
│  │                                                                    │  │
│  │  ProtectedController (Examples):                                 │  │
│  │  ├─ GET /protected/user-info     → Get current user info        │  │
│  │  └─ GET /protected/admin-only    → Admin-only endpoint          │  │
│  └──────────┬──────────────────┬────────────────────────────────────┘  │
│             │                  │                                        │
│  ┌──────────▼──────┐  ┌────────▼──────────────────────────────────┐   │
│  │  AuthService    │  │  (Other Services - Future)                │   │
│  │                 │  │  ├─ GrievanceService                      │   │
│  │ register()      │  │  ├─ UserService                           │   │
│  │ login()         │  │  └─ AdminService                          │   │
│  │ validateToken() │  └──────────────────────────────────────────┘   │
│  │ getUserFromToken()│                                               │   │
│  │                 │           ┌────────────────────────────┐        │   │
│  │ Password:       │           │  Repositories              │        │   │
│  │ BCryptPassword  │           │  └─ UserRepository         │        │   │
│  │ Encoder         │           │  └─ GrievanceRepository(*)│        │   │
│  │                 │           └────────────────────────────┘        │   │
│  │ JWT:            │                                                 │   │
│  │ JwtUtil         │           ┌────────────────────────────┐        │   │
│  │ ├─ generateToken()          │  Entities (JPA Models)     │        │   │
│  │ ├─ validateToken()          │  ├─ User                   │        │   │
│  │ ├─ extractEmail()           │  ├─ Grievance (*)          │        │   │
│  │ ├─ extractUserId()          │  └─ Comment (*)            │        │   │
│  │ └─ extractRole()            └────────────────────────────┘        │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│             │                                  │                         │
└─────────────┼──────────────────────────────────┼─────────────────────────┘
              │                                  │
              │                                  │
         ┌────▼──────────────────────────────────▼─────┐
         │         Security Configuration              │
         │                                             │
         │ SecurityConfig:                             │
         │ ├─ CORS: Allow React frontend               │
         │ ├─ Session: Stateless (JSESSIONID disabled) │
         │ ├─ Auth: JWT (no form-based auth)          │
         │ ├─ CSRF: Disabled (stateless API)          │
         │ └─ PasswordEncoder: BCryptPasswordEncoder() │
         └────┬──────────────────────────────────────┬─┘
              │                                      │
         ┌────▼──────┐                        ┌──────▼─────┐
         │   MySQL   │                        │  JWT Token │
         │  Database │                        │  (Base64)  │
         │           │                        │            │
         │┌─────────┐│             ┌─────────────────────┐ │
         ││  users  ││             │ Header:             │ │
         ││  table  ││             │ - alg: HS256        │ │
         ││ ────────││             │ - typ: JWT          │ │
         ││ id      ││             ├─────────────────────┤ │
         ││ name    ││             │ Payload:            │ │
         ││ email   ││             │ - userId: 1         │ │
         ││ password││─ BCrypt    │ - email: user@...   │ │
         ││ role    ││             │ - role: USER        │ │
         ││ active  ││             │ - exp: 1234567890   │ │
         ││ created ││             ├─────────────────────┤ │
         ││ updated ││             │ Signature:          │ │
         │└─────────┘│             │ HMACSHA256(base64)  │ │
         └───────────┘             └─────────────────────┘ │
                                    └─────────────────────┘
```

---

## 🔄 Request Flow Diagrams

### Registration Flow
```
┌──────────────┐
│   Frontend   │
│              │
│ User fills   │
│ register     │
│ form         │
└──────┬───────┘
       │
       │ POST /api/auth/register
       │ {name, email, password}
       │
       ▼
┌──────────────────────┐
│   AuthController     │
│                      │
│ 1. Validate input    │
│ 2. Call AuthService  │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────────────┐
│   AuthService.register()     │
│                              │
│ 1. Check email exists        │
│ 2. Encrypt password (BCrypt) │
│ 3. Create User entity        │
│ 4. Save to database          │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────┐
│  MySQL Database      │
│                      │
│  INSERT INTO users   │
│  (name, email, pswd) │
└──────────────────────┘
       │
       ▼
┌──────────────────────────┐
│  Response (201 Created)  │
│                          │
│ {                        │
│   message: "...",        │
│   userId: 1,             │
│   email: "..."           │
│ }                        │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────┐
│    Frontend          │
│                      │
│ Show success message │
│ Switch to login form │
└──────────────────────┘
```

### Login Flow
```
┌──────────────┐
│   Frontend   │
│              │
│ User fills   │
│ login form   │
└──────┬───────┘
       │
       │ POST /api/auth/login
       │ {email, password}
       │
       ▼
┌──────────────────────┐
│   AuthController     │
│                      │
│ 1. Validate input    │
│ 2. Call AuthService  │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────────────┐
│   AuthService.login()        │
│                              │
│ 1. Find user by email        │
│ 2. Verify password (BCrypt)  │
│ 3. Check user active         │
│ 4. Generate JWT token        │
│    (JwtUtil.generateToken)   │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────┐
│  JWT Generated           │
│                          │
│  eyJhbGciOiJIUzI1NiJ9   │
│  .eyJ1c2VySWQiOjE...    │
│  .TJVA95UV...           │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  Response (200 OK)           │
│                              │
│ {                            │
│   token: "eyJ...",           │
│   userId: 1,                 │
│   name: "John Doe",          │
│   email: "john@example.com", │
│   role: "USER"               │
│ }                            │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│    Frontend (authService)    │
│                              │
│ Store in localStorage:       │
│ ├─ token                     │
│ ├─ userId                    │
│ ├─ userName                  │
│ ├─ userEmail                 │
│ └─ userRole                  │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────┐
│    Frontend UI       │
│                      │
│ User logged in       │
│ Redirect to dashboard│
└──────────────────────┘
```

### Protected API Call Flow
```
┌──────────────────────┐
│    Frontend          │
│                      │
│ Call: api.get(...)   │
│ (authService method) │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────────────┐
│  Axios Interceptor           │
│  (Request)                   │
│                              │
│ 1. Get token from localStorage
│ 2. Add to header:            │
│    Authorization: Bearer ... │
└──────┬───────────────────────┘
       │
       │ GET /api/protected/user-info
       │ Authorization: Bearer eyJhbGc...
       │
       ▼
┌──────────────────────────────┐
│  Spring Security Filter      │
│  Chain                       │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  JwtAuthenticationFilter     │
│                              │
│ 1. Extract token from header │
│ 2. Remove "Bearer " prefix   │
│ 3. Validate JWT signature    │
│ 4. Check expiration          │
│ 5. Extract claims            │
│ 6. Set Authentication        │
│    in SecurityContext        │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  Security Context            │
│  (Request scope)             │
│                              │
│ Authentication set with:     │
│ - Principal: email           │
│ - Authorities: [ROLE_USER]   │
│ - Details: ...               │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  ProtectedController         │
│  /user-info endpoint         │
│                              │
│ 1. Get authentication        │
│ 2. Extract email             │
│ 3. Get roles                 │
│ 4. Return user info          │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────────────┐
│  Response (200 OK)           │
│                              │
│ {                            │
│   email: "john@ex.com",      │
│   role: "ROLE_USER",         │
│   message: "authenticated!"  │
│ }                            │
└──────┬───────────────────────┘
       │
       ▼
┌──────────────────────┐
│  Axios Interceptor   │
│  (Response)          │
│                      │
│ Success!             │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  Frontend Component  │
│                      │
│ Display user info    │
└──────────────────────┘
```

---

## 🔑 JWT Token Lifecycle

```
┌─────────────────────────────────────────────────────────┐
│              JWT Token Creation                         │
└─────────────────────────────────────────────────────────┘
         │
         │ 1. User logs in
         ▼
┌─────────────────────────────────────────────────────────┐
│  JwtUtil.generateToken(userId, email, role)            │
│  ├─ Create claims Map:                                  │
│  │  └─ userId, email, role                             │
│  ├─ Set expiration: now + 1 hour                       │
│  ├─ Sign with HS256 + secret key                       │
│  └─ Return BASE64 encoded token                        │
└──────────┬──────────────────────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────────────────────┐
│  Token Structure (3 parts separated by dots)            │
│                                                          │
│  Header.                   Payload.                 Sig. │
│  eyJhbGciOiJIUzI1NiJ9.    eyJ1c2VySWQ... .    TJVA95UV...
│                                                          │
│  {"alg":"HS256"}           {"userId":1,         HMACSHA256  │
│                            "email":"...",      (baseHeader. │
│                            "role":"USER",      basepayload, │
│                            "exp":1234567890}   secret)     │
└────────────┬───────────────────────────────────────────┘
             │
             │ 2. Token sent to frontend
             ▼
┌──────────────────────────────────────────────────────────┐
│  Frontend (Browser)                                      │
│  └─ Store in localStorage with key "token"             │
└────────────┬───────────────────────────────────────────┘
             │
             │ 3. For each API request
             ▼
┌──────────────────────────────────────────────────────────┐
│  Axios Interceptor adds to header:                       │
│  Authorization: Bearer <token>                          │
└────────────┬───────────────────────────────────────────┘
             │
             │ 4. Backend receives request
             ▼
┌──────────────────────────────────────────────────────────┐
│  JwtAuthenticationFilter                                │
│  ├─ Extract Bearer token                               │
│  ├─ Validate signature using secret key                │
│  ├─ Check if expiration time passed                    │
│  ├─ Parse claims                                       │
│  └─ Set authentication if valid                        │
└────────────┬───────────────────────────────────────────┘
             │
             ▼
          Valid?
         /       \
       YES        NO
       /           \
      ▼             ▼
┌────────────┐  ┌─────────────────┐
│ Continue   │  │ Return 401      │
│ to handler │  │ Unauthorized    │
└────────────┘  │ (logout frontend)│
                └─────────────────┘
                       │
                       ▼
                ┌─────────────────┐
                │ Frontend clears │
                │ localStorage    │
                │ Redirect login  │
                └─────────────────┘

     ┌────────────────Expiration Loop─────────────────┐
     │                                                │
     │  Max Token Age: 1 hour from creation          │
     │                                                │
     │  Example:                                      │
     │  ├─ Created: 2:00 PM                          │
     │  ├─ Valid Until: 3:00 PM                      │
     │  ├─ At 3:00 PM: Token expires                 │
     │  └─ At 3:01 PM: Validation fails → 401 error  │
     │                                                │
     │  Solution: User must login again              │
     │  for a new token                              │
     │                                                │
     └────────────────────────────────────────────────┘
```

---

## 🔒 Security Layers

```
┌───────────────────────────────────────────────────────┐
│  Layer 1: Input Validation                            │
├───────────────────────────────────────────────────────┤
│  - Email format validation                            │
│  - Password length minimum (6 chars)                  │
│  - Name not empty                                     │
│  - SQL injection prevention (prepared statements)     │
└───────────────────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────┐
│  Layer 2: Password Encryption                         │
├───────────────────────────────────────────────────────┤
│  - BCrypt algorithm with auto-generated salt         │
│  - Passwords never stored in plain text              │
│  - Example:                                           │
│    Input: password123                                │
│    Stored: $2a$10$N9qo8uLOickgx2ZM.z...             │
│  - Verification: BCrypt.matches(input, stored)       │
└───────────────────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────┐
│  Layer 3: User Validation                             │
├───────────────────────────────────────────────────────┤
│  - User must exist in database                       │
│  - Password must match BCrypt hash                   │
│  - User account must be active                       │
│  - Email must be unique (no duplicates)              │
└───────────────────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────┐
│  Layer 4: JWT Generation                              │
├───────────────────────────────────────────────────────┤
│  - Secret key Base64 encoded (256-bit)               │
│  - HS256 HMAC signature algorithm                    │
│  - Includes userId, email, role claims              │
│  - Set expiration time (1 hour)                     │
│  - Cannot be modified without secret key             │
└───────────────────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────┐
│  Layer 5: Token Transmission                          │
├───────────────────────────────────────────────────────┤
│  - Authorization header (not in URL params)          │
│  - Bearer scheme: "Authorization: Bearer <token>"    │
│  - HTTPS recommended in production                   │
│  - CORS configured to allow only trusted origins     │
└───────────────────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────┐
│  Layer 6: Token Validation                            │
├───────────────────────────────────────────────────────┤
│  - Signature verified with secret key                │
│  - Expiration time checked against current time      │
│  - JwtException thrown if invalid                    │
│  - Filter catches exception and rejects request      │
└───────────────────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────┐
│  Layer 7: Spring Security Context                     │
├───────────────────────────────────────────────────────┤
│  - Authentication object created                     │
│  - Principal set to user email                       │
│  - Authorities set to user role                      │
│  - Can be accessed in controller                     │
│  - Available for @PreAuthorize checks               │
└───────────────────────────────────────────────────────┘
                      │
                      ▼
┌───────────────────────────────────────────────────────┐
│  Layer 8: Controller Authorization                    │
├───────────────────────────────────────────────────────┤
│  - @PreAuthorize checks role-based access           │
│  - Example: @PreAuthorize("hasRole('ADMIN')")       │
│  - Endpoint returns 403 Forbidden if unauthorized    │
│  - Can manually check authentication in code        │
└───────────────────────────────────────────────────────┘
```

---

## 📊 Data Flow Summary

```
                    User Registration
┌─────────────────────────────────────────────────────┐
│ Frontend                                             │
│ ├─ Name, Email, Password input                      │
│ └─ POST to /api/auth/register                       │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ Backend: AuthController → AuthService               │
│ ├─ Validate input                                   │
│ ├─ Check if email exists                            │
│ ├─ Encrypt password (BCrypt)                        │
│ ├─ Create User entity                               │
│ └─ Save to database                                 │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ MySQL Database                                      │
│ INSERT INTO users (name, email, password, role)     │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ Response: {"userId": 1, "email": "...", message}    │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
             Success!

                        User Login
┌─────────────────────────────────────────────────────┐
│ Frontend                                             │
│ ├─ Email, Password input                            │
│ └─ POST to /api/auth/login                          │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ Backend: AuthController → AuthService               │
│ ├─ Find user by email                               │
│ ├─ Verify password (BCrypt.matches)                 │
│ ├─ Check user active                                │
│ └─ Generate JWT                                     │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ Response: {"token": "eyJ...", "userId": 1, ...}     │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ Frontend: authService.js                            │
│ ├─ Store token in localStorage                      │
│ ├─ Store userId, name, email, role                  │
│ └─ Set authentication state                         │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
             Authenticated!

                    Accessing Protected Route
┌─────────────────────────────────────────────────────┐
│ Frontend: axios interceptor                         │
│ └─ GET /api/protected/user-info                     │
│    + Header: Authorization: Bearer <token>          │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ Backend: JwtAuthenticationFilter                    │
│ ├─ Extract token from header                        │
│ ├─ Validate signature and expiration                │
│ ├─ Extract claims (userId, email, role)             │
│ └─ Set authentication in SecurityContext            │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ Backend: ProtectedController                        │
│ ├─ Get authentication from SecurityContext          │
│ ├─ Return user info                                 │
│ └─ Response: {"email": "...", "role": "..."} (200)  │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
             Success with user data!
```

---

**This architecture ensures secure, stateless authentication with clear separation of concerns!**
