package com.ramsey.racebook.exception;

public class PostNotFoundException extends Exception {
	
	private static final long serialVersionUID = 344909532899496186L;
	
	public PostNotFoundException(Long id) {
		
		super(String.format("The user with ID %d, was not found", id));
		
	}
	
	public PostNotFoundException(String content) {
		
		super(String.format("Post %s, was not found", content));
		
	}
	
}
