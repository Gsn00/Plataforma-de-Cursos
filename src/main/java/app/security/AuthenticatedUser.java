package app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import app.domain.User;

@Component
public class AuthenticatedUser {

	public User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null) {
			return (User) authentication.getPrincipal();
		}
		
		throw new UsernameNotFoundException("User not found.");
	}
	
}
