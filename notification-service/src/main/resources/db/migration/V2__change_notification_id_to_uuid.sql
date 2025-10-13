ALTER TABLE notification
ALTER COLUMN id TYPE uuid
USING id::uuid;