CREATE TABLE users (
                       id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email       VARCHAR(255) UNIQUE NOT NULL,
                       password    VARCHAR(255) NOT NULL,
                       version     BIGINT NOT NULL DEFAULT 0,
                       full_name   VARCHAR(255),
                       created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE contacts (
                          id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          name        VARCHAR(255) NOT NULL,
                          email       VARCHAR(255),
                          phone       VARCHAR(50),
                          telegram_id VARCHAR(100),
                          created_at  TIMESTAMP DEFAULT NOW()
);