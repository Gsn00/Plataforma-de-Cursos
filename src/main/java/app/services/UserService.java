package app.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import app.domain.User;
import app.exceptions.ResourceNotFoundException;
import app.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
	}

	public User create(User obj) {
		obj.setPassword(new BCryptPasswordEncoder().encode(obj.getPassword()));
		return userRepository.save(obj);
	}

	public User update(User obj, Long id) {
		User existingUser = this.findById(id);
		
		existingUser.setName(obj.getName());
		existingUser.setEmail(obj.getEmail());
		existingUser.setPassword(new BCryptPasswordEncoder().encode(obj.getPassword()));
		
		return userRepository.save(existingUser);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}
	
	
}
