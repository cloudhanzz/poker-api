package jiayun.han.game.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * An exception of this class is triggered when creating a game with too many
 * players.
 * 
 * <p>
 * How many players are too many? The actual business rule will decide
 * 
 * @author Jiayun Han
 *
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class NoEnoughRemainingCardsException extends RestException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *            The message to be displayed
	 */
	public NoEnoughRemainingCardsException(String message) {
		this(message, null);
	}

	/**
	 * @param message
	 *            The message to be displayed
	 * @param requestId
	 *            The request ID string for logging purpose
	 */
	public NoEnoughRemainingCardsException(String message, String requestId) {
		super(message, requestId);
	}
}
