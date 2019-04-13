package jiayun.han.game.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * An exception of this class is triggered upon attempting to post the same
 * Player in the same game for more than once.
 * 
 * @author Jiayun Han
 *
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CreateSamePlayerMoreThanOnceException extends RestException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param requestId
	 *            The request ID string for logging purpose
	 */
	public CreateSamePlayerMoreThanOnceException(String message, String requestId) {
		super(message, requestId);
	}
}
