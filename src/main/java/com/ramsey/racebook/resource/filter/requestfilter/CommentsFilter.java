package com.ramsey.racebook.resource.filter.requestfilter;

import com.ramsey.racebook.ejb.CommentBean;
import com.ramsey.racebook.entity.Comment;
import com.ramsey.racebook.exception.CommentNotFoundException;
import com.ramsey.racebook.resource.filter.Util;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsComment;
import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@FilteredAsComment
@Provider
@Priority(Priorities.AUTHORIZATION)
public class CommentsFilter implements ContainerRequestFilter {
	
	@EJB private Util util;
	@EJB private CommentBean commentBean;
	
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
		
		if(
				requestContext.getMethod().equalsIgnoreCase("POST")
				|| requestContext.getMethod().equalsIgnoreCase("GET")
		) {
			
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
			
			if(userId.equals(headerUserId)) {
				
				if(requestContext.getMethod().equalsIgnoreCase("DELETE")) {
					
					return;
					
				}
				
			}
			
			Long commentId = Long.parseLong(pathParams.getFirst("commentId"));
			Long postId = Long.parseLong(pathParams.getFirst("postId"));
			Comment comment;
			
			try {
				
				try {
					
					comment = commentBean.getComment(commentId, postId, userId);
					
				} catch(EJBException ex) {
					
					throw ex.getCausedByException();
					
				}
				
			} catch(CommentNotFoundException ex) {
				
				requestContext.abortWith(
						Response.status(Response.Status.NOT_FOUND)
								.entity("The resource you're looking for was not found")
								.build()
				);
				return;
				
			} catch(Exception ex) {
				
				requestContext.abortWith(
						Response.status(Response.Status.INTERNAL_SERVER_ERROR)
								.entity(ex.getMessage())
								.build()
				);
				return;
				
			}
			
			if(!comment.getOwnerId().equals(headerUserId)) {
				
				requestContext.abortWith(unauthorizedResponse);
				
			}
			
		}
		
	}
	
}
