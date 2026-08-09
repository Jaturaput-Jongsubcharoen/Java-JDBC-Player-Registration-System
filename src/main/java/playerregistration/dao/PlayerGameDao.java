package playerregistration.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import playerregistration.PlayerGameInfo;
import playerregistration.database.DatabaseConnectionManager;

public class PlayerGameDao {

    public void createTableIfNotExists() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            String createTableSQL = """
                    CREATE TABLE PlayerAndGame (
                        id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        player_id NUMBER NOT NULL,
                        game_id NUMBER NOT NULL,
                        player_date DATE NOT NULL,
                        score NUMBER NOT NULL,
                        CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES Player(player_id) ON DELETE CASCADE,
                        CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES Game(game_id) ON DELETE CASCADE
                    )
                    """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
        }
    }

    public void insertPlayerGame(int playerId, int gameId, Date playerDate, int score) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            insertPlayerGame(conn, playerId, gameId, playerDate, score);
        }
    }

    public void insertPlayerGame(Connection conn, int playerId, int gameId, Date playerDate, int score) throws SQLException {
        String insertSQL = "INSERT INTO PlayerAndGame (player_id, game_id, player_date, score) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, gameId);
            pstmt.setDate(3, playerDate);
            pstmt.setInt(4, score);
            pstmt.executeUpdate();
        }
    }

    public void updatePlayerGameByPlayerId(int playerId, Date playerDate, int score) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            updatePlayerGameByPlayerId(conn, playerId, playerDate, score);
        }
    }

    public void updatePlayerGameByPlayerId(Connection conn, int playerId, Date playerDate, int score) throws SQLException {
        String updatePlayerGameSQL = """
                UPDATE PlayerAndGame
                SET player_date = ?, score = ?
                WHERE player_id = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(updatePlayerGameSQL)) {
            pstmt.setDate(1, playerDate);
            pstmt.setInt(2, score);
            pstmt.setInt(3, playerId);
            pstmt.executeUpdate();
        }
    }

    public List<PlayerGameInfo> fetchLatestPlayerGameInfo() throws SQLException {
        String fetchSQL = """
                SELECT p.player_id AS ID,
                       p.first_name || ' ' || p.last_name AS NAME,
                       p.address AS ADDRESS,
                       p.postal_code AS POSTAL_CODE,
                       p.province AS PROVINCE,
                       p.phone_number AS PHONE_NUMBER,
                       g.game_title AS GAME_TITLE,
                       pg.score AS SCORE,
                       pg.player_date AS DATE_PLAYED
                FROM Player p
                JOIN PlayerAndGame pg ON p.player_id = pg.player_id
                JOIN Game g ON pg.game_id = g.game_id
                WHERE pg.player_date = (
                    SELECT MAX(player_date)
                    FROM PlayerAndGame
                    WHERE player_id = p.player_id
                )
                """;

        List<PlayerGameInfo> results = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(fetchSQL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(new PlayerGameInfo(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getString("ADDRESS"),
                        rs.getString("POSTAL_CODE"),
                        rs.getString("PROVINCE"),
                        rs.getString("PHONE_NUMBER"),
                        rs.getString("GAME_TITLE"),
                        rs.getInt("SCORE"),
                        rs.getDate("DATE_PLAYED").toString()
                ));
            }
        }

        return results;
    }
}
