package app.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import app.domain.RefreshToken;
import app.domain.User;
import app.domain.dto.LoginDTO;
import app.domain.dto.LoginResponse;
import app.domain.dto.RefreshDTO;
import app.domain.dto.RefreshResponse;
import app.repositories.RefreshTokenRepository;
import app.repositories.UserRepository;
import app.security.JwtUtils;

@Service
public class AuthService {
	
	//////////////////////////////////////////////////////////////////////
	///
	///					Criar as Exceptions
	///
	//////////////////////////////////////////////////////////////////////
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Value("${jwt.refresh.expiration.days}")
	private Integer jwtRefreshExpirationDays;
	
	public void register(User register) {
		register.setPassword(new BCryptPasswordEncoder().encode(register.getPassword()));
		register.setRole(register.getRole());
		userRepository.save(register);
	}

	public RefreshResponse refresh(RefreshDTO refreshDto) {
		RefreshToken oldRefreshToken = refreshTokenRepository.findByRefreshTokenUUID(refreshDto.refreshToken())
				.orElseThrow(() -> new RuntimeException("Invalid refresh token"));
		
		if (!oldRefreshToken.getRefreshTokenUUID().equals(refreshDto.refreshToken()))
			throw new RuntimeException("Invalid refresh token");
		
		Instant thisMoment = LocalDateTime.now().toInstant(ZoneOffset.UTC);
		if (oldRefreshToken.getExpiration().isBefore(thisMoment))
			throw new RuntimeException("Refresh token expired");
		
		User user = oldRefreshToken.getUser();
		
		String jwtToken = jwtUtils.generateJwtToken(user.getEmail());
		String refreshTokenUUID = UUID.randomUUID().toString();
		
		refreshTokenRepository.deleteByUser(user);
		
		Instant expiration = LocalDateTime.now().plusDays(jwtRefreshExpirationDays).toInstant(ZoneOffset.UTC);
		refreshTokenRepository.save(new RefreshToken(null, refreshTokenUUID, expiration, user));
		
		return new RefreshResponse(jwtToken, refreshTokenUUID);
	}
	
	public LoginResponse login(LoginDTO login) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.email(), login.password()));
		String jwtToken = jwtUtils.generateJwtToken(authentication.getName());
		String refreshTokenUUID = UUID.randomUUID().toString();
		
		User user = userRepository.findByEmail(login.email()).orElseThrow(() -> new UsernameNotFoundException("User not found."));
		refreshTokenRepository.deleteByUser(user);
		
		Instant expiration = LocalDateTime.now().plusDays(jwtRefreshExpirationDays).toInstant(ZoneOffset.UTC);
		refreshTokenRepository.save(new RefreshToken(null, refreshTokenUUID, expiration, user));
		
		return new LoginResponse(jwtToken, refreshTokenUUID);
	}
}
