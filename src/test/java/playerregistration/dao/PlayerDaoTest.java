package playerregistration.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import playerregistration.database.DatabaseConnectionManager;
import playerregistration.testsupport.TestDatabaseSupport;

class PlayerDaoTest {

    private final PlayerDao playerDao = new PlayerDao();

    @BeforeAll
    static void beforeAll() {
        TestDatabaseSupport.configureTestDatabase();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        TestDatabaseSupport.resetSchema();
    }

    @Test
    void insertAndExistsById_success() throws SQLException {
        playerDao.insertPlayer("Alex", "Morgan", "100 Main St", "M1A1A1", "ON", "4165550101");
        int playerId = playerDao.fetchLastInsertedPlayerId();

        assertTrue(playerId > 0);
        assertTrue(playerDao.existsById(playerId));
    }

    @Test
    void sharedConnectionRollback_discardsUncommittedInsert() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                playerDao.insertPlayer(conn, "Alex", "Morgan", "100 Main St", "M1A1A1", "ON", "4165550101");
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        }

        assertEquals(0, TestDatabaseSupport.countRows("Player"));
    }
}
