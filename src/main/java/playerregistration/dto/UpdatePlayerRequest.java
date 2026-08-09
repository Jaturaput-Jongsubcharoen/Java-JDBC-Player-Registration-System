package playerregistration.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePlayerRequest(
        @NotBlank @Pattern(regexp = "[a-zA-Z]+") String firstName,
        @NotBlank @Pattern(regexp = "[a-zA-Z]+") String lastName,
        @NotBlank String address,
        @NotBlank @Pattern(regexp = "[a-zA-Z]{2}") String province,
        @NotBlank @Size(min = 6, max = 6) @Pattern(regexp = "[a-zA-Z0-9]{6}") String postalCode,
        @NotBlank @Pattern(regexp = "\\d{10}") String phoneNumber,
        @NotBlank @Size(max = 20) String gameTitle,
        @NotNull LocalDate datePlayed,
        @Min(0) int score
) {
}
