package jiayun.han.game.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * An exception of this class is triggered whenever the requested player cannot
 * be found.
 * 
 * @author Jiayun Han
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RestException {

	private static final String MESSAGE = "Player not found";

	private static final long serialVersionUID = 1L;

	/**
	 * @param requestId
	 *            The request ID string for logging purpose
	 */
	public PlayerNotFoundException(String requestId) {
		super(MESSAGE, requestId);
	}
}
