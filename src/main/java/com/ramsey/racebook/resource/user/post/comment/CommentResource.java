package com.ramsey.racebook.resource.user.post.comment;

import com.ramsey.racebook.ejb.CommentBean;
import com.ramsey.racebook.entity.Comment;
import com.ramsey.racebook.exception.CommentNotFoundException;
import com.ramsey.racebook.exception.PostNotFoundException;
import com.ramsey.racebook.exception.UserNotFoundException;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsComment;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.RollbackException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;
import static jakarta.ws.rs.core.Response.Status.*;

@RequestScoped
public class CommentResource {

	@EJB private CommentBean commentBean;
	@PathParam("postId") private Long postId;
	@PathParam("userId") private Long userId;
	
	@POST
	@Consumes(APPLICATION_JSON)
	@FilteredAsComment
	public Response addComment(
			@Context UriInfo uriInfo,
			@HeaderParam("userId") Long headerUserId,
			Comment comment
	) {
		
		try {
			
			try {
				
				commentBean.addComment(postId, headerUserId, comment);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(UserNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The user you're looking for was not found")
					.build();
			
		} catch(PostNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The post you're looking for was not found")
					.build();
			
		} catch(ConstraintViolationException ex) {
			
			return Response.status(BAD_REQUEST)
					.entity("The request have broke a violation please check for any possible violations")
					.build();
			
		} catch(RollbackException ex) {
			
			return Response.status(BAD_REQUEST)
					.entity("Resource was not added, because of possible constraint violation")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		String serverLocation = uriInfo.getBaseUri().toString();
		return Response.noContent()
				.location(
						URI.create(serverLocation).resolve(
								String.format("users/%d/posts/%d/comments/%d", userId, postId, comment.getId())
						)
				).build();
		
	}
	
	@GET
	@Produces(APPLICATION_JSON)
	@FilteredAsComment
	public Response getComments(
			@DefaultValue("0") @QueryParam("max") Integer max,
			@DefaultValue("") @QueryParam("content") String content
	) {
		
		if(max < 0) {
			
			return Response.status(BAD_REQUEST)
					.entity("Max parameter should be greater than 0")
					.build();
			
		}
		
		List<Comment> comments;
		
		try {
			
			try {
				
				comments = commentBean.getComments(content, max, postId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(PostNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The post you're looking for was not found")
					.build();
			
		} catch(CommentNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The comment you're looking for was not found")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(new GenericEntity<>(comments){})
				.build();
		
	}
	
	@GET
	@Path("{commentId:[0-9]*}")
	@Produces(APPLICATION_JSON)
	@FilteredAsComment
	public Response getComment(@PathParam("commentId") Long commentId) {
		
		Comment comment;
		
		try {
			
			try {
				
				comment = commentBean.getComment(commentId, postId, userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(CommentNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The comment you're looking for was not found")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(comment)
				.build();
		
	}
	
	@PUT
	@Path("{commentId:[0-9]*}")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	@FilteredAsComment
	public Response updateComment(
			@Context UriInfo uriInfo,
			@PathParam("commentId") Long commentId,
			Comment comment
	) {
		
		try {
			
			try {
				
				comment = commentBean.updateComment(commentId, postId, userId, comment.getContent());
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(CommentNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
					.build();
			
		} catch(ConstraintViolationException ex) {
			
			return Response.status(BAD_REQUEST)
					.entity("The request have broke a violation please check for any possible violations")
					.build();
			
		} catch(RollbackException ex) {
			
			return Response.notModified()
					.entity("Resource was not added, because of possible constraint violation")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		String serverLocation = uriInfo.getBaseUri().toString();
		return Response.ok()
				.entity(comment)
				.location(
						URI.create(serverLocation).resolve(String.format("comments/%d", comment.getId()))
				).build();
		
	}
	
	@DELETE
	@Path("{commentId:[0-9]*}")
	@FilteredAsComment
	public Response deleteComment(@PathParam("commentId") Long commentId) {
		
		boolean deleted;
		
		try {
			
			try {
				
				deleted = commentBean.deleteComment(commentId, postId, userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(CommentNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
					.build();
			
		} catch(RollbackException ex) {
			
			return Response.notModified()
					.entity("The resource was not deleted because of a transaction exception")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		if(!deleted) {
			
			return Response.notModified()
					.entity("The resource was not modified because of an unknown reason")
					.build();
			
		} else {
			
			return Response.noContent().build();
			
		}
		
	}
	
}
