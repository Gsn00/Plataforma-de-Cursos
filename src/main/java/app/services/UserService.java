package app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import app.domain.User;
import app.domain.dto.UserResponse;
import app.domain.enums.RoleType;
import app.domain.lifecycle.CascadeDeletionManager;
import app.exceptions.ResourceNotFoundException;
import app.mappers.UserMapper;
import app.repositories.UserRepository;
import app.security.AuthenticatedUser;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CascadeDeletionManager cascadeDeletionManager;
	
	@Autowired
	private AuthenticatedUser authenticatedUser;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserMapper userMapper;

	public List<UserResponse> findAll() {
		//Apenas admins
		User user = authenticatedUser.getAuthenticatedUser();
		if (!user.getRole().equals(RoleType.ADMIN))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		List<User> users = userRepository.findAll();
		
		return userMapper.toDTO(users);
	}

	public User findByIdEntity(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}
	
	public UserResponse findById(Long id) {
		User user = this.findByIdEntity(id);
		return userMapper.toDTO(user);
	}

	public void update(User obj, Long id) {
		//O usuário pode apenas atualizar a si mesmo, a menos que seja admin
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (!user.getRole().equals(RoleType.ADMIN) && user.getId() != id)
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		User existingUser = this.findByIdEntity(id);
		
		if (obj.getName() != null && !obj.getName().isBlank())
			existingUser.setName(obj.getName());
		
		if (obj.getEmail() != null && !obj.getEmail().isBlank())
			existingUser.setEmail(obj.getEmail());
		
		if (obj.getPassword() != null && !obj.getPassword().isBlank())
			existingUser.setPassword(passwordEncoder.encode(obj.getPassword()));
		
		userRepository.save(existingUser);
	}

	public void delete(Long id) {
		//O usuário pode apenas deletar a si mesmo, a menos que seja admin
		User user = authenticatedUser.getAuthenticatedUser();
		User userToBeDeleted = this.findByIdEntity(id);
		
		if (!user.getRole().equals(RoleType.ADMIN) && !user.getId().equals(id))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		switch (userToBeDeleted.getRole()) {
			case STUDENT -> cascadeDeletionManager.deleteStudentAndDependenciesByUserId(id);
			case TEACHER -> cascadeDeletionManager.deleteTeacherAndDependenciesByUserId(id);
			default -> cascadeDeletionManager.deleteTeacherAndDependenciesByUserId(id);
		}
	}
}
