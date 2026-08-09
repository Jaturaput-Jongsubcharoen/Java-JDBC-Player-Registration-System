package playerregistration.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import playerregistration.PlayerGameInfo;
import playerregistration.dao.GameDao;
import playerregistration.dao.PlayerDao;
import playerregistration.dao.PlayerGameDao;
import playerregistration.database.DatabaseConnectionManager;

public class PlayerRegistrationService {

    private final PlayerDao playerDao = new PlayerDao();
    private final GameDao gameDao = new GameDao();
    private final PlayerGameDao playerGameDao = new PlayerGameDao();

    public void initializeDatabaseSchema() throws SQLException {
        playerDao.createTableIfNotExists();
        gameDao.createTableIfNotExists();
        playerGameDao.createTableIfNotExists();
    }

    public void createPlayerGameWorkflow(String firstName, String lastName, String address, String postalCode,
                                         String province, String phoneNumber, String gameTitle, Date playerDate,
                                         int score) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                playerDao.insertPlayer(conn, firstName, lastName, address, postalCode, province, phoneNumber);
                gameDao.insertGame(conn, gameTitle);

                int playerId = playerDao.fetchLastInsertedPlayerId(conn);
                int gameId = gameDao.fetchLastInsertedGameId(conn);
                playerGameDao.insertPlayerGame(conn, playerId, gameId, playerDate, score);

                conn.commit();
            } catch (SQLException e) {
                rollbackQuietly(conn);
                throw e;
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public boolean updatePlayerGameWorkflow(int playerId, String firstName, String lastName, String address,
                                            String province, String postalCode, String phoneNumber, String gameTitle,
                                            Date playerDate, int score) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                if (!playerDao.existsById(conn, playerId)) {
                    conn.rollback();
                    return false;
                }

                playerDao.updatePlayer(conn, playerId, firstName, lastName, address, province, postalCode, phoneNumber);
                gameDao.updateGameTitleByPlayerId(conn, playerId, gameTitle);
                playerGameDao.updatePlayerGameByPlayerId(conn, playerId, playerDate, score);

                conn.commit();
                return true;
            } catch (SQLException e) {
                rollbackQuietly(conn);
                throw e;
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public int deletePlayerWorkflow(int playerId) throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            boolean previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                playerDao.deletePlayerById(conn, playerId);
                int rowsAffected = gameDao.deleteUnusedGames(conn);
                conn.commit();
                return rowsAffected;
            } catch (SQLException e) {
                rollbackQuietly(conn);
                throw e;
            } finally {
                conn.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public List<PlayerGameInfo> fetchLatestPlayerGameInfo() throws SQLException {
        return playerGameDao.fetchLatestPlayerGameInfo();
    }

    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // Ignore rollback exception and let caller handle original SQL exception.
        }
    }
}
