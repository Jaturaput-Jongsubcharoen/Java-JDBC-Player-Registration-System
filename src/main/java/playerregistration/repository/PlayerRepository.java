package playerregistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import playerregistration.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
}
