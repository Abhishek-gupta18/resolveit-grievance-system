# Admin Login Troubleshooting Guide

## ✅ Steps to Enable Admin Login

### Step 1: Create Admin User in Database

**Option A: Using MySQL Command Line**

```bash
# Open MySQL terminal
mysql -u root -p

# Use the database
USE students_db;

# Run the SQL command
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
VALUES (
  'Admin User',
  'admin@resolve.com',
  '$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu',
  'ADMIN',
  true,
  NOW(),
  NOW()
);

# Verify
SELECT id, name, email, role, is_active FROM users WHERE role = 'ADMIN';
```

**Option B: Using SQL File**

```bash
# Navigate to project root
cd c:\Users\Asus\OneDrive\Desktop\rajjo bday\demo

# Run the SQL script
mysql -u root -p students_db < CREATE_ADMIN_USER.sql
```

**Option C: Using MySQL Workbench**
1. Open MySQL Workbench
2. Connect to your database
3. Open the file: `CREATE_ADMIN_USER.sql`
4. Execute the query (Ctrl + Enter)
5. Run the verification query

---

### Step 2: Verify Admin User Creation

Run this query to confirm the admin user exists:

```sql
SELECT id, name, email, role, is_active, created_at FROM users WHERE email = 'admin@resolve.com';
```

**Expected Output:**
```
| id | name       | email               | role  | is_active | created_at          |
|----|------------|---------------------|-------|-----------|---------------------|
| 1  | Admin User | admin@resolve.com   | ADMIN | 1         | 2026-03-06 12:00:00 |
```

---

### Step 3: Restart Your Spring Boot Backend

```bash
# If backend is running, stop it (Ctrl + C)

# In the project root directory, run:
mvn clean install
mvn spring-boot:run
```

**Wait for message:** `Started MavennnApplication in X seconds`

---

### Step 4: Try Admin Login

1. **Open Browser:** http://localhost:3000
2. **Click:** "Login / Register"
3. **Enter Credentials:**
   - Email: `admin@resolve.com`
   - Password: `admin123`
4. **Click:** Login
5. **Should Redirect To:** Admin Dashboard ✅

---

## 🔍 Common Issues & Solutions

### ❌ Issue: "Invalid email or password"

**Cause:** Admin user not in database or wrong created credentials

**Solution:**
```bash
# Check if admin user exists
mysql -u root -p -e "USE students_db; SELECT * FROM users WHERE email = 'admin@resolve.com';"

# If no result, create user again using the SQL script
mysql -u root -p students_db < CREATE_ADMIN_USER.sql
```

---

### ❌ Issue: Login redirects to User Dashboard instead of Admin Dashboard

**Cause:** User role in database is not 'ADMIN' or 'USER'

**Solution:**
```sql
-- Check user role
SELECT email, role FROM users WHERE email = 'admin@resolve.com';

-- Update if needed
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@resolve.com';
```

---

### ❌ Issue: Login works but page doesn't load

**Cause:** Browser cache or missing component

**Solution:**
1. **Clear Browser Cache:**
   - Press: `Ctrl + Shift + Delete`
   - Clear "Cached images and files"
   - Refresh page

2. **Check Console for Errors:**
   - Press: `F12`
   - Go to: "Console" tab
   - Look for red error messages

---

### ❌ Issue: "User account is inactive"

**Cause:** `is_active` field is set to false

**Solution:**
```sql
UPDATE users SET is_active = true WHERE email = 'admin@resolve.com';
```

---

### ❌ Issue: Backend throws "User not found" error

**Cause:** Email doesn't exist in database

**Solution:**
```bash
# List all users
mysql -u root -p -e "USE students_db; SELECT id, email, role FROM users;"

# Create admin if not exists
mysql -u root -p students_db < CREATE_ADMIN_USER.sql
```

---

## 🧪 Testing Different User Roles

### Test Case 1: Admin User (Should show Admin Dashboard)

```
Email: admin@resolve.com
Password: admin123
Expected Result: Admin Dashboard with staff management, reports, etc.
```

### Test Case 2: Regular User (Should show User Dashboard)

First, create a regular user:
```sql
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
VALUES (
  'Test User',
  'user@resolve.com',
  '$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu',
  'USER',
  true,
  NOW(),
  NOW()
);
```

Then login:
```
Email: user@resolve.com
Password: admin123
Expected Result: User Dashboard with complaint filing, tracking, etc.
```

---

## 🔑 Password Hashing Reference

The admin password is hashed using **BCrypt**.

**Current Credentials:**
- Email: `admin@resolve.com`
- Password: `admin123`
- BCrypt Hash: `$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu`

**To Generate Your Own BCrypt Hash:**
1. Visit: https://bcryptgenerator.com/
2. Enter your desired password
3. Copy the generated hash
4. Use it in the INSERT query

Example:
```sql
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
VALUES (
  'New Admin',
  'newadmin@resolve.com',
  'YOUR_BCRYPT_HASH_HERE',
  'ADMIN',
  true,
  NOW(),
  NOW()
);
```

---

## 🔄 Complete Reset Steps

If you want to start fresh:

```sql
-- 1. Delete all users
DELETE FROM users;

-- 2. Create new admin user
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
VALUES (
  'Admin User',
  'admin@resolve.com',
  '$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu',
  'ADMIN',
  true,
  NOW(),
  NOW()
);

-- 3. Create test user
INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
VALUES (
  'Test User',
  'user@resolve.com',
  '$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu',
  'USER',
  true,
  NOW(),
  NOW()
);

-- 4. Verify
SELECT id, email, role, is_active FROM users;
```

---

## 📋 Verification Checklist

Before attempting admin login, verify these:

- [ ] Spring Boot backend is running (check http://localhost:8008/api/auth/health)
- [ ] React frontend is running (check http://localhost:3000)
- [ ] Admin user exists in database with ADMIN role
- [ ] Admin user `is_active` is set to `true`
- [ ] Password hash is correct BCrypt hash
- [ ] No JavaScript errors in browser console (F12)
- [ ] Browser cache is cleared
- [ ] JWT token is being stored in localStorage

---

## 🆘 Still Having Issues?

**Debug Steps:**

1. **Check Backend Logs:**
   - Look at the Spring Boot terminal for error messages
   - Search for "Login failed" or "User not found"

2. **Check Network Request:**
   - Open F12 → Network tab
   - Try to login
   - Look for POST request to `/api/auth/login`
   - Check request body and response details

3. **Check Database Connection:**
   ```sql
   SHOW DATABASES;
   USE students_db;
   SHOW TABLES;
   SELECT COUNT(*) FROM users;
   ```

4. **Check Application Properties:**
   - Verify `application.properties` has correct database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/students_db
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```

---

## 📞 Support

If you're still having issues:

1. Share the **browser console errors** (F12 → Console tab)
2. Share the **backend logs** (terminal output)
3. Run verification query and share **database output**

---

**Last Updated:** March 6, 2026  
**Backend Version:** Spring Boot 4.0+  
**Frontend Version:** React 18+
