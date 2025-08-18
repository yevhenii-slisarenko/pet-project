CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(30) NOT NULL UNIQUE,
                       password VARCHAR(80) NOT NULL,
                       email VARCHAR(50) UNIQUE,
                       created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       registration_status VARCHAR(30) NOT NULL,
                       last_login TIMESTAMP,
                       deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE posts (
                       id BIGSERIAL PRIMARY KEY,
                       user_id INTEGER NOT NULL ,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       deleted BOOLEAN NOT NULL DEFAULT false,
                       likes INTEGER NOT NULL  DEFAULT 0,
                       created_by VARCHAR(50),
                       image VARCHAR(2048),
                       comments_count INTEGER NOT NULL DEFAULT 0,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       UNIQUE (title)
);

CREATE TABLE comments (
                          id BIGSERIAL PRIMARY KEY,
                          post_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          message TEXT NOT NULL,
                          created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          deleted BOOLEAN NOT NULL DEFAULT false,
                          created_by VARCHAR(50),
                          replied_id BIGINT NULL,
                          FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                          FOREIGN KEY (replied_id) REFERENCES comments(id) ON DELETE CASCADE
);
CREATE INDEX idx_comments_post_id ON comments (post_id);
CREATE INDEX idx_comments_user_id ON comments (user_id);


CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL,
                       user_system_role VARCHAR(64) NOT NULL,
                       active BOOLEAN NOT NULL DEFAULT true,
                       created_by VARCHAR(50) NOT NULL
);

CREATE TABLE users_roles (
                       user_id BIGINT NOT NULL,
                       role_id INT NOT NULL,
                       PRIMARY KEY (user_id, role_id),
                       FOREIGN KEY (user_id) REFERENCES users (id),
                       FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE refresh_token (
                       id SERIAL PRIMARY KEY,
                       token VARCHAR(128) NOT NULL,
                       created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       user_id BIGINT NOT NULL,
                       CONSTRAINT FK_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                       CONSTRAINT refresh_token_UNIQUE UNIQUE (user_id, id)
);

CREATE TABLE email_verification_token (
                                          id SERIAL PRIMARY KEY,
                                          token VARCHAR(255) NOT NULL UNIQUE,
                                          created TIMESTAMP NOT NULL,
                                          expires TIMESTAMP NOT NULL,
                                          user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE
);


INSERT INTO users (username, password, email, created, updated, registration_status, last_login, deleted) VALUES
                       ('super_admin', '$2a$10$2ZN4Y6FewSkqFwMDdzmTJuQlg/JBFgXpBLkWBOkVtaeJAFiBGA7F.', 'superadmin@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false),
                       ('admin', '$2a$10$2ZN4Y6FewSkqFwMDdzmTJuQlg/JBFgXpBLkWBOkVtaeJAFiBGA7F.', 'admin@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false),
                       ('user', '$2a$10$2ZN4Y6FewSkqFwMDdzmTJuQlg/JBFgXpBLkWBOkVtaeJAFiBGA7F.', 'user@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false),
                       ('user2', '$2a$10$2ZN4Y6FewSkqFwMDdzmTJuQlg/JBFgXpBLkWBOkVtaeJAFiBGA7F.', 'user2@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false);





-- INSERT INTO posts (user_id, title, content, created, updated, deleted, likes, image) VALUES
--                        (1, 'First Post', 'This is content of the first post', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 6, NULL),

INSERT INTO roles (name, user_system_role, created_by) VALUES
                       ('SUPER_ADMIN', 'SUPER_ADMIN', 'SUPER_ADMIN'),
                       ('ADMIN', 'ADMIN', 'SUPER_ADMIN'),
                       ('USER', 'USER', 'SUPER_ADMIN');

INSERT INTO users_roles (user_id, role_id) VALUES
                       (1, 1),
                       (2, 2),
                       (3, 3),
                       (4, 3);

-- INSERT INTO comments (post_id, user_id, message, replied_id) VALUES
--                                                                  (1, 1, 'Test comment for first Post', null),
