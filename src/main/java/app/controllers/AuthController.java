package app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.dto.LoginDTO;
import app.domain.dto.LoginResponse;
import app.domain.dto.RefreshDTO;
import app.domain.dto.RefreshResponse;
import app.domain.dto.RegisterDTO;
import app.services.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO login) {		
		LoginResponse response = authService.login(login);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshDTO refreshDto) {
		RefreshResponse response = authService.refresh(refreshDto);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO register) {
		authService.register(register);
		return ResponseEntity.ok().build();
	}
}
