-- Sessions can be logged before user logins exist.
-- Once logins are added, a later migration will fill in created_by
-- for existing rows and restore NOT NULL.
ALTER TABLE training_sessions ALTER COLUMN created_by DROP NOT NULL;