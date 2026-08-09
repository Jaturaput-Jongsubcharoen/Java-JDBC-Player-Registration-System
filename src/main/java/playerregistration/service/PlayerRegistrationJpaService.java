package playerregistration.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playerregistration.dto.CreatePlayerRequest;
import playerregistration.dto.PlayerGameResponse;
import playerregistration.dto.UpdatePlayerRequest;
import playerregistration.entity.Game;
import playerregistration.entity.Player;
import playerregistration.entity.PlayerAndGame;
import playerregistration.repository.GameRepository;
import playerregistration.repository.PlayerAndGameRepository;
import playerregistration.repository.PlayerRepository;

@Service
public class PlayerRegistrationJpaService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PlayerAndGameRepository playerAndGameRepository;

    public PlayerRegistrationJpaService(
            PlayerRepository playerRepository,
            GameRepository gameRepository,
            PlayerAndGameRepository playerAndGameRepository
    ) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.playerAndGameRepository = playerAndGameRepository;
    }

    @Transactional
    public PlayerGameResponse createPlayerGameWorkflow(CreatePlayerRequest request) {
        validateBusinessRules(request.datePlayed());

        Player player = new Player();
        player.setFirstName(request.firstName());
        player.setLastName(request.lastName());
        player.setAddress(request.address());
        player.setPostalCode(request.postalCode());
        player.setProvince(request.province());
        player.setPhoneNumber(Long.parseLong(request.phoneNumber()));
        Player savedPlayer = playerRepository.save(player);

        Game game = new Game();
        game.setGameTitle(request.gameTitle());
        Game savedGame = gameRepository.save(game);

        PlayerAndGame playerAndGame = new PlayerAndGame();
        playerAndGame.setPlayer(savedPlayer);
        playerAndGame.setGame(savedGame);
        playerAndGame.setPlayerDate(request.datePlayed());
        playerAndGame.setScore(request.score());
        PlayerAndGame saved = playerAndGameRepository.save(playerAndGame);

        return toResponse(saved);
    }

    @Transactional
    public Optional<PlayerGameResponse> updatePlayerGameWorkflow(int playerId, UpdatePlayerRequest request) {
        validateBusinessRules(request.datePlayed());

        Optional<Player> existingPlayer = playerRepository.findById(playerId);
        if (existingPlayer.isEmpty()) {
            return Optional.empty();
        }

        Player player = existingPlayer.get();
        player.setFirstName(request.firstName());
        player.setLastName(request.lastName());
        player.setAddress(request.address());
        player.setProvince(request.province());
        player.setPostalCode(request.postalCode());
        player.setPhoneNumber(Long.parseLong(request.phoneNumber()));
        playerRepository.save(player);

        PlayerAndGame latest = playerAndGameRepository
                .findFirstByPlayerPlayerIdOrderByPlayerDateDesc(playerId)
                .orElseThrow(() -> new IllegalStateException("Player exists but no related game record was found."));

        Game game = latest.getGame();
        game.setGameTitle(request.gameTitle());
        gameRepository.save(game);

        latest.setPlayerDate(request.datePlayed());
        latest.setScore(request.score());
        PlayerAndGame updated = playerAndGameRepository.save(latest);

        return Optional.of(toResponse(updated));
    }

    @Transactional
    public int deletePlayerWorkflow(int playerId) {
        if (!playerRepository.existsById(playerId)) {
            return 0;
        }

        playerRepository.deleteById(playerId);
        return gameRepository.deleteUnusedGames();
    }

    @Transactional(readOnly = true)
    public List<PlayerGameResponse> fetchLatestPlayerGameInfo() {
        return playerAndGameRepository.findLatestForEachPlayer()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateBusinessRules(LocalDate playerDate) {
        if (playerDate == null || playerDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date played must be today or in the past.");
        }
    }

    private PlayerGameResponse toResponse(PlayerAndGame row) {
        Player player = row.getPlayer();
        Game game = row.getGame();

        return new PlayerGameResponse(
                player.getPlayerId(),
                player.getFirstName() + " " + player.getLastName(),
                player.getAddress(),
                player.getPostalCode(),
                player.getProvince(),
                String.valueOf(player.getPhoneNumber()),
                game.getGameTitle(),
                row.getScore(),
                row.getPlayerDate()
        );
    }
}
