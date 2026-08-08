# Java JDBC Player Registration System

A desktop data-management application that demonstrates clean JavaFX UI workflows and JDBC integration with Oracle SQL.

The project is organized as a portfolio-ready codebase focused on maintainability, secure database configuration practices, and clear technical documentation.

## Project Overview

This application manages player and game records. It uses JavaFX for the user interface and JDBC prepared statements for database interactions.

Core use cases:

- Create player records
- Create game records
- Update player information
- Display player information
- Execute SQL queries through prepared statements
- Display query results in JavaFX tables

## Features

- JavaFX desktop interface for data entry and viewing
- JDBC-based Oracle SQL integration
- Prepared statements for safer query execution
- CRUD-style operations for core entities
- Structured project layout for portfolio presentation

## Technologies Used

- Java
- JavaFX
- JDBC
- Oracle SQL
- Git
- GitHub

## Repository Structure

```
src/
docs/
database/
screenshots/
README.md
.gitignore
LICENSE
```

## Installation Instructions

1. Clone the repository.
2. Ensure Java (JDK 17+ recommended) and JavaFX are installed.
3. Set up Oracle SQL and create the required schema objects.
4. Copy `database/db.properties.example` to `database/db.properties`.
5. Update `database/db.properties` with your local database connection values.
6. Build and run the project from your preferred Java IDE.

## Database Schema

Schema reference files are provided in `docs/`:

- `docs/database-schema.md`
- `docs/schema.sql`

## Screenshots

Add screenshots to `screenshots/` and reference them below:

- `screenshots/player-form.png`
- `screenshots/game-form.png`
- `screenshots/player-table.png`

## Security Notes

- Do not commit real database credentials.
- Keep `database/db.properties` local only.
- Use the committed example file as a template.

## Future Improvements

- Add unit tests for service and validation layers
- Add integration tests with test database profiles
- Add input validation error summaries in the UI
- Add CI workflow for build and static checks

## Author

Jaturaput Jongsubcharoen