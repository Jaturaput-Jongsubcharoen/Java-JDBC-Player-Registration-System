-- Canonical schema aligned with existing JavaFX/JDBC DAO and repository SQL.

CREATE TABLE Player (
    player_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR2(255) NOT NULL,
    last_name VARCHAR2(255) NOT NULL,
    address VARCHAR2(255) NOT NULL,
    province VARCHAR2(2) NOT NULL,
    postal_code VARCHAR2(6) NOT NULL,
    phone_number NUMBER(10) NOT NULL
);

CREATE TABLE Game (
    game_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    game_title VARCHAR2(20) NOT NULL
);

CREATE TABLE PlayerAndGame (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    player_id NUMBER NOT NULL,
    game_id NUMBER NOT NULL,
    player_date DATE NOT NULL,
    score NUMBER NOT NULL,
    CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES Player(player_id) ON DELETE CASCADE,
    CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES Game(game_id) ON DELETE CASCADE
);