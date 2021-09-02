package com.ramsey.racebook.resource.filter;

import com.ramsey.racebook.ejb.FriendshipBean;
import com.ramsey.racebook.entity.Friendship;
import com.ramsey.racebook.entity.FriendshipStatus;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;

@Stateless
public class Util {
	
	@EJB private FriendshipBean friendshipBean;
	
	public Boolean areTheyFriend(Long userId, Long headerUserId) {
		
		Friendship friendship;
		
		try {
			
			try {
				
				friendship = friendshipBean.getFriendship(headerUserId, userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(Exception ex) {
			
			return false;
			
		}
		
		return friendship.getFriendshipStatus().equals(FriendshipStatus.ACCEPTED);
		
	}
	
}
