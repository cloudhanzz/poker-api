package jiayun.han.game.util;

import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.HeadersBuilder;

import jiayun.han.game.enums.ProgressStatus;


/**
 * 
 * @author Jiayun Han
 *
 */
public final class LoggingUtil {
	
	private final Logger logger;
	
	public LoggingUtil(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Centralizing logging to reduce code duplication: log the request started
	 * 
	 * @param requestId
	 *            For logging and tracking
	 * @param method
	 *            Participating in logging
	 */
	public void logStart(String requestId, HttpMethod method, String message) {
		logInfo(requestId, method, message, ProgressStatus.STARTED);
	}
	


	/**
	 * Centralizing logging to reduce code duplication: log the request started
	 * 
	 * @param requestId
	 *            For logging and tracking
	 * @param method
	 *            Participating in logging
	 */
	public void logEnd(String requestId, HttpMethod method, String message) {
		logInfo(requestId, method, message, ProgressStatus.ENDED);
	}
	

	/**
	 * Centralizing logging to reduce code duplication: log the request completed
	 * with success
	 * 
	 * @param requestId
	 *            For logging and tracking
	 * @param method
	 *            Participating in logging
	 */
	private void logSuccess(String requestId, HttpMethod method) {
		logger.info("{}: {}: completed", requestId, method.name());
	}

	/**
	 * Centralizing logging to reduce code duplication: log the request completed
	 * with a status code of 204
	 * 
	 * @param requestId
	 *            For logging and tracking
	 * @param method
	 *            Participating in logging
	 */
	public ResponseEntity<?> logAndReturn204(String requestId, HttpMethod method) {
		return logAndReturn(requestId, ResponseEntity.noContent(), method);
	}

	/**
	 * Centralizing logging to reduce code duplication: log the request completed
	 * with success before return the response body
	 * 
	 * @param requestId
	 *            For logging and tracking
	 * @param headerBuilder
	 *            Used to build the response body
	 * @param method
	 *            Participating in logging
	 * @return The response body
	 */
	public ResponseEntity<?> logAndReturn(String requestId, HeadersBuilder<?> headerBuilder, HttpMethod method) {
		ResponseEntity<?> retvalue = headerBuilder.build();
		logSuccess(requestId, method);
		return retvalue;
	}
	


	/**
	 * Centralizing logging to reduce code duplication: log the request started
	 * 
	 * @param requestId
	 *            For logging and tracking
	 * @param method
	 *            Participating in logging
	 */
	private void logInfo(String requestId, HttpMethod method, String message, ProgressStatus status) {
		logger.info("x-request-id={}: {}: {}: {}", requestId, method.name(), message, status);
	}
}
