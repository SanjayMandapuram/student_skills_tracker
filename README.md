# Student Skill & Certification Tracker

Student portfolio and certification verification app. Students manage skills, courses and certificates; faculty and administrators review and verify submissions.

## Stack

Spring Boot 3.3, Java 17, Maven, Spring Security JWT, BCrypt, JPA/Hibernate, MySQL (production), H2 (local development), and a responsive HTML/CSS/JavaScript frontend.

## Local run

The backend uses a file-based H2 database by default, so MySQL is not required for the first run.

```powershell
cd backend
mvn spring-boot:run
# in another terminal from the repository root
node frontend/server.js
```

Open `http://localhost:5500`. The frontend server proxies `/api/*` to `http://127.0.0.1:8080` by default. Set `BACKEND_URL` if the backend uses another local URL.

To create optional development accounts, set `SEED_ADMIN_EMAIL`, `SEED_ADMIN_PASSWORD`, `SEED_STUDENT_EMAIL`, and `SEED_STUDENT_PASSWORD` before starting the backend. Passwords are never stored in source code; they are BCrypt-hashed at startup. Registration supports Student and Faculty accounts. Faculty registration and the faculty verification step require the private `FACULTY_ACCESS_CODE` value.

The application includes authenticated resume and achievement-report PDFs, private certificate evidence upload/download, faculty CSV certificate exports, pageable resource APIs, persisted achievements, scheduled in-app notifications, duplicate-certificate inspection, and a workshops page. Development evidence storage uses `STORAGE_LOCAL_DIRECTORY`; ephemeral production hosts require persistent/object storage before uploaded files should be considered durable.

## Configuration

Copy `.env.example` into your secret manager or local environment. The important variables are `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`, `DB_DIALECT`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `FACULTY_ACCESS_CODE`, `FRONTEND_ORIGIN`, `STORAGE_LOCAL_DIRECTORY`, `NOTIFICATIONS_CRON`, and `NOTIFICATIONS_TIMEZONE`.

For production, start with the `prod` profile:

```text
SPRING_PROFILES_ACTIVE=prod
```

The production profile requires the database and authentication secrets and uses `ddl-auto=validate`; apply the SQL in `database/` or an equivalent migration before deployment. Do not use H2 or placeholder secrets in production.

## Deployment

Deploy `frontend/` as a static site on Vercel with no build command and output directory `.`. Because the frontend is intentionally framework-free, configure `window.TRACKER_API` before `js/api.js` in the deployed HTML pages to the Render API base, for example `https://your-render-service.onrender.com/api`, or use a Vercel rewrite to proxy `/api` to Render. Local development uses the built-in proxy described above.

Deploy `backend/` on Render with Java 17, build command `mvn clean package`, start command `java -jar target/student-tracker-1.0.0.jar`, and the `prod` profile. Set all variables from `.env.example`; set `FRONTEND_ORIGIN` to the exact Vercel origin. Never commit `.env`, database passwords, JWT secrets, faculty codes, or seed passwords.

## Verification

Run from `backend/`:

```powershell
mvn test
mvn package
```

The frontend JavaScript files can be syntax-checked with `node --check frontend/js/api.js`, `node --check frontend/js/auth.js`, and the other files in `frontend/js/`. Main API routes are documented in `docs/`.

Java 17 is required. If Maven is not installed globally, use Apache Maven 3.9.11 or newer from a local tools directory, such as `.tools/apache-maven-3.9.11/bin/mvn.cmd test` on Windows. `.tools/` and build output are ignored by Git.
