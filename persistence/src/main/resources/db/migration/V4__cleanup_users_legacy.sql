UPDATE users
SET nickname = COALESCE(nickname, display_name, username)
WHERE nickname IS NULL;

ALTER TABLE users
DROP COLUMN username;

ALTER TABLE users
DROP COLUMN display_name;
