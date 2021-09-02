package com.ramsey.racebook.resource.filter.requestfilter;

import com.ramsey.racebook.resource.filter.Util;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsPost;
import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@FilteredAsPost
@Provider
@Priority(Priorities.AUTHORIZATION)
public class PostsFilter implements ContainerRequestFilter {

	@EJB private Util util;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		var pathParams = requestContext.getUriInfo().getPathParameters();
		var headers = requestContext.getHeaders();
		var unauthorizedResponse = Response.status(Response.Status.UNAUTHORIZED)
				.entity("User cannot access the resource")
				.build();
		
		if(!headers.containsKey("userId")) {
			
			requestContext.abortWith(unauthorizedResponse);
			return;
			
		}
		
		Long headerUserId = Long.parseLong(headers.getFirst("userId"));
		
		if(requestContext.getMethod().equalsIgnoreCase("POST")) {
			
			Long userId = Long.parseLong(pathParams.getFirst("userId"));
			
			if(!userId.equals(headerUserId)) {
				
				requestContext.abortWith(unauthorizedResponse);
				
			}
			
		} else if(requestContext.getMethod().equalsIgnoreCase("GET")) {
			
			Long userId = Long.parseLong(pathParams.getFirst("userId"));
			
			if(!userId.equals(headerUserId)) {
				
				if(!util.areTheyFriend(userId, headerUserId)) {
					
					requestContext.abortWith(unauthorizedResponse);
					
				}
				
			}
			
		} else if(
				requestContext.getMethod().equalsIgnoreCase("PUT")
				|| requestContext.getMethod().equalsIgnoreCase("DELETE")
		) {
			
			Long userId = Long.parseLong(pathParams.getFirst("userId"));
			
			if(!headerUserId.equals(userId)) {
				
				requestContext.abortWith(unauthorizedResponse);
				
			}
			
		}
		
	}
	
}
