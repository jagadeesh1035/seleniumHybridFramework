package com.cust.framework;

public class FrameworkException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String errorName = "Error";

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(String errorName, String message) {
		super(message);
		this.errorName = errorName;
	}

	public FrameworkException(Throwable cause) {
		super(cause);
	}
}
