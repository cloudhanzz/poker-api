package jiayun.han.game.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the ancestor of all exception classes defined for this project,
 * responsible for logging the error message descriptively.
 * 
 * @author Jiayun Han
 *
 */
public class RestException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger(RestException.class);

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param message
	 *            The message provided by inherited class
	 * @param requestId
	 *            The request ID string for logging purpose
	 */
	public RestException(String message, String requestId) {
		super(String.format("%s: %s", requestId, message));
		logger.error("{}: {}", requestId, message);
	}
}
