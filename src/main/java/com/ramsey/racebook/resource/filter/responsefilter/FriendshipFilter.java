package com.ramsey.racebook.resource.filter.responsefilter;

import com.ramsey.racebook.entity.Friendship;
import com.ramsey.racebook.entity.FriendshipStatus;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsFriendshipResponse;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.List;

@FilteredAsFriendshipResponse
@Provider
@Priority(Priorities.AUTHORIZATION)
public class FriendshipFilter implements ContainerResponseFilter {
	
	@SuppressWarnings("unchecked")
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		
		var headers = requestContext.getHeaders();
		var unauthorizedResponse = Response.status(Response.Status.UNAUTHORIZED).build();
		
		if(!headers.containsKey("userId")) {
			
			requestContext.abortWith(unauthorizedResponse);
			return;
			
		}
		
		Long headerUserId = Long.parseLong(headers.getFirst("userId"));
		
		if(responseContext.getEntityClass().equals(List.class)) {
			
			List<Friendship> entity = (List<Friendship>) responseContext.getEntity();
			Long userId = Long.parseLong(requestContext.getUriInfo().getPathParameters().getFirst("userId"));
			
			if(!userId.equals(headerUserId)) {
				
				entity.removeIf(friendship -> !friendship.getFriendshipStatus().equals(FriendshipStatus.ACCEPTED));
				
			}
			
		}
		
	}
	
}
