package app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	
	@InjectMocks
	private AuthService authService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private JwtUtils jwtUtils;
	
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	
	@Mock
	private RefreshTokenRepository refreshTokenRepository;
	
	@Mock
	private AuthenticatedUser authenticatedUser;
	
	@Test
	void shouldPerformLoginWithSuccess() {
		LoginDTO dto = new LoginDTO("john@gmail.com", "john123");
		
		User user = new User();
		user.setId(1L);
		user.setEmail("john@gmail.com");
		
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(user);
		when(jwtUtils.generateJwtToken(dto.email())).thenReturn("jwt-token");
		authService.jwtRefreshExpirationDays = 2;
		
		LoginResponse response = authService.login(dto);
		
		assertNotNull(response);
		assertEquals("jwt-token", response.token());
		assertNotNull(response.refreshToken());
		
		verify(authenticationManager).authenticate(any());
		verify(jwtUtils).generateJwtToken(dto.email());
	}
	
	@Test
	void shouldRegisterUserWithStudentRole() {
		RegisterDTO dto = new RegisterDTO("Gabriel Novais", "gabriel@gmail.com", "123456", RoleType.STUDENT);
		
		authService.register(dto);
		
		verify(userRepository).save(argThat(user -> 
			user.getName().equals("Gabriel Novais") && 
			user.getEmail().equals("gabriel@gmail.com") && 
			user.getRole().equals(RoleType.STUDENT)));
	}
	
	@Test
	void shouldRegisterAdminWhenAuthenticatedUserIsAdmin() {
		RegisterDTO dto = new RegisterDTO("ADMIN", "admin@gmail.com", "123456", RoleType.ADMIN);
		
		User authUser = new User();
		authUser.setRole(RoleType.ADMIN);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		
		authService.register(dto);
		
		verify(userRepository).save(argThat(user -> 
			user.getName().equals("ADMIN") &&
			user.getEmail().equals("admin@gmail.com") &&
			user.getRole().equals(RoleType.ADMIN)));
	}
	
	@Test
	void shouldThrowExceptionWhenStudentTriesToRegisterAdmin() {
		RegisterDTO dto = new RegisterDTO("ADMIN", "admin@gmail.com", "123456", RoleType.ADMIN);
		
		User authUser = new User();
		authUser.setRole(RoleType.STUDENT);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		
		assertThrows(AccessDeniedException.class, () -> authService.register(dto));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	void shouldThrowExceptionWhenNonAuthenticatedUserTriesToRegisterAdmin() {
		RegisterDTO dto = new RegisterDTO("ADMIN", "admin@gmail.com", "123456", RoleType.ADMIN);
		
		when(authenticatedUser.getAuthenticatedUser()).thenThrow(new AccessDeniedException("Only admins can create other admins."));
		
		assertThrows(AccessDeniedException.class, () -> authService.register(dto));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	void shouldCreateNewRefreshToken() {
		RefreshDTO dto = new RefreshDTO("refresh-token");
		
		Instant thisMoment = ZonedDateTime.now().toInstant();
		User user = new User(1L, "User", "user@gmail.com", "encodedPassword", RoleType.STUDENT);
		RefreshToken oldRefreshToken = new RefreshToken(1L, dto.refreshToken(), thisMoment.plusSeconds(60), user);
		
		when(refreshTokenRepository.findByRefreshTokenUUID(dto.refreshToken())).thenReturn(Optional.of(oldRefreshToken));
		when(jwtUtils.generateJwtToken("user@gmail.com")).thenReturn("new-access-token");
		authService.jwtRefreshExpirationDays = 2;
		
		RefreshResponse response = authService.refresh(dto);
		
		verify(refreshTokenRepository).deleteByUser(user);
		verify(refreshTokenRepository).save(any());
		assertEquals("new-access-token", response.token());
		assertNotNull(response.refreshToken());
	}
	
	@Test
	void shouldThrowExceptionWhenCallRefreshTokenWithExpiredToken() {
		RefreshDTO dto = new RefreshDTO("refresh-token");
		
		Instant thisMoment = ZonedDateTime.now().toInstant();
		User user = new User(1L, "User", "user@gmail.com", "encodedPassword", RoleType.STUDENT);
		RefreshToken oldRefreshToken = new RefreshToken(1L, dto.refreshToken(), thisMoment.minusSeconds(60), user);
		
		when(refreshTokenRepository.findByRefreshTokenUUID(dto.refreshToken())).thenReturn(Optional.of(oldRefreshToken));
		
		assertThrows(TokenExpiredException.class, () -> authService.refresh(dto));
	}
	
	@Test
	void shouldThrowExceptionWhenCallRefreshTokenWithInvalidToken() {
		RefreshDTO dto = new RefreshDTO("invalid-refresh-token");
		
		when(refreshTokenRepository.findByRefreshTokenUUID(dto.refreshToken())).thenReturn(Optional.empty());
		
		assertThrows(InvalidRefreshTokenException.class, () -> authService.refresh(dto));
		verify(refreshTokenRepository, never()).deleteByUser(any());
		verify(refreshTokenRepository, never()).save(any());
		verify(jwtUtils, never()).generateJwtToken(any());
	}
}
