package playerregistration.repository;

import java.util.List;
import java.util.Optional;

import playerregistration.dto.CreatePlayerRequest;
import playerregistration.dto.UpdatePlayerRequest;
import playerregistration.entity.PlayerGameEntity;

public interface PlayerRegistrationRepository {

    int createPlayerGame(CreatePlayerRequest request);

    boolean existsPlayerId(int playerId);

    void updatePlayerGame(int playerId, UpdatePlayerRequest request);

    List<PlayerGameEntity> fetchLatestPlayerGameInfo();

    Optional<PlayerGameEntity> fetchLatestPlayerGameInfoByPlayerId(int playerId);
}
