package playerregistration.testsupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import playerregistration.database.DatabaseConnectionManager;
import playerregistration.service.PlayerRegistrationService;

public final class TestDatabaseSupport {

    private static boolean configured;

    private TestDatabaseSupport() {
    }

    public static synchronized void configureTestDatabase() {
        if (configured) {
            return;
        }

        System.setProperty("db.config.path", "database/db.test.properties");
        DatabaseConnectionManager.initialize();
        configured = true;
    }

    public static void resetSchema() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS PlayerAndGame");
            stmt.execute("DROP TABLE IF EXISTS Game");
            stmt.execute("DROP TABLE IF EXISTS Player");
        }

        new PlayerRegistrationService().initializeDatabaseSchema();
    }

    public static int countRows(String tableName) throws SQLException {
        if (!"Player".equals(tableName) && !"Game".equals(tableName) && !"PlayerAndGame".equals(tableName)) {
            throw new IllegalArgumentException("Unsupported table name for test count: " + tableName);
        }

        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
