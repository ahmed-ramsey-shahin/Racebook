package com.ramsey.racebook.resource.filter.requestfilter;

import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsFriendship;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@FilteredAsFriendship
@Provider
@Priority(Priorities.AUTHORIZATION)
public class FriendshipsFilter implements ContainerRequestFilter {
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		var pathParams = requestContext.getUriInfo().getPathParameters();
		var headers = requestContext.getHeaders();
		var queryParams = requestContext.getUriInfo().getQueryParameters();
		var unauthorizedResponse = Response.status(Response.Status.UNAUTHORIZED)
				.entity("User cannot access the resource")
				.build();
		
		if(!headers.containsKey("userId")) {
			
			requestContext.abortWith(unauthorizedResponse);
			return;
			
		}
		
		Long headerUserId = Long.parseLong(headers.getFirst("userId"));
		
		if(pathParams.containsKey("userId")) {
			
			Long userId = Long.parseLong(pathParams.getFirst("userId"));
			
			if(requestContext.getMethod().equalsIgnoreCase("GET")) {
				
				if(!headerUserId.equals(userId)) {
					
					requestContext.abortWith(unauthorizedResponse);
					
				}
				
			}
			
		} else {
			
			if(!queryParams.containsKey("source") && !queryParams.containsKey("target")) {
				
				requestContext.abortWith(
						Response.status(Response.Status.BAD_REQUEST)
								.entity("source, and target parameters are required")
								.build()
				);
				return;
				
			}
			
			Long source, target;
			
			try {
				
				source = Long.parseLong(queryParams.getFirst("source"));
				target = Long.parseLong(queryParams.getFirst("target"));
				
			} catch(NumberFormatException ex) {
				
				requestContext.abortWith(
						Response.status(Response.Status.BAD_REQUEST)
								.entity("The source, and/or target parameter(s) isn't appropriate")
								.build()
				);
				return;
				
			}
			
			if(requestContext.getMethod().equalsIgnoreCase("POST")) {
				
				if(!headerUserId.equals(source)) {
					
					requestContext.abortWith(unauthorizedResponse);
					
				}
				
			} else if(
					requestContext.getMethod().equalsIgnoreCase("GET")
					|| requestContext.getMethod().equalsIgnoreCase("DELETE")
			) {
				
				if(!headerUserId.equals(source) && !headerUserId.equals(target)) {
					
					requestContext.abortWith(unauthorizedResponse);
					
				}
				
			} else if(requestContext.getMethod().equalsIgnoreCase("PUT")) {
				
				if(!headerUserId.equals(target)) {
					
					requestContext.abortWith(unauthorizedResponse);
					
				}
				
			}
			
		}
		
	}
	
}
