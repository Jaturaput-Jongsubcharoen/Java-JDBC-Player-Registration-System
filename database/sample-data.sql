-- Safe sample data for local testing.
-- Uses MERGE statements so the script can be re-run without duplicate inserts.

MERGE INTO players p
USING (
    SELECT 1001 AS player_id, 'Alex' AS first_name, 'Morgan' AS last_name,
           '100 Main St' AS address, 'M1A1A1' AS postal_code,
           'ON' AS province, '4165550101' AS phone_number
    FROM dual
    UNION ALL
    SELECT 1002, 'Jordan', 'Lee',
           '25 King Ave', 'M5V2T6',
           'ON', '6475550134'
    FROM dual
    UNION ALL
    SELECT 1003, 'Priya', 'Patel',
           '78 Elm Rd', 'L4W5N6',
           'ON', '9055550188'
    FROM dual
) src
ON (p.player_id = src.player_id)
WHEN NOT MATCHED THEN
    INSERT (
        player_id, first_name, last_name, address,
        postal_code, province, phone_number
    )
    VALUES (
        src.player_id, src.first_name, src.last_name, src.address,
        src.postal_code, src.province, src.phone_number
    );

MERGE INTO games g
USING (
    SELECT 2001 AS game_id, 'FIFA 24' AS game_title FROM dual
    UNION ALL
    SELECT 2002, 'NBA 2K24' FROM dual
    UNION ALL
    SELECT 2003, 'Mario Kart 8 Deluxe' FROM dual
) src
ON (g.game_id = src.game_id)
WHEN NOT MATCHED THEN
    INSERT (game_id, game_title)
    VALUES (src.game_id, src.game_title);

MERGE INTO player_and_game pag
USING (
    SELECT 3001 AS player_game_id, 1001 AS player_id, 2001 AS game_id,
           DATE '2026-08-01' AS playing_date, 95 AS score
    FROM dual
    UNION ALL
    SELECT 3002, 1001, 2003, DATE '2026-08-03', 88 FROM dual
    UNION ALL
    SELECT 3003, 1002, 2002, DATE '2026-08-04', 102 FROM dual
    UNION ALL
    SELECT 3004, 1003, 2001, DATE '2026-08-05', 76 FROM dual
) src
ON (pag.player_game_id = src.player_game_id)
WHEN NOT MATCHED THEN
    INSERT (player_game_id, player_id, game_id, playing_date, score)
    VALUES (
        src.player_game_id,
        src.player_id,
        src.game_id,
        src.playing_date,
        src.score
    );

COMMIT;