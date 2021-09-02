package com.ramsey.racebook.exception;

public class FriendshipNotFoundException extends Exception {
	
	private static final long serialVersionUID = -6516592703337341370L;
	
	public FriendshipNotFoundException(Long source, Long target) {
		
		super(String.format("There are no friendship between %d, and %d", source, target));
		
	}
	
	public FriendshipNotFoundException(Long userId) {
		
		super(String.format("The user %d isn't involved in any friendships", userId));
		
	}
	
}
