package playerregistration.entity;

public record PlayerEntity(
        int playerId,
        String firstName,
        String lastName,
        String address,
        String province,
        String postalCode,
        String phoneNumber
) {
}
