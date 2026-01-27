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

---

## Development Roadmap

### Phase 1: Project Setup and Spring Boot Anatomy
- Create project via Spring Initializr
- Understand folder structure and conventions
- Run application successfully
- Learn: IoC, Dependency Injection, Application Context

### Phase 2: User Entity and Security Foundation
- Configure H2 database
- Create User entity (code-first approach)
- Set up Spring Security with form login
- Implement registration flow
- Learn: @Entity, UserDetailsService, password encoding

### Phase 3: Domain Entities and Repositories
- Create Module and Assessment entities
- Define JPA relationships (@OneToMany, @ManyToOne)
- Create Repository interfaces
- Implement GradingScale entity
- Learn: JPA annotations, Hibernate auto-schema

### Phase 4: Service Layer and Strategy Pattern
- Create GradingStrategy interface
- Implement university-specific strategies
- Build GradeService for calculations
- Learn: Strategy Pattern, service layer separation

### Phase 5: Thymeleaf UI
- Create page templates (dashboard, modules, assessments)
- Build forms for data entry
- Integrate Chart.js visualizations
- Add navigation and fragments
- Learn: Thymeleaf syntax, MVC in practice

### Phase 6: PostgreSQL Migration
- Set up PostgreSQL (local or Supabase)
- Update connection configuration
- Switch ddl-auto to validate
- Test data persistence
- Learn: Environment configs, production database

### Phase 7: Deployment
- Package application as JAR
- Deploy to homelab Linux server
- Configure as systemd service
- Set up reverse proxy (Nginx)
- Configure SSL (optional)
- Learn: mvn package, environment variables, Linux services

---

## To-Do List

### Phase 1: Project Setup
- [x] Create Spring Boot project via Spring Initializr
- [x] Add dependencies: Web, Data JPA, H2, Thymeleaf, Security
- [x] Open project in IntelliJ
- [x] Understand project structure (src/main/java, resources, pom.xml)
- [x] Understand GradecalcApplication.java (entry point, @SpringBootApplication)
- [x] Understand pom.xml (dependencies, parent, starters)
- [x] Run application successfully on localhost:8080
- [x] Verify Spring Security default login works
- [x] Create project documentation (README.md)
- [x] Create database schema diagram (Mermaid)
- [x] Create application architecture diagram (Mermaid)

### Phase 2: User Entity and Security Foundation
- [ ] Configure application.properties (H2, JPA settings)
- [ ] Create User entity with JPA annotations
- [ ] Create UserRepository interface
- [ ] Implement CustomUserDetailsService
- [ ] Configure SecurityConfig (filter chain, password encoder)
- [ ] Create registration endpoint and form
- [ ] Create login page (custom, replace Spring default)
- [ ] Test user registration and login flow
- [ ] Verify H2 console shows user data

### Phase 3: Domain Entities and Repositories
- [ ] Create Module entity with User relationship
- [ ] Create Assessment entity with Module relationship
- [ ] Create GradingScale entity
- [ ] Create ModuleRepository interface
- [ ] Create AssessmentRepository interface
- [ ] Create GradingScaleRepository interface
- [ ] Seed GradingScale data for all universities
- [ ] Test relationships via H2 console

### Phase 4: Service Layer and Strategy Pattern
- [ ] Create GradingStrategy interface
- [ ] Implement UcdStrategy (4.2 GPA logic)
- [ ] Implement TcdStrategy (Roman numeral logic)
- [ ] Implement TudStrategy (percentage bands)
- [ ] Implement StandardStrategy (letter grades)
- [ ] Create UserService
- [ ] Create ModuleService
- [ ] Create AssessmentService
- [ ] Create GradeService (uses strategies)
- [ ] Write unit tests for grade calculations

### Phase 5: Thymeleaf UI
- [ ] Create base layout template with fragments
- [ ] Create navigation fragment
- [ ] Create home/dashboard page
- [ ] Create module list page
- [ ] Create module detail page (with assessments)
- [ ] Create add/edit module form
- [ ] Create add/edit assessment form
- [ ] Integrate Chart.js CDN
- [ ] Create stacked bar chart component
- [ ] Add CSS styling
- [ ] Test full user flow

### Phase 6: PostgreSQL Migration
- [ ] Install PostgreSQL locally OR set up Supabase
- [ ] Create production database
- [ ] Add PostgreSQL driver dependency
- [ ] Create application-prod.properties
- [ ] Update ddl-auto to validate
- [ ] Test connection and data persistence
- [ ] Export/migrate any test data

### Phase 7: Deployment
- [ ] Run mvn clean package
- [ ] Test JAR runs standalone
- [ ] Transfer JAR to homelab server
- [ ] Create systemd service file
- [ ] Configure environment variables for secrets
- [ ] Start and enable service
- [ ] Configure Nginx reverse proxy
- [ ] Set up SSL certificate (Let's Encrypt)
- [ ] Test from external network
- [ ] Document deployment process

---

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven (or use included mvnw wrapper)
- IntelliJ IDEA (recommended) or any IDE

### Running Locally

1. Clone or download the project
2. Open in IntelliJ
3. Wait for Maven to download dependencies
4. Run `GradecalcApplication.java`
5. Open `http://localhost:8080` in browser
6. Default login: username `user`, password shown in console

### Accessing H2 Console (Development)

1. Go to `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:gradecalc`
3. Username: `sa`
4. Password: (leave empty)

---

## Deployment Plan

**Target Environment:** Self-hosted Linux server (homelab)

**Expected Scale:** Personal use, possibly a few friends/family. No need for cloud scaling.

**Database:** PostgreSQL running on same server or separate container

**Access:** Local network initially, potential external access via reverse proxy

**Security Considerations:**
- Strong passwords enforced
- HTTPS via Let's Encrypt
- Firewall rules limiting access
- Regular backups of PostgreSQL data

---

## Learning Notes

Key concepts to remember from this project:

### Spring Boot Fundamentals
- **Inversion of Control (IoC):** Spring manages object creation and lifecycle
- **Dependency Injection:** Spring automatically provides dependencies via constructors or @Autowired
- **Application Context:** Container holding all Spring-managed beans
- **Starters:** Pre-bundled dependency sets (e.g., spring-boot-starter-web)
- **Convention over Configuration:** Follow folder conventions, Spring auto-configures

### Annotations Reference
- `@SpringBootApplication` — Entry point, combines @Configuration, @EnableAutoConfiguration, @ComponentScan
- `@Entity` — Marks a class as a JPA entity (database table)
- `@Repository` — Marks a class as a data access component
- `@Service` — Marks a class as a business logic component
- `@Controller` — Handles HTTP requests, returns view names
- `@RestController` — Returns data directly (JSON), not views

### JPA/Hibernate
- `ddl-auto=create-drop` — Dev mode, recreates schema each restart
- `ddl-auto=validate` — Prod mode, only validates schema matches entities
- `@OneToMany` / `@ManyToOne` — Define relationships between entities
- Repository interfaces auto-implement CRUD via Spring Data

### Spring Security
- Security filter chain intercepts all requests
- UserDetailsService loads user data for authentication
- PasswordEncoder (BCrypt) hashes passwords
- CSRF protection enabled by default

---

## License

Personal project. Not licensed for distribution.

---

## Author

Ignacio — Learning Spring Boot, one phase at a time.
