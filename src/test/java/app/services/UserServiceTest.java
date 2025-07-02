package app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import app.domain.User;
import app.domain.dto.UserResponse;
import app.domain.enums.RoleType;
import app.domain.lifecycle.CascadeDeletionManager;
import app.mappers.UserMapper;
import app.repositories.UserRepository;
import app.security.AuthenticatedUser;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private AuthenticatedUser authenticatedUser;
	
	@Mock
	private UserMapper userMapper;
	
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	
	@Mock
	private CascadeDeletionManager cascadeDeletionManager;
	
	@Test
	void shouldPerformFindAllIfUserIsAdmin() {
		User authUser = new User();
		authUser.setRole(RoleType.ADMIN);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(userRepository.findAll()).thenReturn(List.of());
		when(userMapper.toDTO(anyList())).thenReturn(List.of());
		
		List<UserResponse> response = userService.findAll();
		
		assertNotNull(response);
		verify(userRepository).findAll();
		verifyNoMoreInteractions(userRepository);
	}
	
	@Test
	void shouldThrowExceptionIfUserIsNotAdmin() {
		User authUser = new User();
		authUser.setRole(RoleType.STUDENT);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		
		assertThrows(AccessDeniedException.class, () -> userService.findAll());
		verifyNoInteractions(userRepository);
	}
	
	@Test
	void shouldUpdateUserWithSuccess() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		User existingUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		User newUser = new User(null, "Gabriel 2", "gabriel2@gmail.com", "123", RoleType.STUDENT);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(existingUser)).thenReturn(existingUser);
		when(passwordEncoder.encode("123")).thenReturn("encodedPassword2");
		
		userService.update(newUser, 1L);
		
		assertEquals("Gabriel 2", existingUser.getName());
		assertEquals("gabriel2@gmail.com", existingUser.getEmail());
		assertEquals("encodedPassword2", existingUser.getPassword());
		verify(userRepository).findById(1L);
		verify(userRepository).save(existingUser);
		verifyNoMoreInteractions(userRepository);
	}
	
	@Test
	void shouldAllowAdminToUpdateOtherUser() {
		User authUser = new User(1L, "ADMIN", "admin@gmail.com", "encodedAdminPasssword", RoleType.ADMIN);
		User existingUser = new User(2L, "Teacher", "teacher@gmail.com", "encodedTeacherPasssword", RoleType.TEACHER);
		User newUser = new User(null, "Teacher 2", "teacher2@gmail.com", "123", RoleType.TEACHER);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(existingUser)).thenReturn(existingUser);
		when(passwordEncoder.encode("123")).thenReturn("encodedTeacherPassword2");
		
		userService.update(newUser, 2L);
		
		assertEquals("Teacher 2", existingUser.getName());
		assertEquals("teacher2@gmail.com", existingUser.getEmail());
		assertEquals("encodedTeacherPassword2", existingUser.getPassword());
		verify(userRepository).findById(2L);
		verify(userRepository).save(existingUser);
		verifyNoMoreInteractions(userRepository);
	}
	
	@Test
	void shouldThrowExceptionIfUserTriesToUpdateOtherUser() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		User newUser = new User(null, "Gabriel 2", "gabriel2@gmail.com", "123", RoleType.STUDENT);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		
		assertThrows(AccessDeniedException.class, () -> userService.update(newUser, 2L));
		verifyNoInteractions(userRepository);
	}
	
	@Test
	void shouldPerformDeleteWithSuccess() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		User userToBeDeleted = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(userRepository.findById(1L)).thenReturn(Optional.of(userToBeDeleted));
		
		userService.delete(1L);
		
		verify(cascadeDeletionManager).deleteStudentAndDependenciesByUserId(1L);
		verifyNoMoreInteractions(cascadeDeletionManager);
	}
	
	@Test
	void shouldAllowAdminToDeleteOtherUser() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.ADMIN);
		User userToBeDeleted = new User(2L, "John Doe", "john@gmail.com", "encodedPasssword", RoleType.TEACHER);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(userRepository.findById(2L)).thenReturn(Optional.of(userToBeDeleted));
		
		userService.delete(2L);
		
		verify(cascadeDeletionManager).deleteTeacherAndDependenciesByUserId(2L);
		verifyNoMoreInteractions(cascadeDeletionManager);
	}
	
	@Test
	void shouldThrowExceptionIfUserTriesToDeleteOtherUser() {
		User authUser = new User(1L, "Gabriel", "gabriel@gmail.com", "encodedPasssword", RoleType.STUDENT);
		User userToBeDeleted = new User(2L, "John Doe", "john@gmail.com", "encodedPasssword", RoleType.STUDENT);
		
		when(authenticatedUser.getAuthenticatedUser()).thenReturn(authUser);
		when(userRepository.findById(2L)).thenReturn(Optional.of(userToBeDeleted));
		
		assertThrows(AccessDeniedException.class, () -> userService.delete(2L));
		
		verifyNoInteractions(cascadeDeletionManager);
	}
 }
