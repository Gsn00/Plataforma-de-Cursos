package app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.config.SecurityConfig;
import app.domain.User;
import app.domain.dto.UserResponse;
import app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class UserController {

	@Autowired
	private UserService userService;
	
	@Operation(summary = "Busca todos os usuários (apenas admins)", method = "GET")
	@GetMapping
	public ResponseEntity<List<UserResponse>> findAll() {
		List<UserResponse> list = userService.findAll();
		return ResponseEntity.ok(list);
	}
	
	@Operation(summary = "Busca um usuário por ID (todos podem acessar)", method = "GET")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
		UserResponse user = userService.findById(id);
		return ResponseEntity.ok(user);
	}
	
	@Operation(summary = "Atualiza os dados de um usuário (o usuário pode atualizar a si mesmo, admins podem atualizar outros)", method = "PUT")
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@RequestBody User obj, @PathVariable Long id) {
		userService.update(obj, id);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Deleta um usuário (o usuário pode deletar a si mesmo, admins podem deletar outros)", method = "DELETE")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.ok().build();
	}
}
