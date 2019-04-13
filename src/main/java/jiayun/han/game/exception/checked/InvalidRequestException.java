package jiayun.han.game.exception.checked;

import jiayun.han.game.enums.ExceptionCode;

/**
 * 
 * @author Jiayun Han
 *
 */
public class InvalidRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	private ExceptionCode code;

	/**
	 * @param message
	 *            The message to be displayed
	 */
	public InvalidRequestException(ExceptionCode code, String message) {
		super(message);
		this.code = code;
	}

	public ExceptionCode getCode() {
		return code;
	}
}
