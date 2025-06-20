package app.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.TokenExpiredException;

import app.domain.RefreshToken;
import app.domain.User;
import app.domain.dto.LoginDTO;
import app.domain.dto.LoginResponse;
import app.domain.dto.RefreshDTO;
import app.domain.dto.RefreshResponse;
import app.domain.dto.RegisterDTO;
import app.domain.enums.RoleType;
import app.exceptions.InvalidRefreshTokenException;
import app.repositories.RefreshTokenRepository;
import app.repositories.UserRepository;
import app.security.AuthenticatedUser;
import app.security.JwtUtils;

@Service
public class AuthService {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticatedUser authenticatedUser;
	
	@Value("${jwt.refresh.expiration.days}")
	private Integer jwtRefreshExpirationDays;
	
	public void register(RegisterDTO register) {
		if (register.role().equals(RoleType.ADMIN)) {
			User authUser = authenticatedUser.getAuthenticatedUser();
			if (!authUser.getRole().equals(RoleType.ADMIN))
				throw new AccessDeniedException("Only admins can create other admins.");
		}
		
		User user = new User
				(null, register.name(), register.email(), passwordEncoder.encode(register.password()), register.role());
		userRepository.save(user);
	}

	public RefreshResponse refresh(RefreshDTO refreshDto) {
		RefreshToken oldRefreshToken = refreshTokenRepository.findByRefreshTokenUUID(refreshDto.refreshToken())
				.orElseThrow(() -> new InvalidRefreshTokenException());
		
		Instant thisMoment = ZonedDateTime.now().toInstant();
		if (oldRefreshToken.getExpiration().isBefore(thisMoment))
			throw new TokenExpiredException("The refresh token has expired on ", oldRefreshToken.getExpiration());
		
		User user = oldRefreshToken.getUser();
		
		String jwtToken = jwtUtils.generateJwtToken(user.getEmail());
		String refreshTokenUUID = UUID.randomUUID().toString();
		
		refreshTokenRepository.deleteByUser(user);
		
		Instant expiration = ZonedDateTime.now().plusDays(jwtRefreshExpirationDays).toInstant();
		refreshTokenRepository.save(new RefreshToken(null, refreshTokenUUID, expiration, user));
		
		return new RefreshResponse(jwtToken, refreshTokenUUID);
	}
	
	public LoginResponse login(LoginDTO login) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.email(), login.password()));
		
		User user = (User) authentication.getPrincipal();
		
		String jwtToken = jwtUtils.generateJwtToken(user.getEmail());
		String refreshTokenUUID = UUID.randomUUID().toString();
		
		refreshTokenRepository.deleteByUser(user);
		
		Instant expiration = LocalDateTime.now().plusDays(jwtRefreshExpirationDays).toInstant(ZoneOffset.UTC);
		refreshTokenRepository.save(new RefreshToken(null, refreshTokenUUID, expiration, user));
		
		return new LoginResponse(jwtToken, refreshTokenUUID);
	}
}
