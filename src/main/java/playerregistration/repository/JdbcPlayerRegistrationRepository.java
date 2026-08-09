package playerregistration.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import playerregistration.dto.CreatePlayerRequest;
import playerregistration.dto.UpdatePlayerRequest;
import playerregistration.entity.PlayerGameEntity;

@Repository
public class JdbcPlayerRegistrationRepository implements PlayerRegistrationRepository {

    private static final String FETCH_LATEST_SQL = """
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

    private final JdbcTemplate jdbcTemplate;

    public JdbcPlayerRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createPlayerGame(CreatePlayerRequest request) {
        jdbcTemplate.update(
                "INSERT INTO Player (first_name, last_name, address, province, postal_code, phone_number) VALUES (?, ?, ?, ?, ?, ?)",
                request.firstName(),
                request.lastName(),
                request.address(),
                request.province(),
                request.postalCode(),
                request.phoneNumber()
        );

        jdbcTemplate.update(
                "INSERT INTO Game (game_title) VALUES (?)",
                request.gameTitle()
        );

        Integer playerId = jdbcTemplate.queryForObject("SELECT MAX(player_id) FROM Player", Integer.class);
        Integer gameId = jdbcTemplate.queryForObject("SELECT MAX(game_id) FROM Game", Integer.class);

        if (playerId == null || gameId == null) {
            throw new IllegalStateException("Unable to resolve newly created player/game IDs.");
        }

        jdbcTemplate.update(
                "INSERT INTO PlayerAndGame (player_id, game_id, player_date, score) VALUES (?, ?, ?, ?)",
                playerId,
                gameId,
                Date.valueOf(request.datePlayed()),
                request.score()
        );

        return playerId;
    }

    @Override
    public boolean existsPlayerId(int playerId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Player WHERE player_id = ?",
                Integer.class,
                playerId
        );
        return count != null && count > 0;
    }

    @Override
    public void updatePlayerGame(int playerId, UpdatePlayerRequest request) {
        jdbcTemplate.update(
                "UPDATE Player SET first_name = ?, last_name = ?, address = ?, province = ?, postal_code = ?, phone_number = ? WHERE player_id = ?",
                request.firstName(),
                request.lastName(),
                request.address(),
                request.province(),
                request.postalCode(),
                request.phoneNumber(),
                playerId
        );

        jdbcTemplate.update(
                """
                UPDATE Game
                SET game_title = ?
                WHERE game_id = (
                    SELECT game_id FROM PlayerAndGame WHERE player_id = ?
                )
                """,
                request.gameTitle(),
                playerId
        );

        jdbcTemplate.update(
                "UPDATE PlayerAndGame SET player_date = ?, score = ? WHERE player_id = ?",
                Date.valueOf(request.datePlayed()),
                request.score(),
                playerId
        );
    }

    @Override
    public List<PlayerGameEntity> fetchLatestPlayerGameInfo() {
        return jdbcTemplate.query(FETCH_LATEST_SQL, playerGameRowMapper());
    }

    @Override
    public Optional<PlayerGameEntity> fetchLatestPlayerGameInfoByPlayerId(int playerId) {
        String sql = FETCH_LATEST_SQL + " AND p.player_id = ?";
        List<PlayerGameEntity> rows = jdbcTemplate.query(sql, playerGameRowMapper(), playerId);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rows.get(0));
    }

    private RowMapper<PlayerGameEntity> playerGameRowMapper() {
        return (rs, rowNum) -> {
            Date sqlDate = rs.getDate("DATE_PLAYED");
            LocalDate datePlayed = sqlDate == null ? null : sqlDate.toLocalDate();
            return new PlayerGameEntity(
                    rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("POSTAL_CODE"),
                    rs.getString("PROVINCE"),
                    rs.getString("PHONE_NUMBER"),
                    rs.getString("GAME_TITLE"),
                    rs.getInt("SCORE"),
                    datePlayed
            );
        };
    }
}
