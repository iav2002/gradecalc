# Grade Calculator for Irish University Students

A personal web application to calculate, track, and predict academic grades across different Irish university grading systems.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Motivation](#motivation)
3. [Features](#features)
4. [Supported Universities](#supported-universities)
5. [Tech Stack](#tech-stack)
6. [Architecture](#architecture)
7. [Database Schema](#database-schema)
8. [Development Roadmap](#development-roadmap)
9. [To-Do List](#to-do-list)
10. [Setup Instructions](#setup-instructions)
11. [Deployment Plan](#deployment-plan)
12. [Learning Notes](#learning-notes)

---

## Project Overview

This application helps Irish university students answer two critical questions:

1. **"What grade do I currently have?"** — Calculate the "banked" percentage from completed assessments
2. **"What do I need on remaining assessments to hit my target?"** — Predict required scores

Different Irish universities use different grading scales (UCD uses a 4.2 GPA system, TCD uses Roman numerals, TUD uses percentage bands). This app handles all of them through a flexible Strategy Pattern architecture.

---

## Motivation

- Students often lose track of their progress across multiple modules
- Manual calculations are error-prone, especially with weighted assessments
- Existing tools don't support Irish-specific grading systems
- Personal project to learn/refresh Spring Boot development
- End goal: self-hosted on a homelab server for personal and limited user access

---

## Features

### Core Features (MVP)
- User registration and authentication
- Module management (add, edit, delete modules)
- Assessment tracking with weights and marks
- "Banked vs Required" grade calculations
- Support for multiple grading systems per user preference

### Enhanced Features (Post-MVP)
- Visual dashboard with Chart.js (stacked bar charts showing banked/required/lost marks)
- Semester filtering and organization
- Grade prediction scenarios ("What if I get X on the final?")
- Module code lookup (pre-populated module names)
- Export to PDF/CSV

### Future Considerations
- Mobile-responsive design
- API endpoints for potential mobile app
- Multi-user household support
- Grade history and trends over semesters

---

## Supported Universities

| University | Code | Grading System | Scale |
|------------|------|----------------|-------|
| University College Dublin | UCD | GPA | 0.00 - 4.20 |
| Trinity College Dublin | TCD | Roman Numerals | I, II.1, II.2, III, F |
| Technological University Dublin | TUD | Percentage Bands | Distinction, Merit, Pass |
| Dublin City University | DCU | Percentage | 0 - 100% |
| University of Galway | NUIG | Percentage | 0 - 100% |
| University College Cork | UCC | Percentage | 0 - 100% |
| Maynooth University | MU | Percentage | 0 - 100% |
| Standard (Generic) | STANDARD | Letter Grades | A, B, C, D, F |

Each university has specific grade boundaries stored in the database, allowing easy updates without code changes.

---

## Tech Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| Language | Java 17 (LTS) | Core application language |
| Framework | Spring Boot 3.5 | Application framework, embedded server |
| Build Tool | Maven | Dependency management, build lifecycle |
| Security | Spring Security | Authentication, authorization, CSRF |
| ORM | Spring Data JPA / Hibernate | Database abstraction, entity mapping |
| Database (Dev) | H2 | In-memory database for development |
| Database (Prod) | PostgreSQL | Persistent storage for production |
| Templates | Thymeleaf | Server-side HTML rendering |
| Visualization | Chart.js | Client-side charts and graphs |
| CSS | TBD (possibly Tailwind or Bootstrap) | Styling |

---

## Architecture

The application follows a layered architecture with clear separation of concerns.

### Layer Overview

```
Browser
    | HTTP
Spring Security (authentication/authorization)
    | allowed
Controllers (handle requests, return views)
    | delegate
Services (business logic)
    | data access
Repositories (database queries)
    | SQL
Database (H2 or PostgreSQL)
```

### Key Design Patterns

**Strategy Pattern** — Used for grading calculations. Each university's grading logic is encapsulated in its own strategy class implementing a common interface.

```
GradingStrategy (interface)
    |-- UcdStrategy
    |-- TcdStrategy
    |-- TudStrategy
    |-- StandardStrategy
```

**Repository Pattern** — Spring Data JPA repositories abstract all database operations.

**MVC Pattern** — Controllers handle HTTP, Services handle logic, Thymeleaf handles views.

### Diagrams

- `database-schema.mermaid` — Entity-relationship diagram
- `application-architecture.mermaid` — Full application flow diagram

---

## Database Schema

### Tables

**USERS**
- Stores account information and university preference
- One user has many modules

**MODULES**
- Stores academic modules (courses)
- Linked to a user
- One module has many assessments

**ASSESSMENTS**
- Stores individual assessments (exams, assignments, projects)
- Tracks weight, obtained marks, and completion status
- Linked to a module

**GRADING_SCALES**
- Reference table for grade boundaries
- Allows adding new universities without code changes
- Stores min/max percentages for each grade label

### Relationships

```
USERS (1) ---- (many) MODULES (1) ---- (many) ASSESSMENTS
                                    
GRADING_SCALES ---- referenced by ---- USERS (via university_preset)
```


## Deployment Plan

**Target Environment:** Self-hosted Linux server (homelab)

**Expected Scale:** Personal use, possibly a few friends 

**Database:** PostgreSQL running on same server or separate container

**Access:** Local network initially, potential external access via reverse proxy

**Security Considerations:**
- Strong passwords enforced
- HTTPS via Let's Encrypt
- Firewall rules limiting access
- Regular backups of PostgreSQL data
