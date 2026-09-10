-- Seed data shared with a fresh local H2 database.
-- Each INSERT is idempotent so restarting the application keeps user changes.

INSERT INTO app_user (created_at, display_name, password_hash, role, username)
SELECT TIMESTAMP '2026-09-06 15:05:59.552012', U&'\5f20\8001\5e08',
       '$2a$10$squxro36x5oWSrIJPIuZyOnbs.3K7ErTHroPrzzu4TPxlaPtpGb0S', 'TEACHER', 'teacher01'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE username = 'teacher01');

INSERT INTO app_user (created_at, display_name, password_hash, role, username)
SELECT TIMESTAMP '2026-09-06 15:05:59.675288', U&'\674e\540c\5b66',
       '$2a$10$A1aa7Yvaz6CMFZ/8PxbyfeOe7QP0SRAkKZX/UYYa/6M1TgpliTqmu', 'STUDENT', 'student01'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE username = 'student01');

INSERT INTO app_user (created_at, display_name, password_hash, role, username)
SELECT TIMESTAMP '2026-09-06 15:06:39.344923', U&'\90d1',
       '$2a$10$s5KEONovd2FnLZD4x76pTeNmvbdRkNYyhHgLgN0ooY6twcsj6ggAq', 'TEACHER', 'zzzz'
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE username = 'zzzz');

-- Backfill profile fields for demo users (idempotent: only fills rows still missing an email).
UPDATE app_user
SET student_no = '20230001',
    major = U&'\8ba1\7b97\673a\79d1\5b66\4e0e\6280\672f',
    email = 'student01@campus.edu.cn'
WHERE username = 'student01' AND email IS NULL;

UPDATE app_user
SET student_no = 'T0001',
    major = U&'\8ba1\7b97\673a\5b66\9662',
    email = 'teacher01@campus.edu.cn'
WHERE username = 'teacher01' AND email IS NULL;

UPDATE app_user
SET student_no = 'T0002',
    email = 'zzzz@campus.edu.cn'
WHERE username = 'zzzz' AND email IS NULL;

INSERT INTO activity
    (capacity, created_at, description, end_time, location, registration_deadline,
     start_time, status, title, updated_at, version, organizer_id)
SELECT 30, TIMESTAMP '2026-09-06 15:05:59.679537',
       U&'\6e05\7406\6821\56ed\516c\5171\533a\57df\5e76\5b8c\6210\5783\573e\5206\7c7b\5ba3\4f20\3002',
       TIMESTAMP '2026-09-13 18:05:59.677527', U&'\5927\5b66\751f\6d3b\52a8\4e2d\5fc3\95e8\53e3',
       TIMESTAMP '2026-09-11 15:05:59.677527', TIMESTAMP '2026-09-13 15:05:59.677527',
       'PUBLISHED', U&'\6821\56ed\73af\4fdd\5fd7\613f\6d3b\52a8',
       TIMESTAMP '2026-09-06 15:05:59.679537', 0,
       (SELECT id FROM app_user WHERE username = 'teacher01')
WHERE NOT EXISTS (
    SELECT 1 FROM activity a
    JOIN app_user u ON u.id = a.organizer_id
    WHERE a.title = U&'\6821\56ed\73af\4fdd\5fd7\613f\6d3b\52a8'
      AND u.username = 'teacher01'
);

INSERT INTO activity
    (capacity, created_at, description, end_time, location, registration_deadline,
     start_time, status, title, updated_at, version, organizer_id)
SELECT 30, TIMESTAMP '2026-09-06 15:06:53.786107', '1',
       TIMESTAMP '2026-09-23 15:06:00', '1', TIMESTAMP '2026-09-07 15:06:00',
       TIMESTAMP '2026-09-10 15:06:00', 'PUBLISHED', '1',
       TIMESTAMP '2026-09-06 15:06:57.720783', 1,
       (SELECT id FROM app_user WHERE username = 'zzzz')
WHERE NOT EXISTS (
    SELECT 1 FROM activity a
    JOIN app_user u ON u.id = a.organizer_id
    WHERE a.title = '1' AND u.username = 'zzzz'
);

INSERT INTO registration (registered_at, activity_id, student_id)
SELECT TIMESTAMP '2026-09-08 14:05:45.975646', a.id, s.id
FROM activity a
JOIN app_user organizer ON organizer.id = a.organizer_id
JOIN app_user s ON s.username = 'student01'
WHERE a.title = U&'\6821\56ed\73af\4fdd\5fd7\613f\6d3b\52a8'
  AND organizer.username = 'teacher01'
  AND NOT EXISTS (
      SELECT 1 FROM registration r
      WHERE r.activity_id = a.id AND r.student_id = s.id
  );
