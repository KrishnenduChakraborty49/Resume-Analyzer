# AI Resume Analyzer & Builder

A professional-grade, local-first AI application for analyzing resumes against ATS (Applicant Tracking System) standards and generating premium, industry-ready resumes. Powered by **Spring Boot**, **React**, and **Ollama (qwen2.5:7b)**.

## 🚀 Features

- **Deep ATS Analysis**: AI-powered scoring, technical proficiency breakdown, and skills gap analysis.
- **Multi-Format Support**: Parse PDF, DOCX, DOC, ODF, RTF, TXT, and HTML documents with ease.
- **AI Resume Builder**: 6-step guided form with AI-powered professional summary generation.
- **5 Premium Templates**:
    - **Classic Professional**: Two-column, navy & white (Corporate/Traditional).
    - **Modern Minimal**: Clean, editorial style with lots of whitespace.
    - **Creative Bold**: Vibrant teal-themed design for standout profiles.
    - **Executive Elite**: Gold-accented, authoritative design for senior roles.
    - **Tech Focused**: Terminal/Code-themed design for developers and hackers.
- **Privacy First**: All AI processing happens locally on your machine via Ollama.
- **History Tracking**: Keep track of all your analyses and saved resumes.

## 🛠️ Tech Stack

- **Backend**: Java 25, Spring Boot 4.0.3, Spring Data JPA, MySQL 8.x
- **AI**: Ollama (Running locally at `localhost:11434`), Model: `qwen2.5:7b`
- **Frontend**: React 18, Vite, Tailwind CSS, Framer Motion, Axios
- **Parsing**: Apache Tika, Apache PDFBox, Apache POI

## 📋 Prerequisites

1.  **Java 25** and **Maven** installed.
2.  **Node.js 18+** and **npm**.
3.  **MySQL 8.x** running locally.
4.  **Ollama** installed and running (`ollama serve`).
5.  Pull the required model:
    ```bash
    ollama pull qwen2.5:7b
    ```

## ⚙️ Setup Instructions

### 1. Database Setup
Create a database named `resume_db` in MySQL:
```sql
CREATE DATABASE IF NOT EXISTS resume_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Backend Configuration
Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.username=root
spring.datasource.password=chakraborty@123
```

### 3. Run Backend
```bash
mvn spring-boot:run
```
The backend will start on `http://localhost:8080`.

### 4. Run Frontend
```bash
cd frontend
npm install
npm run dev
```
The frontend will start on `http://localhost:5173`.

## 📂 Project Structure

- `src/main/java`: Spring Boot source code (Controllers, Services, Entities).
- `src/main/resources`: Configuration and AI prompts.
- `frontend/src`: React application components and pages.
- `frontend/src/components/templates`: 5 Premium Resume Template definitions.

## ⚠️ Troubleshooting

- **Ollama Offline**: Ensure `ollama serve` is running. A red dot in the navbar indicates connection failure.
- **Analysis Timeout**: Analyzing large resumes with a local 7B model can take 15-40 seconds. We've set the timeout to 2 minutes.
- **MySQL Connection**: Ensure the `resume_db` exists and credentials are correct.
- **Formatting Issues**: Use the "Download PDF" button for pixel-perfect A4 renders.

---
Built with ❤️ by Antigravity AI
