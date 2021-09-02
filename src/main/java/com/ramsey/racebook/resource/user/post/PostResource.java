package com.ramsey.racebook.resource.user.post;

import com.ramsey.racebook.ejb.PostBean;
import com.ramsey.racebook.entity.Post;
import com.ramsey.racebook.exception.PostNotFoundException;
import com.ramsey.racebook.exception.UserNotFoundException;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsPost;
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
import java.util.ArrayList;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;

@RequestScoped
public class PostResource {
	
	@EJB private PostBean postBean;
	@PathParam("userId") private Long userId;
	
	@POST
	@Consumes(APPLICATION_JSON)
	@FilteredAsPost
	public Response addPost(
			@Context UriInfo uriInfo,
			Post post
	) {
		
		try {
			
			try {
				
				postBean.addPost(userId, post);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(UserNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
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
		return Response.status(NO_CONTENT)
				.location(
						URI.create(serverLocation).resolve(
								String.format("users/%d/posts/%d", userId, post.getId())
						)
				).build();
		
	}
	
	@GET
	@Path("{postId:[0-9]*}")
	@Produces(APPLICATION_JSON)
	@FilteredAsPost
	public Response getPost(
			@PathParam("postId") Long postId
	) {
		
		Post post;
		
		try {
			
			try {
				
				post = postBean.getPost(postId, userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(PostNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(post)
				.build();
		
	}
	
	@GET
	@Produces(APPLICATION_JSON)
	@FilteredAsPost
	public Response getPosts(
			@DefaultValue("0") @QueryParam("max") Integer max,
			@DefaultValue("") @QueryParam("content") String content
	) {
		
		if(max < 0) {
			
			return Response.status(BAD_REQUEST)
					.entity("Max parameter should be greater than 0")
					.build();
			
		}
		
		List<Post> posts;
		
		try {
			
			try {
				
				posts = postBean.getUserPosts(userId, content, max);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(UserNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The user you're looking for was not found")
					.build();
			
		} catch(PostNotFoundException ex) {
			
			if(content.trim().isEmpty()) {
				
				return Response.ok().entity(new ArrayList<>()).build();
				
			} else {
				
				return Response.status(NOT_FOUND)
						.entity("The post you're looking for was not found")
						.build();
				
			}
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(new GenericEntity<>(posts){})
				.build();
		
	}
	
	@PUT
	@Path("{postId:[0-9]*}")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	@FilteredAsPost
	public Response updatePost(
			@Context UriInfo uriInfo,
			@PathParam("postId") Long postId,
			Post post
	) {
		
		try {
			
			try {
				
				post = postBean.updatePostContent(postId, userId, post.getContent());
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(PostNotFoundException ex) {
			
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
				.entity(post)
				.location(
						URI.create(serverLocation).resolve(
								String.format("users/%d/posts/%d", userId, post.getId())
						)
				).build();
		
	}
	
	@DELETE
	@Path("{postId:[0-9]*}")
	@FilteredAsPost
	public Response deletePost(@PathParam("postId") Long postId) {
		
		boolean deleted;
		
		try {
			
			try {
				
				deleted = postBean.deletePost(postId, userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(PostNotFoundException ex) {
			
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
