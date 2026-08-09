package playerregistration.entity;

import java.time.LocalDate;

public record PlayerGameEntity(
        int playerId,
        String fullName,
        String address,
        String postalCode,
        String province,
        String phoneNumber,
        String gameTitle,
        int score,
        LocalDate datePlayed
) {
}
