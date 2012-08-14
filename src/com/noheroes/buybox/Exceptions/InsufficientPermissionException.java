/*
 * Copyright (C) 2012 No Heroes.
 * See readme for license details.
 */
package com.noheroes.buybox.Exceptions;

public class InsufficientPermissionException extends Exception {
	private final static long serialVersionUID = 1l;
	private String message;
	
	/*
	 * Setting the message of the Exception to a new String
	 */
	public InsufficientPermissionException () {
		this.message = "You do not have permission for that.";
	}
	
	/*
	 * Setting the message of the Exception to the given message
	 */
	public InsufficientPermissionException (String message) {
		this.message = message;
	}
	
	/*
	 * returns the message
	 */
	@Override
	public String getMessage () {
		return this.message;
	}
}
