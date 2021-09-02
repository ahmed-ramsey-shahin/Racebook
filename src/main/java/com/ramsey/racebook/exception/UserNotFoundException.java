package com.ramsey.racebook.exception;

public class UserNotFoundException extends Exception {
	
	private static final long serialVersionUID = -3228155197469037595L;
	
	public UserNotFoundException(Long id) {
		
		super(String.format("The user with ID %d, was not found", id));
		
	}
	
	public UserNotFoundException(String username) {
		
		super(String.format("%s, was not found", username));
		
	}
	
}
