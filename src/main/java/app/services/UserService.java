package app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import app.domain.User;
import app.domain.enums.RoleType;
import app.domain.lifecycle.CascadeDeletionManager;
import app.exceptions.ResourceNotFoundException;
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

	public List<User> findAll() {
		//Apenas admins
		return userRepository.findAll();
	}

	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}

	public void update(User obj, Long id) {
		//O usuário pode apenas atualizar a si mesmo, a menos que seja admin
		User user = authenticatedUser.getAuthenticatedUser();
		
		if (!user.getRole().equals(RoleType.ADMIN) && user.getId() != id)
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		User existingUser = this.findById(id);
		
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
		
		if (!user.getRole().equals(RoleType.ADMIN) && !user.getId().equals(id))
			throw new AccessDeniedException("You have no permission to access this endpoint.");
		
		if (user.getRole().equals(RoleType.STUDENT)) {
			cascadeDeletionManager.deleteStudentAndDependenciesByUserId(id);
		} else {
			cascadeDeletionManager.deleteTeacherAndDependenciesByUserId(id);
		}
	}
}
