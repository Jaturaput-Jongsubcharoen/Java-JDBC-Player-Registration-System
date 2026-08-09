package playerregistration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import playerregistration.PlayerGameInfo;
import playerregistration.testsupport.TestDatabaseSupport;

class PlayerRegistrationServiceTest {

    private final PlayerRegistrationService service = new PlayerRegistrationService();

    @BeforeAll
    static void beforeAll() {
        TestDatabaseSupport.configureTestDatabase();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        TestDatabaseSupport.resetSchema();
    }

    @Test
    void createWorkflow_success_persistsRelatedRows() throws SQLException {
        service.createPlayerGameWorkflow(
                "Alex", "Morgan", "100 Main St", "M1A1A1", "ON", "4165550101",
                "FIFA 24", Date.valueOf("2026-08-01"), 95
        );

        assertEquals(1, TestDatabaseSupport.countRows("Player"));
        assertEquals(1, TestDatabaseSupport.countRows("Game"));
        assertEquals(1, TestDatabaseSupport.countRows("PlayerAndGame"));

        List<PlayerGameInfo> report = service.fetchLatestPlayerGameInfo();
        assertEquals(1, report.size());
        assertEquals("FIFA 24", report.get(0).getGameTitle());
    }

    @Test
    void createWorkflow_failure_rollsBackAllRows() throws SQLException {
        assertThrows(SQLException.class, () -> service.createPlayerGameWorkflow(
                "Alex", "Morgan", "100 Main St", "M1A1A1", "ON", "4165550101",
                "GAME_TITLE_IS_WAY_TOO_LONG", Date.valueOf("2026-08-01"), 95
        ));

        assertEquals(0, TestDatabaseSupport.countRows("Player"));
        assertEquals(0, TestDatabaseSupport.countRows("Game"));
        assertEquals(0, TestDatabaseSupport.countRows("PlayerAndGame"));
    }

    @Test
    void updateWorkflow_nonExistingPlayer_returnsFalseWithoutChanges() throws SQLException {
        service.createPlayerGameWorkflow(
                "Alex", "Morgan", "100 Main St", "M1A1A1", "ON", "4165550101",
                "FIFA 24", Date.valueOf("2026-08-01"), 95
        );

        boolean updated = service.updatePlayerGameWorkflow(
                99999,
                "Taylor", "Stone", "200 King St", "ON", "M5V2T6", "6475550102",
                "NBA 2K24", Date.valueOf("2026-08-05"), 102
        );

        assertFalse(updated);
        assertEquals(1, TestDatabaseSupport.countRows("Player"));
        assertEquals(1, TestDatabaseSupport.countRows("Game"));
        assertEquals(1, TestDatabaseSupport.countRows("PlayerAndGame"));
    }

    @Test
    void updateWorkflow_success_updatesRelatedRows() throws SQLException {
        service.createPlayerGameWorkflow(
                "Alex", "Morgan", "100 Main St", "M1A1A1", "ON", "4165550101",
                "FIFA 24", Date.valueOf("2026-08-01"), 95
        );

        int playerId = service.fetchLatestPlayerGameInfo().get(0).getId();
        boolean updated = service.updatePlayerGameWorkflow(
                playerId,
                "Taylor", "Stone", "200 King St", "ON", "M5V2T6", "6475550102",
                "NBA 2K24", Date.valueOf("2026-08-05"), 102
        );

        assertTrue(updated);

        PlayerGameInfo updatedRow = service.fetchLatestPlayerGameInfo().get(0);
        assertEquals("Taylor Stone", updatedRow.getName());
        assertEquals("NBA 2K24", updatedRow.getGameTitle());
        assertEquals(102, updatedRow.getScore());
    }

    @Test
    void deleteWorkflow_success_removesPlayerAndOrphans() throws SQLException {
        service.createPlayerGameWorkflow(
                "Alex", "Morgan", "100 Main St", "M1A1A1", "ON", "4165550101",
                "FIFA 24", Date.valueOf("2026-08-01"), 95
        );

        int playerId = service.fetchLatestPlayerGameInfo().get(0).getId();
        int deletedGames = service.deletePlayerWorkflow(playerId);

        assertTrue(deletedGames >= 0);
        assertEquals(0, TestDatabaseSupport.countRows("Player"));
        assertEquals(0, TestDatabaseSupport.countRows("PlayerAndGame"));
        assertEquals(0, service.fetchLatestPlayerGameInfo().size());
    }
}
