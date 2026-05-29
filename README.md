# Global Class Offering Booking System

A production-ready RESTful backend service for a global live-learning platform where teachers conduct online classes for students across different timezones. Built with Spring Boot and PostgreSQL.

---

## 🚀 Tech Stack Used

- **Language:** Java 17
- **Framework:** Spring Boot 3.x
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Database Migration:** Flyway
- **API Documentation:** Swagger UI (OpenAPI 3)
- **Boilerplate Reduction:** Lombok

---

## ⚙️ Setup Instructions & Steps to Run Locally

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

### 1. Database Setup

Create a database:

```sql
CREATE DATABASE class_booking_db;
```

### 2. Environment Variables

Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:class_booking_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:your_password}
```

### Environment Variables

| Variable | Description |
|-----------|-------------|
| DB_HOST | PostgreSQL Host |
| DB_PORT | PostgreSQL Port |
| DB_NAME | Database Name |
| DB_USERNAME | Database Username |
| DB_PASSWORD | Database Password |

### 3. Run the Application

```bash
mvn spring-boot:run
```

Flyway will automatically create all database tables and insert sample data.

### 4. Swagger Documentation

```text
http://localhost:8080/swagger-ui.html
```

---

## 📌 API Endpoints

### Teacher APIs

#### Create Offering

```http
POST /api/teachers/{teacherId}/offerings
```

#### Add Sessions

```http
POST /api/offerings/{offeringId}/sessions
```

#### Get Teacher Offerings

```http
GET /api/teachers/{teacherId}/offerings
```

---

### Parent APIs

#### Get Available Offerings

```http
GET /api/parents/{parentId}/offerings
```

#### Book Offering

```http
POST /api/parents/{parentId}/bookings
```

#### Get Parent Bookings

```http
GET /api/parents/{parentId}/bookings
```

---

## 🗄️ Database Schema Overview

### teachers

Stores teacher information and timezone.

```text
id
name
email
timezone
```

### parents

Stores parent/student information and timezone.

```text
id
name
email
timezone
```

### courses

Stores course catalog.

```text
id
name
description
```

Examples:

- Python Coding
- Art Drawing
- Public Speaking

### offerings

Represents a schedulable batch.

```text
id
course_id
teacher_id
title
created_at
```

### sessions

Stores actual class timings.

```text
id
offering_id
teacher_id
start_time_utc
end_time_utc
```

Important:

- Stored strictly in UTC
- Each session belongs to one offering

### bookings

Stores parent bookings.

```text
id
parent_id
offering_id
status
created_at
```

Important:

- Booking happens at Offering Level

---

## 🔗 Database Relationships

```text
Teacher
   |
   └── Offerings
           |
           └── Sessions

Course
   |
   └── Offerings

Parent
   |
   └── Bookings
             |
             └── Offerings
```

### Cardinality

```text
Teacher 1 ---> N Offerings

Offering 1 ---> N Sessions

Parent N ---> N Offerings
(via Bookings)
```

---

## 🔒 Database Constraints

```sql
UNIQUE(parent_id, offering_id)
```

Purpose:

Prevent the same parent from booking the same offering multiple times.

---

## 🧠 Core Engineering Approaches

### 1. Timezone Handling Approach

#### Golden Rule

All timestamps are stored in UTC.

Database:

```sql
TIMESTAMP WITH TIME ZONE
```

Java:

```java
Instant
```

#### Teacher Flow

```text
Teacher Local Time
        ↓
Teacher Timezone
        ↓
Convert To UTC
        ↓
Store In Database
```

#### Parent Flow

```text
UTC Time From Database
        ↓
Read Parent Timezone
        ↓
Convert To Parent Local Time
        ↓
Return Response
```

Benefits:

- Single source of truth
- No timezone ambiguity
- Easy global scheduling

---

### 2. Concurrency Handling Approach

To handle simultaneous booking attempts safely:

```java
@Transactional
```

and

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

are used.

#### Why Lock Parent Row?

If the same parent sends two booking requests simultaneously:

```text
Request A
      ↓
Locks Parent Row
      ↓
Performs Validation
      ↓
Saves Booking
      ↓
Commits

Request B
      ↓
Waits
      ↓
Rechecks Validation
      ↓
Proceeds Safely
```

Benefits:

- Prevents race conditions
- Prevents overlapping bookings
- Maintains database consistency

---

### 3. Time Conflict Detection Logic

When booking an offering:

```text
Fetch Parent's Existing Sessions
               ↓
Fetch New Offering Sessions
               ↓
Compare Time Intervals
```

Two sessions overlap if:

```text
(existingStart < newEnd)
AND
(newStart < existingEnd)
```

Example:

```text
Existing Session:
5:00 PM - 6:00 PM

New Session:
5:30 PM - 6:30 PM
```

Result:

```text
Conflict Detected
Booking Rejected
```

HTTP Response:

```http
409 CONFLICT
```

---

## 🔄 Booking Workflow

```text
Parent Requests Booking
        ↓
Start Transaction
        ↓
Lock Parent Row
(PESSIMISTIC_WRITE)
        ↓
Check Duplicate Booking
        ↓
Fetch Existing Sessions
        ↓
Fetch New Offering Sessions
        ↓
Run Conflict Detection
        ↓
If Conflict Exists
        ↓
Reject Booking

Else
        ↓
Save Booking
        ↓
Commit Transaction
```

---

## 📂 Project Structure

```text
src/main/java/com/class_booking

├── controller
├── service
├── repository
├── entity
├── dto
├── exception
├── config
└── util
```

---

## 📌 Assumptions Made

### Frontend Delegation

For simplicity, the backend returns session timings converted to the parent's timezone.

In a real-world production system:

```text
Backend → UTC ISO Timestamp
Frontend → Local Formatting
```

### Pre-populated Data

Flyway migrations insert sample data:

- Teachers
- Parents
- Courses

with different timezones.

---

## 🧪 Testing Strategy

### Unit Tests

- Conflict Detection Logic
- Timezone Conversion Logic
- Validation Logic

### Integration Tests

- Create Offering
- Add Sessions
- Get Offerings
- Book Offering
- Get Bookings

### Concurrency Tests

Simulate parallel booking requests.

Expected Result:

```text
One Valid Request Succeeds
Conflicting Request Fails
```

---

## 📖 API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Optional:

- Postman Collection Included

---

## 🚀 Future Improvements

- JWT Authentication
- Role Based Access Control (RBAC)
- Redis Caching
- Docker Deployment
- CI/CD Pipeline
- Email Notifications
- Event-Driven Architecture

---

## ✅ Assignment Requirements Covered

- Teacher APIs
- Parent APIs
- Offering-Level Booking
- UTC Time Storage
- Timezone Conversion
- Conflict Detection
- Concurrency Handling
- Duplicate Booking Prevention
- Validation
- Exception Handling
- Swagger Documentation
- Flyway Migration
- Database Design
- Testing Strategy
- Production-Style Architecture

---

## 👨‍💻 Author

Backend Engineering Assignment

Global Class Offering Booking System

Built using Java, Spring Boot, PostgreSQL, Flyway, and Swagger.