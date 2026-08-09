package playerregistration.dto;

import java.time.LocalDate;

public record PlayerGameResponse(
        int id,
        String name,
        String address,
        String postalCode,
        String province,
        String phoneNumber,
        String gameTitle,
        int score,
        LocalDate datePlayed
) {
}
