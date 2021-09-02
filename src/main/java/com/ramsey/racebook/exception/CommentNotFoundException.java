package com.ramsey.racebook.exception;

public class CommentNotFoundException extends Exception {
	
	private static final long serialVersionUID = -5652698120797546284L;
	
	public CommentNotFoundException(Long id) {
		
		super(String.format("Comment with ID %d, was not found", id));
		
	}
	
	public CommentNotFoundException(String content) {
		
		super(String.format("Comment %s, was not found", content));
		
	}
	
}
