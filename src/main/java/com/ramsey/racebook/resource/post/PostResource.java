package com.ramsey.racebook.resource.post;

import com.ramsey.racebook.ejb.PostBean;
import com.ramsey.racebook.entity.Post;
import com.ramsey.racebook.exception.PostNotFoundException;
import com.ramsey.racebook.resource.filter.filtertypes.FilteredAsPostResponse;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;
import static jakarta.ws.rs.core.Response.Status.*;

@Path("posts")
public class PostResource {

	@EJB private PostBean postBean;
	
	@GET
	@Produces(APPLICATION_JSON)
	@FilteredAsPostResponse
	public Response getPosts(
			@DefaultValue("0") @QueryParam("max") Integer max,
			@DefaultValue("") @QueryParam("content") String content
	) {
		
		if(max < 0)
			return Response.status(BAD_REQUEST)
					.entity("Max parameter should be greater than 0")
					.build();
		
		List<Post> posts;
		
		try {
			
			try {
				
				posts = postBean.getPosts(content, max);
				
			} catch(EJBException ex) {
				
				throw ex.getCausedByException();
				
			}
			
		} catch(PostNotFoundException ex) {
			
			posts = new ArrayList<>();
			
		} catch(Exception ex) {
			
			return Response.status(INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage())
					.build();
			
		}
		
		return Response.ok()
				.entity(new GenericEntity<>(posts){})
				.build();
		
	}
	
}
