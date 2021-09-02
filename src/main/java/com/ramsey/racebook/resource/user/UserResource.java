package com.ramsey.racebook.resource.user;

import com.ramsey.racebook.ejb.UserBean;
import com.ramsey.racebook.entity.User;
import com.ramsey.racebook.exception.UserNotFoundException;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsUser;
import com.ramsey.racebook.resource.user.friendship.FriendshipResource;
import com.ramsey.racebook.resource.user.post.PostResource;
import com.ramsey.racebook.resource.user.post.comment.CommentResource;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.transaction.RollbackException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import static jakarta.ws.rs.core.MediaType.*;
import static jakarta.ws.rs.core.Response.Status.*;

import java.net.URI;
import java.util.List;

@Path("users")
public class UserResource {
	
	@EJB private UserBean userBean;
	@Inject private PostResource postResource;
	@Inject private CommentResource commentResource;
	@Inject private FriendshipResource friendshipResource;
	
	@POST
	@Consumes(APPLICATION_JSON)
	public Response addUser(
			@Context UriInfo uriInfo,
			User user
	) {
		
		try {
			
			try {
				
				userBean.addUser(user);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
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
						URI.create(serverLocation).resolve(String.format("users/%d", user.getId()))
				)
				.build();
		
	}
	
	@GET
	@Produces(APPLICATION_JSON)
	public Response getUsers(
			@DefaultValue("0") @QueryParam("max") Integer max
	) {
		
		List<User> users;
		
		try {
			
			users = userBean.getUsers(max);
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(new GenericEntity<>(users){})
				.build();
		
	}
	
	@GET
	@Path("{id:[0-9]*}")
	@Produces(APPLICATION_JSON)
	public Response getUser(
			@PathParam("id") Long userId
	) {
		
		User user;
		
		try {
			
			try {
				
				user = userBean.getUser(userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(UserNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(user)
				.build();
		
	}
	
	@GET
	@Path("{username:(?=.*[a-z])[a-zA-Z\\d]{4,}}")
	@Produces(APPLICATION_JSON)
	public Response getUsers(
			@DefaultValue("0") @QueryParam("max") Integer max,
			@PathParam("username") String username
	) {
		
		List<User> users;
		
		try {
			
			users = userBean.getUsers(username, max);
			
		} catch(UserNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(new GenericEntity<>(users){})
				.build();
		
	}
	
	@PUT
	@Path("{userId:[0-9]*}")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	@FilteredAsUser
	public Response updateUser(
			@Context UriInfo uriInfo,
			@PathParam("userId") Long userId,
			User user
	) {
		
		try {
			
			try {
				
				user = userBean.updateUser(userId, user);
				
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
				.entity(user)
				.location(
						URI.create(serverLocation).resolve(String.format("users/%d", user.getId()))
				).build();
		
	}
	
	@DELETE
	@Path("{userId:[0-9]*}")
	@FilteredAsUser
	public Response deleteUser(
			@PathParam("userId") Long userId
	) {
		
		boolean deleted;
		
		try {
			
			try {
				
				deleted = userBean.deleteUser(userId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(UserNotFoundException ex) {
			
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
		
		if(deleted) {
			
			return Response.noContent()
					.build();
			
		}

		return Response.notModified()
				.entity("The resource was not modified because of an unknown reason")
				.build();
		
	}
	
	@Path("{userId:[0-9]*}/posts")
	public PostResource getPostResource() {
		
		return postResource;
		
	}

	@Path("{userId:[0-9]*}/posts/{postId:[0-9]*}/comments")
	public CommentResource getCommentResource() {
		
		return commentResource;
		
	}
	
	@Path("{userId:[0-9]*}/friendships")
	public FriendshipResource getFriendshipResource() {
		
		return friendshipResource;
		
	}
	
}
