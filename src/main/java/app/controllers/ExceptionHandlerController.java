package app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;

import app.exceptions.InvalidRefreshTokenException;
import app.exceptions.ResourceNotFoundException;
import app.exceptions.UserAlreadyEnrolledException;

@RestControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler({DataIntegrityViolationException.class, DuplicateKeyException.class})
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public Map<String, String> handleUniqueConstraint(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Resource already exists");
        error.put("message", ex.getRootCause().getMessage());
        return error;
    }
	
	@ExceptionHandler(InvalidRefreshTokenException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public Map<String, String> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not allowed");
        error.put("message", ex.getMessage());
        return error;
    }
	
	@ExceptionHandler(TokenExpiredException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public Map<String, String> handleTokenExpired(TokenExpiredException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not allowed");
        error.put("message", ex.getMessage());
        return error;
    }
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidEnum(HttpMessageNotReadableException ex) {
		Map<String, String> error = new HashMap<>();
        error.put("error", "Bad request");
        error.put("message", ex.getMessage());
        return error;
    }
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
		Map<String, String> error = new HashMap<>();
        error.put("error", "Access denied");
        error.put("message", ex.getMessage());
        return error;
    }
	
	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
    public Map<String, String> handleBadCredentials(BadCredentialsException ex) {
		Map<String, String> error = new HashMap<>();
        error.put("error", "Access denied");
        error.put("message", ex.getMessage());
        return error;
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> error = new HashMap<>();
        error.put("error", "Method Argument Not Valid");
        ex.getBindingResult().getFieldErrors().forEach(field -> error.put(field.getField(), field.getDefaultMessage()));
        return error;
    }
	
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleResourceNotFound(ResourceNotFoundException ex) {
		Map<String, String> error = new HashMap<>();
        error.put("error", "Resource Not Found");
        error.put("message", ex.getMessage());
        return error;
    }
	
	@ExceptionHandler(UserAlreadyEnrolledException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUserAlreadyEnrolled(UserAlreadyEnrolledException ex) {
		Map<String, String> error = new HashMap<>();
        error.put("error", "User Already Enrolled");
        error.put("message", ex.getMessage());
        return error;
    }
	
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRuntimeException(RuntimeException ex) {
		Map<String, String> error = new HashMap<>();
        error.put("error", "Runtime Exception");
        error.put("message", ex.getMessage());
        return error;
    }
}
