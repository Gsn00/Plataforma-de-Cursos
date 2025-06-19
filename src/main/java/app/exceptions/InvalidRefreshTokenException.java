package app.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidRefreshTokenException() {
		super("Invalid refresh token");
	}
}
