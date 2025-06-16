package app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
