package playerregistration.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import playerregistration.dto.CreatePlayerRequest;
import playerregistration.dto.PlayerGameResponse;
import playerregistration.dto.UpdatePlayerRequest;
import playerregistration.service.PlayerRegistrationApiService;

@RestController
@RequestMapping("/api/players")
@Validated
public class PlayerRegistrationController {

    private final PlayerRegistrationApiService apiService;

    public PlayerRegistrationController(PlayerRegistrationApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    public List<PlayerGameResponse> getPlayers() {
        return apiService.fetchPlayers();
    }

    @PostMapping
    public ResponseEntity<PlayerGameResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        PlayerGameResponse response = apiService.createPlayer(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{playerId}")
    public ResponseEntity<PlayerGameResponse> updatePlayer(
            @PathVariable int playerId,
            @Valid @RequestBody UpdatePlayerRequest request
    ) {
        return apiService.updatePlayer(playerId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
