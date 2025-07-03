package app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import app.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@EnableWebSecurity
@SecurityScheme(name = SecurityConfig.SECURITY, type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SecurityConfig {
	
	public static final String SECURITY = "bearerAuth";
	
	@Autowired
	private JwtAuthenticationFilter jwtFilter;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/auth/**").permitAll()
					.requestMatchers("/v3/api-docs/**", "swagger-ui/**", "swagger-ui.html").permitAll()
					.requestMatchers(HttpMethod.GET, "/users/findAll", "/users/findById").hasRole("ADMIN")
					.requestMatchers(HttpMethod.POST, "/courses/**", "/lessons/**").hasAnyRole("TEACHER", "ADMIN")
					.requestMatchers(HttpMethod.PUT, "/courses/**", "/lessons/**").hasAnyRole("TEACHER", "ADMIN")
					.requestMatchers(HttpMethod.DELETE, "/courses/**", "/lessons/**").hasAnyRole("TEACHER", "ADMIN")
					.anyRequest().authenticated())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
