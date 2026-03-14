-- Set user id=3 as ADMIN for the requested account
-- Also clears staff_id because this is no longer a staff account.

UPDATE users
SET role = 'ADMIN',
    staff_id = NULL,
    is_active = TRUE,
    updated_at = NOW()
WHERE id = 3
  AND email = 'abhishekgupta.1856@outlook.com';

SELECT id, name, email, role, staff_id, is_active
FROM users
WHERE id = 3;
