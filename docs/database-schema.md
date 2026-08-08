# Database Schema

This project uses three core tables that map to the application entities Player, Game, and PlayerAndGame.

## Tables and Purpose

### players (Player)

Stores the player profile information used by the registration form and update workflows.

Columns:

- player_id (PK)
- first_name (NOT NULL)
- last_name (NOT NULL)
- address
- postal_code
- province
- phone_number

### games (Game)

Stores game definitions that players can be associated with.

Columns:

- game_id (PK)
- game_title (NOT NULL, UNIQUE)

### player_and_game (PlayerAndGame)

Stores relationship rows between one player and one game, plus gameplay data.

Columns:

- player_game_id (PK)
- player_id (FK -> players.player_id, NOT NULL)
- game_id (FK -> games.game_id, NOT NULL)
- playing_date (NOT NULL)
- score (NOT NULL, default 0, CHECK score >= 0)

## Keys and Relationships

- Primary key on each table ensures row uniqueness.
- Foreign key `player_and_game.player_id` references `players.player_id`.
- Foreign key `player_and_game.game_id` references `games.game_id`.
- Relationship type is many-to-many between players and games, implemented through `player_and_game`.

## Why player_and_game Is the Linking Table

The application allows a player to play multiple games and each game to be played by multiple players.
This cannot be represented directly in two tables without data duplication.

The `player_and_game` table resolves this by:

- connecting one player to one game per row
- storing relationship-specific attributes (`playing_date`, `score`)
- keeping Player and Game data normalized

See `database/schema.sql` for the executable Oracle SQL schema.