package playerregistration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import playerregistration.database.DatabaseConnectionManager;

public class GameDao {

    public void createTableIfNotExists() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            String createTableSQL = """
                    CREATE TABLE Game (
                        game_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        game_title VARCHAR2(20) NOT NULL
                    )
                    """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
        }
    }

    public void insertGame(String gameTitle) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            insertGame(conn, gameTitle);
        }
    }

    public void insertGame(Connection conn, String gameTitle) throws SQLException {
        String insertSQL = "INSERT INTO Game (game_title) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, gameTitle);
            pstmt.executeUpdate();
        }
    }

    public int fetchLastInsertedGameId() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            return fetchLastInsertedGameId(conn);
        }
    }

    public int fetchLastInsertedGameId(Connection conn) throws SQLException {
        String query = "SELECT MAX(game_id) AS last_id FROM Game";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        }
        return -1;
    }

    public void updateGameTitleByPlayerId(int playerId, String gameTitle) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            updateGameTitleByPlayerId(conn, playerId, gameTitle);
        }
    }

    public void updateGameTitleByPlayerId(Connection conn, int playerId, String gameTitle) throws SQLException {
        String updateGameSQL = """
                UPDATE Game
                SET game_title = ?
                WHERE game_id = (
                    SELECT game_id FROM PlayerAndGame WHERE player_id = ?
                )
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(updateGameSQL)) {
            pstmt.setString(1, gameTitle);
            pstmt.setInt(2, playerId);
            pstmt.executeUpdate();
        }
    }

    public int deleteUnusedGames() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            return deleteUnusedGames(conn);
        }
    }

    public int deleteUnusedGames(Connection conn) throws SQLException {
        String deleteUnusedGamesSQL = """
                DELETE FROM Game
                WHERE game_id NOT IN (SELECT game_id FROM PlayerAndGame)
                """;
        try (PreparedStatement deleteGamesStmt = conn.prepareStatement(deleteUnusedGamesSQL)) {
            return deleteGamesStmt.executeUpdate();
        }
    }
}
