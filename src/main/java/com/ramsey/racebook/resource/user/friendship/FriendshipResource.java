package com.ramsey.racebook.resource.user.friendship;

import com.ramsey.racebook.ejb.FriendshipBean;
import com.ramsey.racebook.entity.Friendship;
import com.ramsey.racebook.exception.FriendshipNotFoundException;
import com.ramsey.racebook.exception.UserNotFoundException;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsFriendship;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.*;

@RequestScoped
public class FriendshipResource {
	
	@EJB private FriendshipBean friendshipBean;
	@PathParam("userId") private Long userId;
	
	@GET
	@FilteredAsFriendship
	public Response getFriendships() {
		
		List<Friendship> friendships;
		
		try {
			
			try {
				
				friendships = friendshipBean.getFriendships(userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(UserNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The user you're looking for was not found")
					.build();
			
		} catch(FriendshipNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(new GenericEntity<>(friendships){})
				.build();
		
	}
	
}
