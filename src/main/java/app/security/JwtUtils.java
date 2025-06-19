package app.security;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtils {
	
	@Value("${jwt.secret.key}")
	private String jwtSecret;
	
	@Value("${jwt.expiration.minutes}")
	private Integer jwtExpirationMinutes;

	private String appName = "plataformadecursos";
	
	public String generateJwtToken(String username) {
		Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
		return JWT.create()
				.withSubject(username)
				.withIssuer(appName)
				.withIssuedAt(ZonedDateTime.now().toInstant())
				.withExpiresAt(ZonedDateTime.now().plusMinutes(jwtExpirationMinutes).toInstant())
				.sign(algorithm);
	}
	
	public String validateTokenAndGetUsername(String token) {
		Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(appName).build();
		DecodedJWT decodedJwt = verifier.verify(token);
		return decodedJwt.getSubject();
	}
}
