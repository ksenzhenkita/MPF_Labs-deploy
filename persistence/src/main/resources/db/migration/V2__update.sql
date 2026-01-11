CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     username VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(255)
    );

ALTER TABLE comments
    ADD COLUMN user_id BIGINT;

ALTER TABLE comments
    ADD CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE SET NULL;
