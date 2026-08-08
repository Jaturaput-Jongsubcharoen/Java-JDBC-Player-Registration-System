# Database Schema

This project uses three core entities to support player registration workflows.

## Tables

### `players`

- `player_id` (PK)
- `first_name`
- `last_name`
- `address`
- `postal_code`
- `province`
- `phone_number`

### `games`

- `game_id` (PK)
- `game_title`

### `player_and_game`

- `player_game_id` (PK)
- `player_id` (FK -> players.player_id)
- `game_id` (FK -> games.game_id)
- `playing_date`
- `score`

## Relationship Summary

- One player can be associated with many game records.
- One game can be associated with many player records.
- The junction table `player_and_game` stores the relationship and result data.

See `docs/schema.sql` for a runnable SQL version.