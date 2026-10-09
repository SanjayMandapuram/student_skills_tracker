# Setup guide

1. Install JDK 17+, Maven 3.9+, and MySQL 8+.
2. The default development setup uses a local H2 file database and requires no MySQL password. To use MySQL instead, set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER`, and `DB_DIALECT`.
4. Start the backend from `backend` with `mvn spring-boot:run`.
5. Serve `frontend` on port 5500: `node frontend/server.js`.
6. Open http://localhost:5500. If you want local demo accounts, configure the optional `SEED_*` variables described in the root README before starting the backend.

The backend creates the schema and seed users on first startup. The frontend uses real REST calls; it does not contain fake records.
