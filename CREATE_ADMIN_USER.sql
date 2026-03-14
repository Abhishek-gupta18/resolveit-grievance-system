-- SQL Script to Create Admin User for Testing

-- Make sure you're using the correct database
USE students_db;

-- Delete existing admin user if it exists (optional)
-- DELETE FROM users WHERE email = 'admin@resolve.com';

-- Insert new admin user
-- Password: admin123
-- BCrypt Hash: $2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu
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

-- Verify insertion
SELECT id, name, email, role, is_active FROM users WHERE email = 'admin@resolve.com';

-- If you want to create multiple admin users:
-- INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
-- VALUES (
--   'Super Admin',
--   'superadmin@resolve.com',
--   '$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu',
--   'ADMIN',
--   true,
--   NOW(),
--   NOW()
-- );

-- Create test user (USER role) for comparison
-- INSERT INTO users (name, email, password, role, is_active, created_at, updated_at) 
-- VALUES (
--   'Test User',
--   'user@resolve.com',
--   '$2a$10$slYQmyNdGzin7olVyJCL0OPST9/PgBkqquzi.Hy1yK1J0D0AI2afu',
--   'USER',
--   true,
--   NOW(),
--   NOW()
-- );

-- View all users
SELECT id, name, email, role, is_active FROM users;
