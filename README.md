# Java JDBC Player Registration System

[![CI](https://github.com/Jaturaput-Jongsubcharoen/Java-JDBC-Player-Registration-System/actions/workflows/ci.yml/badge.svg)](https://github.com/Jaturaput-Jongsubcharoen/Java-JDBC-Player-Registration-System/actions/workflows/ci.yml)

# Project overview

A JavaFX desktop application for managing players, games, and player-game results with a layered JDBC architecture. The codebase is organized for maintainability and portfolio-grade engineering practices, including automated tests and GitHub Actions CI.

## Screenshots

### Main Application Window
<img width="1917" height="1030" alt="JavaFX_Database_Example_1-main-window" src="https://github.com/user-attachments/assets/8b4db47c-2950-454f-a6ab-5e92b6051bf8" />
Main JavaFX application window showing the player information form, game information form, and primary action buttons before any workflow is executed.

### Creating a Player
<img width="1918" height="1032" alt="JavaFX_Database_Example_2-create-player" src="https://github.com/user-attachments/assets/a3623031-e120-4ff4-8209-a9eda5ca6fd5" />
Successful create workflow after entering player and game data, demonstrating record creation and the create status message.

### Displaying Players and Games
<img width="1920" height="1032" alt="JavaFX_Database_Example_3-display-all-players" src="https://github.com/user-attachments/assets/e95510e3-09ac-4b91-88c3-99caf1461576" />
Reporting view opened from `Display All Players`, showing the joined player/game results loaded from the database in a table.

### Updating an Existing Player
<img width="1918" height="1033" alt="JavaFX_Database_Example_4-update-player" src="https://github.com/user-attachments/assets/0d5a3562-3cb0-4fe1-934d-5450baa03af8" />
Update workflow using an existing player ID to modify the current player row and its related game and score data without creating a new player ID.

### Validation and Error Handling
<img width="1920" height="1035" alt="JavaFX_Database_Example_5-validation-errors" src="https://github.com/user-attachments/assets/9b1ea866-7269-4e2a-a1f7-26fc5b1368a8" />
Validation example highlighting incorrect or incomplete user input and the inline field-level error messages shown in the UI.

## Spring Boot REST API

### Root Health Endpoint

<img src="screenshots/Spring_boot_api-root-endpoint.png" />

Spring Boot health endpoint available at:

`GET /`

Example response:

```json
{
  "status": "running",
  "application": "Player Registration API"
}
```

### Player API Response

<img src="screenshots/Spring_boot_api-players-response.png" />

Player information returned from:

`GET /api/players`

This endpoint demonstrates successful integration between Spring Boot, JPA repositories, and the underlying database.

---

## REST API Endpoints

The project also provides a Spring Boot REST API for integration testing and future frontend applications.

### Base URL

```text
http://localhost:8081
```

### Available endpoints

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/` | Application health check |
| GET | `/api/players` | Retrieve player information |
| POST | `/api/players` | Create a player |
| PUT | `/api/players/{playerId}` | Update a player |

### Example response

```json
{
  "status": "running",
  "application": "Player Registration API"
}
```

## Key Features

- JavaFX desktop UI for create, update, delete, and reporting workflows
- Layered architecture: UI -> service -> DAO -> connection manager -> database
- JDBC `PreparedStatement` usage for parameterized SQL operations
- Transactional service workflows for multi-table consistency
- Config-driven database connectivity via `database/db.properties`
- Automated tests with JUnit 5 and H2 in Oracle compatibility mode
- CI workflow that runs Maven tests on pull requests and pushes to `main`

## Technology Stack

- Java 17
- JavaFX 21
- Spring Boot 3
- Spring Data JPA
- Maven
- JDBC
- Oracle SQL (production/local runtime target)
- H2 (test database in Oracle compatibility mode)
- JUnit 5
- Docker
- GitHub Actions

## Architecture

High-level flow:

- JavaFX UI (`Main`) orchestrates user interactions
- Service layer (`PlayerRegistrationService`) manages business workflows and transactions
- DAO layer (`PlayerDao`, `GameDao`, `PlayerGameDao`) executes SQL
- `DatabaseConnectionManager` handles configuration loading and JDBC connections
- Oracle database stores production data.
- H2 in Oracle compatibility mode is available for local demonstrations and automated tests.

```mermaid
flowchart LR
		UI[JavaFX UI\nMain] --> SERVICE[Service Layer\nPlayerRegistrationService]
		SERVICE --> DAO[DAO Layer\nPlayerDao / GameDao / PlayerGameDao]
		DAO --> DCM[DatabaseConnectionManager]
		DCM --> DB[(Oracle Database)]

		TESTS[JUnit 5 Tests] --> H2[H2 in Oracle Mode]
		TESTS --> SERVICE
		TESTS --> DAO
```

## Project Structure

```text
.github/workflows/ci.yml
database/
  db.properties.example
  db.test.properties
  schema.sql
  sample-data.sql
docs/
  database-schema.md
src/
  main/
    java/playerregistration/
      Main.java
      service/
      dao/
      database/
  test/
    java/playerregistration/
      service/
      dao/
      testsupport/
```

Important components:

- `src/main/java/playerregistration/Main.java`: JavaFX UI and input handling.
- `src/main/java/playerregistration/service/`: Transactional application workflows.
- `src/main/java/playerregistration/dao/`: SQL operations for `Player`, `Game`, and `PlayerAndGame`.
- `src/main/java/playerregistration/database/`: `DatabaseConnectionManager` for config loading, JDBC URL-based driver initialization, and connection creation.
- `src/test/`: JUnit test suite (service and DAO behavior) plus test DB support utilities.
- `database/schema.sql` and `docs/database-schema.md`: schema reference artifacts.
- `.github/workflows/ci.yml`: CI pipeline running Maven tests.

## Database Design

The runtime application model uses three core tables:

- `Player`: player profile records
- `Game`: game catalog records
- `PlayerAndGame`: linking table with gameplay attributes (`player_date`, `score`)

Relationship model:

- `PlayerAndGame.player_id` references `Player.player_id`
- `PlayerAndGame.game_id` references `Game.game_id`
- Supports many-to-many association between players and games through the linking table

Reference SQL scripts and documentation are available in:

- `database/schema.sql`
- `database/sample-data.sql`
- `docs/database-schema.md`

## Transaction Management

Related create/update/delete operations in the service layer are handled in a single JDBC transaction:

- Acquire one `Connection`
- Call `setAutoCommit(false)`
- Execute related DAO operations
- `commit()` on success
- `rollback()` on failure

This pattern is implemented in `PlayerRegistrationService` for consistency across multi-table writes.

## Security and Configuration

- `database/db.properties.example` is the committed template.
- Create a local `database/db.properties` with your own Oracle connection values.
- Real credentials are not committed.
- Tests use `database/db.test.properties` with test-only H2 credentials.

## Testing

The automated test suite uses:

- JUnit 5
- H2 in Oracle compatibility mode (`MODE=Oracle`)
- Service and DAO tests under `src/test/java/playerregistration/`

Current automated test scope includes transactional workflows and DAO behavior, including rollback scenarios.

Run tests locally with:

```bash
mvn clean test
```

## CI/CD

GitHub Actions automatically runs Maven tests using JDK 17 on:

- Pull requests
- Pushes to `main`

Workflow file:

- `.github/workflows/ci.yml`

## Local Setup

1. Install JDK 17.
2. Install Maven 3.9+.
3. Copy `database/db.properties.example` to `database/db.properties`.
4. Configure Oracle JDBC URL, username, and password in `database/db.properties`.
5. Run the application:

```bash
mvn clean javafx:run
```

6. Run the automated tests:

```bash
mvn clean test
```

## Future Improvements

- Expand DAO test coverage for edge cases and SQL constraints
- Add JavaFX UI automation tests
- Improve concurrency-safe generated ID handling strategy
- Add structured logging for operational diagnostics
