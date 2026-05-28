CREATE TABLE teachers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    timezone VARCHAR(100) NOT NULL
);

CREATE TABLE parents (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    timezone VARCHAR(100) NOT NULL
);

CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE offerings (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGSERIAL REFERENCES courses(id),
    teacher_id BIGSERIAL REFERENCES teachers(id),
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    offering_id BIGSERIAL REFERENCES offerings(id),
    teacher_id BIGSERIAL REFERENCES teachers(id),
    start_time_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time_utc TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGSERIAL REFERENCES parents(id),
    offering_id BIGSERIAL REFERENCES offerings(id),
    status VARCHAR(50) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (parent_id, offering_id)
);