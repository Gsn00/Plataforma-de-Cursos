package app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import app.domain.RefreshToken;
import app.domain.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshTokenUUID(String refreshTokenUUID);
	
	@Transactional
	void deleteByUser(User user);
}
