package com.ramsey.racebook.resource.filter.requestfilter;

import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsUser;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@FilteredAsUser
@Provider
@Priority(Priorities.AUTHORIZATION)
public class UsersFilter implements ContainerRequestFilter {
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		var pathParams = requestContext.getUriInfo().getPathParameters();
		var header = requestContext.getHeaders();
		Long userId = Long.parseLong(pathParams.getFirst("userId"));
		var unauthorizedResponse = Response.status(Response.Status.UNAUTHORIZED)
				.entity("User cannot access the resource")
				.build();
		
		if(!header.containsKey("userId")) {
			
			requestContext.abortWith(unauthorizedResponse);
			return;
			
		}
		
		Long headerUserId = Long.parseLong(header.getFirst("userId"));
		
		if(!userId.equals(headerUserId)) {
			
			requestContext.abortWith(unauthorizedResponse);
			
		}
		
	}
	
}
