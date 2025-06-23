package app.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import app.domain.User;
import app.domain.dto.UserResponse;

@Component
public class UserMapper {
	
	public UserResponse toDTO(User user) {
		
		UserResponse userResponse = new UserResponse
				(user.getId(), user.getName(), user.getEmail());
		
		return userResponse;
	}
	
	public List<UserResponse> toDTO(List<User> list) {
		List<UserResponse> usersResponse = list.stream().map(user -> {
			return this.toDTO(user);
		}).collect(Collectors.toList());
		
		return usersResponse;
	}
}
