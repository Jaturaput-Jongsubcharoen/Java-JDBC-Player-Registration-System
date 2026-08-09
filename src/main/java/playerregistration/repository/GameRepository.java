package playerregistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import playerregistration.entity.Game;

public interface GameRepository extends JpaRepository<Game, Integer> {

    @Modifying
    @Query("DELETE FROM Game g WHERE g.gameId NOT IN (SELECT pag.game.gameId FROM PlayerAndGame pag)")
    int deleteUnusedGames();
}
