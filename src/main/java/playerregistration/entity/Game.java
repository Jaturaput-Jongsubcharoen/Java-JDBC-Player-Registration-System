package playerregistration.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "game_title", nullable = false, length = 20)
    private String gameTitle;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private Set<PlayerAndGame> playerAndGames = new LinkedHashSet<>();

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Set<PlayerAndGame> getPlayerAndGames() {
        return playerAndGames;
    }

    public void setPlayerAndGames(Set<PlayerAndGame> playerAndGames) {
        this.playerAndGames = playerAndGames;
    }
}
