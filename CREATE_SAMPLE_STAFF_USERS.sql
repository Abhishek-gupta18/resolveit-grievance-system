-- Seed sample staff users for complaint handling
-- Password for all users below: password

INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
SELECT 'Aarav Verma', 'aarav001@staff.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiuQf6M5jK0M6xQx5I6fM9vDOMkMt2',  'STAFF', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'aarav001@staff.com');

INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
SELECT 'Meera Iyer', 'meera002@staff.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiuQf6M5jK0M6xQx5I6fM9vDOMkMt2', 'STAFF', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'meera002@staff.com');

INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
SELECT 'Rohit Das', 'rohit003@staff.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiuQf6M5jK0M6xQx5I6fM9vDOMkMt2',  'STAFF', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'rohit003@staff.com');

INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
SELECT 'Nisha Kapoor', 'nisha004@staff.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiuQf6M5jK0M6xQx5I6fM9vDOMkMt2', 'STAFF', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'nisha004@staff.com');

INSERT INTO users (name, email, password, role, is_active, created_at, updated_at)
SELECT 'Kabir Sharma', 'kabir005@staff.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiuQf6M5jK0M6xQx5I6fM9vDOMkMt2', 'STAFF', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'kabir005@staff.com');
