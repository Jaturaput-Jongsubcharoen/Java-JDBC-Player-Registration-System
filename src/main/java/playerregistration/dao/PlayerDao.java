package playerregistration.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import playerregistration.database.DatabaseConnectionManager;

public class PlayerDao {

    public void createTableIfNotExists() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            String createTableSQL = """
                    CREATE TABLE Player (
                        player_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        first_name VARCHAR2(255) NOT NULL,
                        last_name VARCHAR2(255) NOT NULL,
                        address VARCHAR2(255) NOT NULL,
                        province VARCHAR2(2) NOT NULL,
                        postal_code VARCHAR2(6) NOT NULL,
                        phone_number NUMBER(10) NOT NULL
                    )
                    """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
        }
    }

    public void insertPlayer(String firstName, String lastName, String address, String postalCode, String province, String phoneNumber) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            insertPlayer(conn, firstName, lastName, address, postalCode, province, phoneNumber);
        }
    }

    public void insertPlayer(Connection conn, String firstName, String lastName, String address, String postalCode, String province, String phoneNumber) throws SQLException {
        String insertSQL = "INSERT INTO Player (first_name, last_name, address, province, postal_code, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, address);
            pstmt.setString(4, province);
            pstmt.setString(5, postalCode);
            pstmt.setString(6, phoneNumber);
            pstmt.executeUpdate();
        }
    }

    public boolean existsById(int playerId) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            return existsById(conn, playerId);
        }
    }

    public boolean existsById(Connection conn, int playerId) throws SQLException {
        String checkPlayerSQL = "SELECT COUNT(*) AS count FROM Player WHERE player_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkPlayerSQL)) {
            checkStmt.setInt(1, playerId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                return rs.next() && rs.getInt("count") > 0;
            }
        }
    }

    public void updatePlayer(int playerId, String firstName, String lastName, String address, String province, String postalCode, String phoneNumber) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            updatePlayer(conn, playerId, firstName, lastName, address, province, postalCode, phoneNumber);
        }
    }

    public void updatePlayer(Connection conn, int playerId, String firstName, String lastName, String address, String province, String postalCode, String phoneNumber) throws SQLException {
        String updatePlayerSQL = """
                UPDATE Player
                SET first_name = ?, last_name = ?, address = ?, province = ?, postal_code = ?, phone_number = ?
                WHERE player_id = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(updatePlayerSQL)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, address);
            pstmt.setString(4, province);
            pstmt.setString(5, postalCode);
            pstmt.setString(6, phoneNumber);
            pstmt.setInt(7, playerId);
            pstmt.executeUpdate();
        }
    }

    public int fetchLastInsertedPlayerId() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            return fetchLastInsertedPlayerId(conn);
        }
    }

    public int fetchLastInsertedPlayerId(Connection conn) throws SQLException {
        String query = "SELECT MAX(player_id) AS last_id FROM Player";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        }
        return -1;
    }

    public void deletePlayerById(int playerId) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             ) {
            deletePlayerById(conn, playerId);
        }
    }

    public void deletePlayerById(Connection conn, int playerId) throws SQLException {
        String deletePlayerSQL = "DELETE FROM Player WHERE player_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deletePlayerSQL)) {
            deleteStmt.setInt(1, playerId);
            deleteStmt.executeUpdate();
        }
    }
}
