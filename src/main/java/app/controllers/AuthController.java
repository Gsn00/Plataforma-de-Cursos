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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@Operation(summary = "Efetua o login e retorna um access token e um refresh token.", method = "POST")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO login) {		
		LoginResponse response = authService.login(login);
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "Renova o access token usando o refresh token.", method = "POST")
	@PostMapping("/refresh")
	public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshDTO refreshDto) {
		RefreshResponse response = authService.refresh(refreshDto);
		return ResponseEntity.ok(response);
	}
	
	@Operation(summary = "Cria uma nova conta. (se for criar uma conta admin, precisa estar logado em uma outra conta admin)", method = "POST")
	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO register) {
		authService.register(register);
		return ResponseEntity.ok().build();
	}
}
