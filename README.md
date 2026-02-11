# Resume Analyzer API

A comprehensive backend service for analyzing resumes and comparing them with job descriptions using intelligent parsing algorithms and ATS (Applicant Tracking System) scoring mechanisms.

## Table of Contents
- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Architecture](#architecture)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [ATS Scoring Logic](#ats-scoring-logic)
- [Sample Request & Response Payloads](#sample-request--response-payloads)
- [Database Schema](#database-schema)
- [Error Handling](#error-handling)
- [Contributing](#contributing)

---

## Project Overview

The Resume Analyzer API is a robust Spring Boot application designed to help job seekers optimize their resumes and understand how well they match specific job requirements. The system provides:

- **Intelligent Resume Parsing**: Extracts skills, experience, education, and projects from resume text
- **ATS Score Calculation**: Evaluates resume quality based on key components
- **Job Description Matching**: Compares resume skills against job requirements
- **Improvement Suggestions**: Provides actionable feedback for resume enhancement
- **Data Persistence**: Stores analysis results for future reference

### Key Capabilities
- Section-aware parsing (distinguishes between resume sections)
- Case-insensitive skill matching
- Duplicate removal with order preservation
- Experience calculation from date ranges
- Match percentage computation
- RESTful API design with comprehensive error handling

---

## Tech Stack

### Programming Languages
- **Java** (JDK 21)

### Frameworks & Libraries
- **Spring Boot** - Core framework for building the application
- **Spring Data JPA** - Data persistence and ORM
- **Hibernate** - ORM implementation
- **Spring Web** - RESTful API development
- **Lombok** - Boilerplate code reduction

### Architecture & Design
- **Microservices Architecture** - Scalable, modular design
- **RESTful API** - Standard HTTP methods and status codes
- **Layered Architecture** - Controller → Service → Repository pattern
- **DTO Pattern** - Data Transfer Objects for API communication

### Developer Tools & Database
- **IntelliJ IDEA** - Primary IDE
- **Postman** - API testing and documentation
- **PostgreSQL** - Relational database for production
- **MySQL** - Alternative database support

### Version Control
- **Git** - Source code management
- **GitHub** - Repository hosting

---

## Features

### 1. Resume Analysis
- Extracts technical skills from dedicated skills section
- Calculates total work experience from date ranges
- Identifies education qualifications
- Lists projects with timeline information
- Generates ATS score (0-100)
- Provides improvement suggestions

### 2. Job Description Comparison
- Extracts required skills from job postings
- Matches resume skills against job requirements
- Identifies missing skills
- Calculates match percentage
- Offers targeted recommendations

### 3. Data Management
- Paginated retrieval of all analyses
- Individual record lookup by ID
- Soft deletion support
- Timestamp tracking for all records

### 4. Smart Parsing Features
- Section-aware extraction (TECHNICAL SKILLS, EXPERIENCE, PROJECTS, EDUCATION)
- Multi-format skill detection (comma-separated, bullet points, categorized)
- Case-insensitive comparison with original case preservation
- Duplicate removal while maintaining order
- Date range parsing for experience calculation

---

## Architecture

### System Architecture
```
┌─────────────────┐
│   REST Client   │
│  (Postman/UI)   │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│  ResumeAnalyzerController   │
│  - Request validation       │
│  - Response formatting      │
│  - Exception handling       │
└────────────┬────────────────┘
             │
             ▼
┌─────────────────────────────┐
│ ResumeAnalyzerServiceImpl   │
│  - Business logic           │
│  - Resume parsing           │
│  - Score calculation        │
│  - Job matching             │
└────────────┬────────────────┘
             │
      ┌──────┴──────┐
      ▼             ▼
┌──────────────┐ ┌──────────┐
│ ResumeParser │ │  Repos   │
│  - Skills    │ │  - JPA   │
│  - Education │ │  - CRUD  │
│  - Projects  │ │          │
│  - Experience│ │          │
└──────────────┘ └────┬─────┘
                      │
                      ▼
              ┌───────────────┐
              │  PostgreSQL   │
              │   Database    │
              └───────────────┘
```

### Layer Responsibilities

**Controller Layer** (`ResumeAnalyzerController`)
- HTTP request/response handling
- Input validation
- Error response formatting
- API endpoint mapping

**Service Layer** (`ResumeAnalyzerServiceImpl`)
- Core business logic
- Resume text processing
- ATS score computation
- Job matching algorithms
- DTO conversions

**Helper Layer** (`ResumeParser`)
- Text parsing utilities
- Regex-based extraction
- Pattern matching
- Data normalization

**Repository Layer** (`ResumeAnalysisRepository`, `JobMatchRepository`)
- Database operations
- JPA entity management
- Query execution

**Entity Layer** (`ResumeAnalysisEntity`, `JobMatchEntity`)
- Database table mapping
- Relationships definition
- Field constraints

---

## Setup Instructions

### Prerequisites
- JDK 21 or higher
- Maven 3.6+
- PostgreSQL 12+ 
- Git
- Postman (for API testing)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/DhananjayKumar20/Smart_Resume_Analyzer.git -b master .
   cd resume-analyzer-api
   ```

2. **Configure Database**
   
   Update `application.properties` or `application.yml`:
   ```properties
   # PostgreSQL Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/resume_analyzer_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=org.postgresql.Driver
   
   # JPA/Hibernate Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   
   # Server Configuration
   server.port=8080
   ```

3. **Create Database**
   ```sql
   CREATE DATABASE resume_analyzer_db;
   ```

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or run the JAR:
   ```bash
   java -jar target/resume-analyzer-api-1.0.0.jar
   ```

6. **Verify Installation**
   ```bash
   curl http://localhost:8080/api/health
   ```
   
   Expected Response:
   ```json
   {
     "data": "Resume Analyzer API is running!",
     "error": null
   }
   ```

---

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Response Structure
All API responses follow a consistent structure:

```json
{
  "data": <Response Object or null>,
  "error": {
    "message": "Error description"
  } or null
}
```

---

### Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/analyze-resume` | Analyze a resume and generate ATS score |
| POST | `/compare-job-description` | Compare resume with job description |
| GET | `/analyze-resume` | Get all resume analyses (paginated) |
| GET | `/{id}` | Get specific resume analysis by ID |
| GET | `/compare-job-description` | Get all job comparisons (paginated) |
| GET | `/job-matches/{id}` | Get specific job match by ID |
| DELETE | `/analyze-resume/{id}` | Delete resume analysis by ID |
| DELETE | `/compare-job-description/{id}` | Delete job match by ID |
| GET | `/health` | Health check endpoint |

---

### 1. Analyze Resume

Analyzes resume text to extract skills, experience, education, projects, and calculates ATS score.

**Endpoint:** `POST /api/analyze-resume`

**Request Body:**
```json
{
  "resumeText": "string (minimum 50 characters)"
}
```

**Success Response:** `200 OK`
```json
{
  "data": {
    "resumeAnalyzeId": 8,
    "skills": [
      "C/C++",
      "Java",
      "HTML",
      "CSS",
      "Spring Boot",
      "Spring Security",
      "OAuth2",
      "Spring Data JPA",
      "REST API",
      "Kafka",
      "Docker",
      "Microservices",
      "Git",
      "GitHub"
    ],
    "experience": "10 months",
    "education": [
      "Master",
      "MCA",
      "Bachelor",
      "BCA"
    ],
    "projects": [
      "Betting Service Platform",
      "Split and Steal Game Platform"
    ],
    "atsScore": 100,
    "improvementSuggestions": [],
    "createdAt": "2026-02-11T13:09:39.8123019"
  },
  "error": null
}
```

**Error Responses:**

`400 Bad Request` - Invalid input
```json
{
  "data": null,
  "error": {
    "message": "Resume text must not be empty"
  }
}
```

`500 Internal Server Error` - Processing failure
```json
{
  "data": null,
  "error": {
    "message": "Failed to analyze resume: [error details]"
  }
}
```

---

### 2. Compare Job Description

Compares resume skills with job description requirements and provides match percentage.

**Endpoint:** `POST /api/compare-job-description`

**Request Body:**
```json
{
  "resumeText": "string (minimum 50 characters)",
  "jobDescriptionText": "string"
}
```

**Success Response:** `201 Created`
```json
{
  "data": {
    "jobDescriptionCompareId": 4,
    "matchedSkills": [
      "Java",
      "Spring Boot",
      "RESTful APIs",
      "Microservices architecture",
      "Hibernate",
      "JPA",
      "SQL",
      "CI/CD"
    ],
    "missingSkills": [
      "Database optimization",
      "Unit testing and integration testing",
      "Agile methodology"
    ],
    "matchPercentage": 27.03,
    "improvementSuggestions": [
      "Add these key missing skills: Database optimization, Unit testing and integration testing, CI and 2 more",
      "Your resume matches less than 50% of job requirements. Consider gaining experience in missing skills."
    ],
    "createdAt": "2026-02-11T12:50:37.169402"
  },
  "error": null
}
```

**Error Responses:**

`400 Bad Request` - Invalid input
```json
{
  "data": null,
  "error": {
    "message": "Job description text must not be empty"
  }
}
```

`500 Internal Server Error` - Processing failure
```json
{
  "data": null,
  "error": {
    "message": "Failed to compare job description: [error details]"
  }
}
```

---

### 3. Get All Resume Analyses

Retrieves all resume analyses with pagination support.

**Endpoint:** `GET /api/analyze-resume`

**Query Parameters:**
- `page` (optional, default: 0) - Page number (0-indexed)
- `size` (optional, default: 10) - Number of items per page

**Example Request:**
```
GET /api/analyze-resume?page=0&size=10
```

**Success Response:** `200 OK`
```json
{
  "data": {
    "content": [
      {
        "resumeAnalyzeId": 8,
        "skills": ["Java", "Spring Boot", "Docker"],
        "experience": "10 months",
        "education": ["MCA", "BCA"],
        "projects": ["Betting Service Platform"],
        "atsScore": 100,
        "improvementSuggestions": [],
        "createdAt": "2026-02-11T13:09:39.812302"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "size": 10,
    "number": 0,
    "numberOfElements": 1,
    "empty": false
  },
  "error": null
}
```

**Error Response:** `500 Internal Server Error`
```json
{
  "data": null,
  "error": {
    "message": "Failed to fetch resumes: [error details]"
  }
}
```

---

### 4. Get Resume Analysis by ID

Retrieves a specific resume analysis by its ID.

**Endpoint:** `GET /api/{id}`

**Path Parameters:**
- `id` (required) - Resume analysis ID

**Example Request:**
```
GET /api/8
```

**Success Response:** `200 OK`
```json
{
  "data": {
    "resumeAnalyzeId": 8,
    "skills": [
      "Java",
      "Spring Boot",
      "Docker",
      "Microservices"
    ],
    "experience": "10 months",
    "education": ["MCA", "BCA"],
    "projects": ["Betting Service Platform"],
    "atsScore": 100,
    "improvementSuggestions": [],
    "createdAt": "2026-02-11T13:09:39.812302"
  },
  "error": null
}
```

**Error Responses:**

`404 Not Found`
```json
{
  "data": null,
  "error": {
    "message": "Resume not found with id: 8"
  }
}
```

`500 Internal Server Error`
```json
{
  "data": null,
  "error": {
    "message": "Unexpected error: [error details]"
  }
}
```

---

### 5. Get All Job Comparisons

Retrieves all job description comparisons with pagination.

**Endpoint:** `GET /api/compare-job-description`

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Items per page

**Example Request:**
```
GET /api/compare-job-description?page=0&size=10
```

**Success Response:** `200 OK`
```json
{
  "data": {
    "content": [
      {
        "jobDescriptionCompareId": 4,
        "matchedSkills": ["Java", "Spring Boot", "Hibernate"],
        "missingSkills": ["Database optimization", "Unit testing"],
        "matchPercentage": 27.03,
        "improvementSuggestions": [
          "Add these key missing skills: Database optimization, Unit testing"
        ],
        "createdAt": "2026-02-11T12:50:37.169402"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true
  },
  "error": null
}
```

**Error Response:** `500 Internal Server Error`
```json
{
  "data": null,
  "error": {
    "message": "Failed to fetch job comparisons: [error details]"
  }
}
```

---

### 6. Get Job Match by ID

Retrieves a specific job comparison by its ID.

**Endpoint:** `GET /api/job-matches/{id}`

**Path Parameters:**
- `id` (required) - Job match ID

**Example Request:**
```
GET /api/job-matches/4
```

**Success Response:** `200 OK`
```json
{
  "data": {
    "jobDescriptionCompareId": 4,
    "matchedSkills": [
      "Java",
      "Spring Boot",
      "Hibernate",
      "JPA"
    ],
    "missingSkills": [
      "Database optimization",
      "Unit testing"
    ],
    "matchPercentage": 27.03,
    "improvementSuggestions": [
      "Add these key missing skills: Database optimization, Unit testing",
      "Your resume matches less than 50% of job requirements."
    ],
    "createdAt": "2026-02-11T12:50:37.169402"
  },
  "error": null
}
```

**Error Responses:**

`404 Not Found`
```json
{
  "data": null,
  "error": {
    "message": "Job match not found with id: 4"
  }
}
```

---

### 7. Delete Resume Analysis

Deletes a resume analysis by ID.

**Endpoint:** `DELETE /api/analyze-resume/{id}`

**Path Parameters:**
- `id` (required) - Resume analysis ID

**Example Request:**
```
DELETE /api/analyze-resume/8
```

**Success Response:** `200 OK`
```json
{
  "data": "Resume deleted successfully",
  "error": null
}
```

**Error Responses:**

`404 Not Found`
```json
{
  "data": null,
  "error": {
    "message": "Resume not found with id: 8"
  }
}
```

`500 Internal Server Error`
```json
{
  "data": null,
  "error": {
    "message": "Unexpected error occurred: [error details]"
  }
}
```

---

### 8. Delete Job Match

Deletes a job comparison by ID.

**Endpoint:** `DELETE /api/compare-job-description/{id}`

**Path Parameters:**
- `id` (required) - Job match ID

**Example Request:**
```
DELETE /api/compare-job-description/4
```

**Success Response:** `200 OK`
```json
{
  "data": "Job match deleted successfully",
  "error": null
}
```

**Error Responses:**

`404 Not Found`
```json
{
  "data": null,
  "error": {
    "message": "Job match not found with id: 4"
  }
}
```

---

### 9. Health Check

Verifies that the API is running and accessible.

**Endpoint:** `GET /api/health`

**Example Request:**
```
GET /api/health
```

**Success Response:** `200 OK`
```json
{
  "data": "Resume Analyzer API is running!",
  "error": null
}
```

---

## How to Use Postman Collection

A Postman collection is provided to quickly test all API endpoints for the **Smart Resume Analyzer API**.

**Steps to Import:**

1. Download the Postman collection JSON: [`Smart_Resume_Analyzer.postman_collection.json`](path/to/Smart_Resume_Analyzer.postman_collection.json)
2. Open **Postman**.
3. Click **Import → Raw Text → Paste the JSON above → Import** (or import via file).
4. The collection **“Smart Resume Analyzer API”** will appear in Postman.

---

**Test the APIs:**

### ✅ Health Check
- **GET** `/api/health`  
- **Purpose:** Verify that the backend is running.
- **Response Example:**
```json
{
  "data": "Resume Analyzer API is running!",
  "error": null
}


## ATS Scoring Logic

### Overview
The ATS (Applicant Tracking System) score is a numerical representation (0-100) of resume quality based on the presence and completeness of key resume components.

### Scoring Components

The system evaluates four critical resume sections:

| Component | Weight | Criteria |
|-----------|--------|----------|
| **Skills** | 30 points | Presence of technical skills in TECHNICAL SKILLS section |
| **Experience** | 30 points | Work experience with valid date ranges |
| **Education** | 20 points | Educational qualifications (degrees) |
| **Projects** | 20 points | Project entries with timelines |

### Calculation Algorithm

```java
private int calculateAtsScore(List<String> skills, String experience,
                              List<String> education, List<String> projects) {
    int atsScore = 0;
    
    // Skills evaluation (30 points)
    if (!skills.isEmpty()) {
        atsScore += 30;
    }
    
    // Experience evaluation (30 points)
    if (!experience.equals("Not specified")) {
        atsScore += 30;
    }
    
    // Education evaluation (20 points)
    if (!education.isEmpty()) {
        atsScore += 20;
    }
    
    // Projects evaluation (20 points)
    if (!projects.isEmpty()) {
        atsScore += 20;
    }
    
    // Cap at maximum score
    return Math.min(atsScore, 100);
}
```

### Score Interpretation

| Score Range | Rating | Interpretation |
|-------------|--------|----------------|
| 0-25 | Poor | Resume lacks critical information |
| 26-50 | Below Average | Missing multiple key sections |
| 51-75 | Average | Has most sections but needs improvement |
| 76-99 | Good | Well-structured with minor gaps |
| 100 | Excellent | Complete resume with all sections |

### Improvement Suggestions

The system generates targeted suggestions based on missing components:

**Skills Missing (0 points)**
```
"Add your technical skills to improve visibility."
```

**Experience Missing (0 points)**
```
"Mention your work experience with dates."
```

**Education Missing (0 points)**
```
"Add your education details."
```

**Projects Missing (0 points)**
```
"Add projects to showcase your practical experience."
```

### Example Score Calculation

**Resume with All Sections:**
- Skills: Java, Spring Boot, Docker ✓ (+30)
- Experience: "10 months" ✓ (+30)
- Education: MCA, BCA ✓ (+20)
- Projects: Betting Platform ✓ (+20)
- **Total: 100/100**

**Resume with Missing Projects:**
- Skills: Java, Python ✓ (+30)
- Experience: "2 years" ✓ (+30)
- Education: B.Tech ✓ (+20)
- Projects: [] ✗ (+0)
- **Total: 80/100**
- **Suggestion:** "Add projects to showcase your practical experience."

---

## Sample Request & Response Payloads

### 1. Analyze Resume - Complete Example

**Request:**
```bash
POST http://localhost:8080/api/analyze-resume
Content-Type: application/json

{
  "resumeText": "EXPERIENCE\n• Retax Infotech Pvt Ltd September 2025 - January 2026\nJava Developer Intern Remote\n◦ Developed a Zoho-type Cloud Accounting Bookkeeping Platform using Java, Spring Boot, Hibernate, MySQL, implementing optimized backend workflows, database indexing, and scalable multi-tenant architecture for high-performance processing.\n◦ Secure RESTful APIs and implemented advanced GST taxation (Interstate/Intrastate, Reverse Charge, TDS/TCS) and Organization, User Management, Expense, Student Management, Transaction tracking and other different modules with Spring Security, JWT, and RBAC, ensuring enterprise-grade authentication, compliance, and system reliability.\n• SolSoN Tech Pvt Ltd March 2025 - July 2025\nJava Developer Intern Meerut\n◦ Developed cloud-ready microservices for a Betting Service Platform using Java, Spring Boot, MySQL, and designed secure RESTful APIs with OAuth2, Keycloak, JWT, and RBAC, ensuring high availability, low latency, and reliable third-party integrations.\n◦ Utilized Apache Kafka for asynchronous, event-driven processing and implemented Docker-based microservice containerization, applying multithreading, concurrency optimization, and fault-tolerant design to ensure high-throughput, scalable, and reliable system deployments.\nPROJECTS\n• Betting Service Platform March 2025\nTools: Java, MySQL, Spring Boot, Hibernate, REST API, Microservices, Kafka, Docker\n◦ Designed and developed a scalable microservices-based Betting Platform that supports secure bet placement, game outcome resolution, and real-time analytics using Java, Spring Boot, and REST APIs.\n◦ Implemented RESTful APIs for placing bets, validating users, and resolving game outcomes, integrated with third-party services like Wallet and Game Engine using RestTemplate.\n◦ Integrated Apache Kafka for asynchronous event-driven processing and used Docker to containerize microservices, optimizing database transactions and performance for high-concurrency, real-time workloads.\n• Split and Steal Game Platform May 2025\nTools: Java, MySQL, Spring Boot, Hibernate, REST API, Microservices, Docker\n◦ Built a microservices-based multiplayer game platform using Java, Spring Boot, and RESTful APIs, enabling real-time sessions, decision collection, and outcome resolution, with session management, game logic, and third-party integrations (Wallet Media) via RestTemplate.\n◦ Integrated MySQL with Spring Data JPA for persistent session and decision data storage, and containerized the entire platform using Docker for consistent, scalable deployments.\nTECHNICAL SKILLS\n• Programming Languages/Web Technologies: C/C++, Java, HTML, CSS\n• Frameworks/Tools: Spring Boot, Spring Security, OAuth2, Spring Data JPA, REST API, Kafka, Docker\n• Architecture/Version Control: Microservices, Git, GitHub\n• Developer Tools/Database: VS Code, IntelliJ IDEA, Eclipse, Postman, MySQL, PostgreSQL\nEDUCATION\n• Meerut Institute of Engineering and Technology (MIET) September 2024 - July 2026\nMaster of Computer Applications(MCA) Meerut, India\n• Vidya Knowledge Park September 2021 - July 2024\nBachelor of Computer Applications(BCA) Meerut, India\n◦ CGPA: 7.3\nCERTIFICATIONS\n• Web Development From Internshala Training April 2023\n• Advanced Excel by RCPL Month 2023"
}
```

**Response:**
```json
{
  "data": {
    "resumeAnalyzeId": 8,
    "skills": [
      "C/C++",
      "Java",
      "HTML",
      "CSS",
      "Spring Boot",
      "Spring Security",
      "OAuth2",
      "Spring Data JPA",
      "REST API",
      "Kafka",
      "Docker",
      "Microservices",
      "Git",
      "GitHub",
      "VS Code",
      "IntelliJ IDEA",
      "Eclipse",
      "Postman",
      "MySQL",
      "PostgreSQL"
    ],
    "experience": "10 months",
    "education": [
      "Master",
      "MCA",
      "Bachelor",
      "BCA"
    ],
    "projects": [
      "Betting Service Platform",
      "Split and Steal Game Platform"
    ],
    "atsScore": 100,
    "improvementSuggestions": [],
    "createdAt": "2026-02-11T13:09:39.8123019"
  },
  "error": null
}
```

---

### 2. Compare Job Description - Complete Example

**Request:**
```bash
POST http://localhost:8080/api/compare-job-description
Content-Type: application/json

{
  "resumeText": "EXPERIENCE\n• Retax Infotech Pvt Ltd September 2025 - January 2026\nJava Developer Intern Remote\n◦ Developed a Zoho-type Cloud Accounting Bookkeeping Platform using Java, Spring Boot, Hibernate, MySQL, implementing optimized backend workflows, database indexing, and scalable multi-tenant architecture for high-performance processing.\n◦ Secure RESTful APIs and implemented advanced GST taxation (Interstate/Intrastate, Reverse Charge, TDS/TCS) and Organization, User Management, Expense, Student Management, Transaction tracking and other different modules with Spring Security, JWT, and RBAC, ensuring enterprise-grade authentication, compliance, and system reliability.\n• SolSoN Tech Pvt Ltd March 2025 - June 2025\nJava Developer Intern Meerut\n◦ Developed cloud-ready microservices for a Betting Service Platform using Java, Spring Boot, MySQL, and designed secure RESTful APIs with OAuth2, Keycloak, JWT, and RBAC, ensuring high availability, low latency, and reliable third-party integrations.\n◦ Utilized Apache Kafka for asynchronous, event-driven processing and implemented Docker-based microservice containerization, applying multithreading, concurrency optimization, and fault-tolerant design to ensure high-throughput, scalable, and reliable system deployments.\nPROJECTS\n• Betting Service Platform March 2025\nTools: Java, MySQL, Spring Boot, Hibernate, REST API, Microservices, Kafka, Docker\n◦ Designed and developed a scalable microservices-based Betting Platform that supports secure bet placement, game outcome resolution, and real-time analytics using Java, Spring Boot, and REST APIs.\n◦ Implemented RESTful APIs for placing bets, validating users, and resolving game outcomes, integrated with third-party services like Wallet and Game Engine using RestTemplate.\n◦ Integrated Apache Kafka for asynchronous event-driven processing and used Docker to containerize microservices, optimizing database transactions and performance for high-concurrency, real-time workloads.\n• Split and Steal Game Platform May 2025\nTools: Java, MySQL, Spring Boot, Hibernate, REST API, Microservices, Docker\n◦ Built a microservices-based multiplayer game platform using Java, Spring Boot, and RESTful APIs, enabling real-time sessions, decision collection, and outcome resolution, with session management, game logic, and third-party integrations (Wallet Media) via RestTemplate.\n◦ Integrated MySQL with Spring Data JPA for persistent session and decision data storage, and containerized the entire platform using Docker for consistent, scalable deployments.\nTECHNICAL SKILLS\n• Programming Languages/Web Technologies: C/C++, Java, Hibernate, HTML, CSS\n• Frameworks/Tools: Spring Boot, Spring Security, OAuth2, Spring Data JPA, Microservices architecture, Hibernate, JPA,SQL, REST API, Kafka, RESTful APIs, Hibernate/JPA, CI/CD, Docker\n• Architecture/Version Control: Microservices, Git, GitHub\n• Developer Tools/Database: VS Code, IntelliJ IDEA, Eclipse, Postman, MySQL, PostgreSQL\nEDUCATION\n• Meerut Institute of Engineering and Technology (MIET) September 2024 - July 2026\nMaster of Computer Applications(MCA) Meerut, India\n• Vidya Knowledge Park September 2021 - July 2024\nBachelor of Computer Applications(BCA) Meerut, India\n◦ CGPA: 7.3\nCERTIFICATIONS\n• Web Development From Internshala Training April 2023\n• Advanced Excel by RCPL Month 2023",
  
  "jobDescriptionText": "We are looking for a skilled Java Developer to join our team.\n\nResponsibilities:\n- Design, develop, and maintain backend services using Java and Spring Boot, delivering reliable RESTful APIs for internal and external clients.\n- Architect and implement microservices components, including service-to-service communication, resilience, and observability.\n- Implement persistence layers with Hibernate/JPA and optimize SQL queries for performance and scalability.\n- Write unit and integration tests, participate in peer code reviews, and enforce code quality and secure coding standards.\n- Collaborate with product owners, QA, and DevOps to deliver features through CI/CD pipelines and Agile ceremonies.\n- Diagnose and resolve production issues, perform performance tuning, and contribute to technical design discussions.\n\nRequired Skills:\n- Java, Spring Boot, RESTful APIs\n- Microservices architecture\n- Hibernate / JPA\n- SQL / Database optimization\n- Unit testing and integration testing\n- CI/CD and Agile methodology"
}
```

**Response:**
```json
{
  "data": {
    "jobDescriptionCompareId": 4,
    "matchedSkills": [
      "Java",
      "Spring Boot",
      "RESTful APIs",
      "Microservices architecture",
      "Hibernate",
      "JPA",
      "SQL",
      "Microservices",
      "Hibernate/JPA",
      "CI/CD"
    ],
    "missingSkills": [
      "Database optimization",
      "Unit testing and integration testing",
      "CI",
      "CD and Agile methodology",
      "Design",
      "develop",
      "Architect and implement microservices components",
      "including service-to-service communication",
      "resilience",
      "observability",
      "Write unit and integration tests",
      "participate in peer code reviews",
      "Collaborate with product owners",
      "QA",
      "CD pipelines and Agile ceremonies",
      "Diagnose and resolve production issues",
      "perform performance tuning",
      "contribute to technical design discussions",
      "Spring",
      "Boot",
      "RESTful",
      "APIs",
      "Implement",
      "DevOps",
      "CD",
      "Agile",
      "Unit"
    ],
    "matchPercentage": 27.03,
    "improvementSuggestions": [
      "Add these key missing skills: Database optimization, Unit testing and integration testing, CI, CD and Agile methodology, Design and 22 more",
      "Your resume matches less than 50% of job requirements. Consider gaining experience in missing skills."
    ],
    "createdAt": "2026-02-11T12:50:37.169402"
  },
  "error": null
}
```

---

### 3. Get All Resumes - Paginated

**Request:**
```bash
GET http://localhost:8080/api/analyze-resume?page=0&size=5
```

**Response:**
```json
{
  "data": {
    "content": [
      {
        "resumeAnalyzeId": 8,
        "skills": [
          "C/C++",
          "Java",
          "Spring Boot",
          "Docker"
        ],
        "experience": "10 months",
        "education": ["Master", "MCA", "Bachelor", "BCA"],
        "projects": ["Betting Service Platform"],
        "atsScore": 100,
        "improvementSuggestions": [],
        "createdAt": "2026-02-11T13:09:39.812302"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5,
      "sort": {
        "sorted": true,
        "empty": false,
        "unsorted": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "size": 5,
    "number": 0,
    "sort": {
      "sorted": true,
      "empty": false,
      "unsorted": false
    },
    "numberOfElements": 1,
    "empty": false
  },
  "error": null
}
```

---

### 4. Get Resume by ID

**Request:**
```bash
GET http://localhost:8080/api/8
```

**Response:**
```json
{
  "data": {
    "resumeAnalyzeId": 8,
    "skills": [
      "C/C++",
      "Java",
      "HTML",
      "CSS",
      "Spring Boot",
      "Spring Security",
      "OAuth2",
      "Spring Data JPA",
      "REST API",
      "Kafka",
      "Docker",
      "Microservices",
      "Git",
      "GitHub",
      "VS Code",
      "IntelliJ IDEA",
      "Eclipse",
      "Postman",
      "MySQL",
      "PostgreSQL"
    ],
    "experience": "10 months",
    "education": [
      "Master",
      "MCA",
      "Bachelor",
      "BCA"
    ],
    "projects": [
      "Betting Service Platform",
      "Split and Steal Game Platform"
    ],
    "atsScore": 100,
    "improvementSuggestions": [],
    "createdAt": "2026-02-11T13:09:39.812302"
  },
  "error": null
}
```

---

### 5. Get All Job Comparisons

**Request:**
```bash
GET http://localhost:8080/api/compare-job-description?page=0&size=5
```

**Response:**
```json
{
  "data": {
    "content": [
      {
        "jobDescriptionCompareId": 4,
        "matchedSkills": [
          "Java",
          "Spring Boot",
          "RESTful APIs",
          "Microservices architecture",
          "Hibernate",
          "JPA",
          "SQL",
          "CI/CD"
        ],
        "missingSkills": [
          "Database optimization",
          "Unit testing and integration testing",
          "Agile methodology"
        ],
        "matchPercentage": 27.03,
        "improvementSuggestions": [
          "Add these key missing skills: Database optimization, Unit testing and integration testing, CI and 2 more",
          "Your resume matches less than 50% of job requirements. Consider gaining experience in missing skills."
        ],
        "createdAt": "2026-02-11T12:50:37.169402"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true
  },
  "error": null
}
```

---

### 6. Get Job Match by ID

**Request:**
```bash
GET http://localhost:8080/api/job-matches/4
```

**Response:**
```json
{
  "data": {
    "jobDescriptionCompareId": 4,
    "matchedSkills": [
      "Java",
      "Spring Boot",
      "RESTful APIs",
      "Microservices architecture",
      "Hibernate",
      "JPA",
      "SQL",
      "Microservices",
      "Hibernate/JPA",
      "CI/CD"
    ],
    "missingSkills": [
      "Database optimization",
      "Unit testing and integration testing"
    ],
    "matchPercentage": 27.03,
    "improvementSuggestions": [
      "Add these key missing skills: Database optimization, Unit testing and integration testing",
      "Your resume matches less than 50% of job requirements. Consider gaining experience in missing skills."
    ],
    "createdAt": "2026-02-11T12:50:37.169402"
  },
  "error": null
}
```

---

### 7. Delete Resume Analysis

**Request:**
```bash
DELETE http://localhost:8080/api/analyze-resume/8
```

**Response:**
```json
{
  "data": "Resume deleted successfully",
  "error": null
}
```

---

### 8. Delete Job Match

**Request:**
```bash
DELETE http://localhost:8080/api/compare-job-description/4
```

**Response:**
```json
{
  "data": "Job match deleted successfully",
  "error": null
}
```

---

### 9. Health Check

**Request:**
```bash
GET http://localhost:8080/api/health
```

**Response:**
```json
{
  "data": "Resume Analyzer API is running!",
  "error": null
}
```

---

## Database Schema

### ResumeAnalysisEntity Table

```sql
CREATE TABLE resume_analysis (
    id BIGSERIAL PRIMARY KEY,
    resume_text TEXT NOT NULL,
    skills TEXT[], -- Array of skills
    experience VARCHAR(255),
    education TEXT[], -- Array of degrees
    projects TEXT[], -- Array of project names
    ats_score INTEGER,
    improvement_suggestions TEXT[], -- Array of suggestions
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Column Descriptions:**
- `id`: Auto-generated primary key
- `resume_text`: Full resume text content
- `skills`: Extracted technical skills (array)
- `experience`: Calculated total experience (e.g., "10 months")
- `education`: List of degrees (e.g., ["MCA", "BCA"])
- `projects`: List of project names
- `ats_score`: Calculated score (0-100)
- `improvement_suggestions`: List of improvement recommendations
- `created_at`: Timestamp of record creation

---

### JobMatchEntity Table

```sql
CREATE TABLE job_match (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    job_description TEXT NOT NULL,
    matched_skills TEXT[], -- Array of matched skills
    missing_skills TEXT[], -- Array of missing skills
    match_percentage DECIMAL(5,2),
    improvement_suggestions TEXT[], -- Array of suggestions
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resume_id) REFERENCES resume_analysis(id) ON DELETE CASCADE
);
```

**Column Descriptions:**
- `id`: Auto-generated primary key
- `resume_id`: Foreign key to resume_analysis table
- `job_description`: Full job description text
- `matched_skills`: Skills present in both resume and job description
- `missing_skills`: Skills in job description but not in resume
- `match_percentage`: Percentage match (0.00 - 100.00)
- `improvement_suggestions`: Recommendations for improvement
- `created_at`: Timestamp of record creation

**Relationships:**
- One-to-Many: One resume can have multiple job matches
- Cascade Delete: Deleting a resume deletes associated job matches

---

## Error Handling

### Exception Hierarchy

```
RuntimeException
├── IllegalArgumentException
│   └── Validation errors (400 Bad Request)
├── DataNotFoundException
│   └── Resource not found errors (404 Not Found)
├── ResumeAnalysisException
│   └── Resume processing errors (500 Internal Server Error)
└── JobMatchException
    └── Job comparison errors (500 Internal Server Error)
```

### HTTP Status Codes

| Status Code | Description | When Used |
|-------------|-------------|-----------|
| 200 OK | Success | GET, DELETE operations successful |
| 201 Created | Resource created | POST compare-job-description successful |
| 400 Bad Request | Invalid input | Validation failures |
| 404 Not Found | Resource not found | ID not found in database |
| 500 Internal Server Error | Server error | Unexpected errors, processing failures |

### Error Response Format

All error responses follow this structure:
```json
{
  "data": null,
  "error": {
    "message": "Detailed error description"
  }
}
```

### Common Error Scenarios

**1. Empty Resume Text**
```json
{
  "data": null,
  "error": {
    "message": "Resume text must not be empty"
  }
}
```

**2. Resume Text Too Short**
```json
{
  "data": null,
  "error": {
    "message": "Resume text is too short to analyze (minimum 50 characters)"
  }
}
```

**3. Resume Not Found**
```json
{
  "data": null,
  "error": {
    "message": "Resume not found with id: 8"
  }
}
```

**4. Job Match Not Found**
```json
{
  "data": null,
  "error": {
    "message": "Job match not found with id: 4"
  }
}
```

**5. Processing Failure**
```json
{
  "data": null,
  "error": {
    "message": "Failed to analyze resume: NullPointerException at line 245"
  }
}
```

---

## Contributing

We welcome contributions to improve the Resume Analyzer API!

### How to Contribute

1. **Fork the Repository**
   ```bash
   git clone https://github.com/yourusername/resume-analyzer-api.git
   cd resume-analyzer-api
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make Your Changes**
   - Follow Java coding conventions
   - Add unit tests for new functionality
   - Update documentation as needed

4. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "Add: Brief description of changes"
   ```

5. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Describe your changes clearly
   - Reference any related issues
   - Ensure all tests pass

### Code Style Guidelines

- Use meaningful variable and method names
- Follow Spring Boot best practices
- Add JavaDoc comments for public methods
- Keep methods focused and concise
- Handle exceptions appropriately

### Testing

- Write unit tests for service layer methods
- Test edge cases and error scenarios
- Ensure test coverage is maintained

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL community for robust database support
- All contributors who helped improve this project

---

**Last Updated:** February 11, 2026  
**Version:** 1.0.0  
**Author:** Dhananjay
