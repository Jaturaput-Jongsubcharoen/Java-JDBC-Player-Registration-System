package playerregistration.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import playerregistration.dto.CreatePlayerRequest;
import playerregistration.dto.PlayerGameResponse;
import playerregistration.dto.UpdatePlayerRequest;
import playerregistration.entity.PlayerGameEntity;
import playerregistration.repository.PlayerRegistrationRepository;

@Service
public class PlayerRegistrationApiService {

    private final PlayerRegistrationRepository repository;

    public PlayerRegistrationApiService(PlayerRegistrationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PlayerGameResponse createPlayer(CreatePlayerRequest request) {
        int playerId = repository.createPlayerGame(request);
        return repository.fetchLatestPlayerGameInfoByPlayerId(playerId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("Unable to fetch created player."));
    }

    @Transactional
    public Optional<PlayerGameResponse> updatePlayer(int playerId, UpdatePlayerRequest request) {
        if (!repository.existsPlayerId(playerId)) {
            return Optional.empty();
        }

        repository.updatePlayerGame(playerId, request);
        return repository.fetchLatestPlayerGameInfoByPlayerId(playerId).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PlayerGameResponse> fetchPlayers() {
        return repository.fetchLatestPlayerGameInfo().stream()
                .map(this::toResponse)
                .toList();
    }

    private PlayerGameResponse toResponse(PlayerGameEntity entity) {
        return new PlayerGameResponse(
                entity.playerId(),
                entity.fullName(),
                entity.address(),
                entity.postalCode(),
                entity.province(),
                entity.phoneNumber(),
                entity.gameTitle(),
                entity.score(),
                entity.datePlayed()
        );
    }
}
