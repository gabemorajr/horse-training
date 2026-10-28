-- V1__initial_schema.sql
-- Flyway migration: initial schema for the horse training app (version 1)

-- People who can log in. Dad and I for now (role = 'ADMIN').
-- Later, clients get role = 'CLIENT' and are linked via clients.user_id.
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'ADMIN'
                    CHECK (role IN ('ADMIN', 'CLIENT')),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Horse owners / paying customers.
CREATE TABLE clients (
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(30),
    notes           TEXT,
    user_id         BIGINT UNIQUE REFERENCES users(id),  -- null until client logins exist
    archived_at     TIMESTAMPTZ,                          -- soft delete: keep history
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Horses exist independently of any one training period,
-- so a horse that comes back next year keeps its history.
CREATE TABLE horses (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    breed           VARCHAR(100),
    sex             VARCHAR(20) CHECK (sex IN ('MARE', 'GELDING', 'STALLION', 'FILLY', 'COLT')),
    color           VARCHAR(50),
    birth_year      INT CHECK (birth_year BETWEEN 1980 AND 2100),
    notes           TEXT,
    archived_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- A period when a horse is in training, and which client is responsible for it.
-- Putting the client here (not on horses) handles ownership changes cleanly:
-- each stay records who the client was at that time.
CREATE TABLE training_stays (
    id              BIGSERIAL PRIMARY KEY,
    horse_id        BIGINT NOT NULL REFERENCES horses(id),
    client_id       BIGINT NOT NULL REFERENCES clients(id),
    arrival_date    DATE   NOT NULL,
    departure_date  DATE,                                 -- null = currently in training
    goals           TEXT,
    -- Billing terms for this stay. Stored per stay so a rate change
    -- for new clients doesn't rewrite what existing clients agreed to.
    monthly_rate    NUMERIC(10, 2) NOT NULL CHECK (monthly_rate >= 0),
    feed_included   BOOLEAN        NOT NULL DEFAULT TRUE,  -- false = client supplies feed
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (departure_date IS NULL OR departure_date >= arrival_date)
);

-- A horse can only have one active stay at a time (enforced by the database,
-- not just application code).
CREATE UNIQUE INDEX one_active_stay_per_horse
    ON training_stays (horse_id)
    WHERE departure_date IS NULL;

CREATE INDEX idx_training_stays_client ON training_stays (client_id);
CREATE INDEX idx_training_stays_horse  ON training_stays (horse_id);  -- a horse's full stay history

-- Individual training sessions, logged against a stay.
CREATE TABLE training_sessions (
    id                BIGSERIAL PRIMARY KEY,
    stay_id           BIGINT NOT NULL REFERENCES training_stays(id),
    session_date      DATE   NOT NULL,
    duration_minutes  INT CHECK (duration_minutes > 0),
    focus             VARCHAR(255),        -- e.g. "groundwork", "lead changes"
    notes             TEXT,
    created_by        BIGINT NOT NULL REFERENCES users(id),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_training_sessions_stay_date ON training_sessions (stay_id, session_date DESC);
