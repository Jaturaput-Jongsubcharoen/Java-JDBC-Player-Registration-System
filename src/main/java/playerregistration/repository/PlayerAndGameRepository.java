package playerregistration.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import playerregistration.entity.PlayerAndGame;

public interface PlayerAndGameRepository extends JpaRepository<PlayerAndGame, Integer> {

    Optional<PlayerAndGame> findFirstByPlayerPlayerIdOrderByPlayerDateDesc(Integer playerId);

    @Query("""
            SELECT pag
            FROM PlayerAndGame pag
            JOIN FETCH pag.player p
            JOIN FETCH pag.game g
            WHERE pag.playerDate = (
                SELECT MAX(innerPag.playerDate)
                FROM PlayerAndGame innerPag
                WHERE innerPag.player.playerId = p.playerId
            )
            """)
    List<PlayerAndGame> findLatestForEachPlayer();
}
