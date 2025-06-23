package app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyEnrolledException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UserAlreadyEnrolledException(String msg) {
		super(msg);
	}
}
