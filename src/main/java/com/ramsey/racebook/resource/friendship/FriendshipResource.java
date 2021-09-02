package com.ramsey.racebook.resource.friendship;

import com.ramsey.racebook.ejb.FriendshipBean;
import com.ramsey.racebook.entity.Friendship;
import com.ramsey.racebook.entity.FriendshipStatus;
import com.ramsey.racebook.exception.FriendshipNotFoundException;
import com.ramsey.racebook.exception.UserNotFoundException;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsFriendship;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.transaction.RollbackException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

import static jakarta.ws.rs.core.MediaType.*;
import static jakarta.ws.rs.core.Response.Status.*;

@Path("friendships")
public class FriendshipResource {

	@EJB private FriendshipBean friendshipBean;
	
	@POST
	@FilteredAsFriendship
	public Response addFriendship(
			@Context UriInfo uriInfo,
			@QueryParam("source") Long sourceId,
			@QueryParam("target") Long targetId
	) {
		
		if(sourceId == null || targetId == null) {
			
			return Response.status(BAD_REQUEST)
					.entity("source, and target parameters are required")
					.build();
			
		}
		
		try {
			
			try {
				
				friendshipBean.addFriendshipBetween(sourceId, targetId);
				
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
		
		String serverLocation = uriInfo.getBaseUri().toString();
		return Response.noContent()
				.location(
						URI.create(serverLocation)
								.resolve(String.format("friendships/?source=%d&target=?%d", sourceId, targetId))
				).build();
		
	}
	
	@GET
	@Produces(APPLICATION_JSON)
	@FilteredAsFriendship
	public Response getFriendship(
			@QueryParam("source") Long sourceId,
			@QueryParam("target") Long targetId
	) {
		
		if(sourceId == null || targetId == null) {
			
			return Response.status(BAD_REQUEST)
					.entity("source, and target parameters are required")
					.build();
			
		}
		
		Friendship friendship;
		
		try {
			
			try {
				
				friendship = friendshipBean.getFriendship(sourceId, targetId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(UserNotFoundException | FriendshipNotFoundException ex) {
			
			return Response.status(NOT_FOUND)
					.entity("The resource you're looking for was not found")
					.build();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(friendship)
				.build();
		
	}
	
	@PUT
	@Produces(APPLICATION_JSON)
	@FilteredAsFriendship
	public Response updateFriendshipStatus(
			@Context UriInfo uriInfo,
			@QueryParam("source") Long sourceId,
			@QueryParam("target") Long targetId
	) {
		
		Response response = getFriendship(sourceId, targetId);
		
		if(response.getStatus() != 200) {
			
			return response;
			
		}
		
		Friendship friendship = (Friendship) response.getEntity();
		
		if(friendship.getFriendshipStatus().equals(FriendshipStatus.ACCEPTED)) {
			
			return Response.status(BAD_REQUEST)
					.entity("Source, and target are already friends")
					.build();
			
		}
		
		try {
			
			friendshipBean.setFriendshipStatus(sourceId, targetId, FriendshipStatus.ACCEPTED);
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		friendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);
		String serverLocation = uriInfo.getBaseUri().toString();
		return Response.ok()
				.entity(friendship)
				.location(
						URI.create(serverLocation)
								.resolve(String.format("friendships/?source=%d&target=?%d", sourceId, targetId))
				).build();
		
	}
	
	@DELETE
	@FilteredAsFriendship
	public Response deleteFriendship(
			@QueryParam("source") Long sourceId,
			@QueryParam("target") Long targetId
	) {
		
		Response response = getFriendship(sourceId, targetId);
		
		if(response.getStatus() != 200) {
			
			return response;
			
		}
		
		boolean deleted;
		
		try {
			
			try {
				
				deleted = friendshipBean.deleteFriendship(sourceId, targetId);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
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
			
		}
		
		return Response.noContent()
				.build();
		
	}
	
}
