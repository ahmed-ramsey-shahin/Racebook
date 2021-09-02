package com.ramsey.racebook.resource.filter.responsefilter;

import com.ramsey.racebook.entity.Post;
import com.ramsey.racebook.resource.filter.Util;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsPostResponse;
import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.List;

@FilteredAsPostResponse
@Provider
@Priority(Priorities.AUTHORIZATION)
public class PostFilter implements ContainerResponseFilter {
	
	@EJB private Util util;
	
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
		
		if(requestContext.getMethod().equalsIgnoreCase("GET")) {
			
			List<Post> posts = (List<Post>) responseContext.getEntity();
			posts.removeIf(post -> {
				
				if(headerUserId.equals(post.getOwnerId())) {
					
					return false;
					
				}
				
				return util.areTheyFriend(post.getOwnerId(), headerUserId);
				
			});
			
		}
		
	}
	
}
