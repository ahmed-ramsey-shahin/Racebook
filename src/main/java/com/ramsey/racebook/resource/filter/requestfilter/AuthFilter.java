package com.ramsey.racebook.resource.filter.requestfilter;

import com.ramsey.racebook.Util.HMACGenerator;
import com.ramsey.racebook.ejb.UserBean;
import com.ramsey.racebook.entity.User;
import com.ramsey.racebook.exception.UserNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.concurrent.TimeUnit;

//@Provider
//@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {
	
	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "HMAC";
	@EJB private UserBean userBean;
	
	private static Date getDateFromString(String str) {
		
		var DTL = str.split("-");
		var DMY = DTL[0].split("/");
		var HMS = DTL[1].split(":");

		int day = Integer.parseInt(DMY[0]);
		int month = Integer.parseInt(DMY[1]);
		int year = Integer.parseInt(DMY[2]);

		int hour = Integer.parseInt(HMS[0]);
		int minute = Integer.parseInt(HMS[1]);
		int second = Integer.parseInt(HMS[2]);

		String timezone = DTL[2];
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTime();
		
	}
	
	private static long differenceBetweenTwoDatesInMinutes(Date date1, Date date2) {
		
		long diffInMils = date1.getTime() - date2.getTime();
		return TimeUnit.MINUTES.convert(diffInMils, TimeUnit.MILLISECONDS);
	
	}
	
	@Override
	public void filter(ContainerRequestContext request) {
		
		if(request.getUriInfo().getPath().endsWith("users/") && request.getMethod().equalsIgnoreCase("post")) {
			
			return;
			
		}
		
		List<String> authHeader = request.getHeaders().get(AUTHORIZATION_HEADER_KEY);

		if(authHeader != null && authHeader.size() > 0) {
			
			String authToken = authHeader.get(0);
			authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
			
			User user;
			long userId;
			long nonce;
			String password;
			String username = request.getHeaderString("username");
			String dateStr;
			Date date;
			long diff;
			
			try {
				
				dateStr = request.getHeaderString("date");
				date = getDateFromString(dateStr);
				
			} catch(Exception ex) {
				
				Response error = Response.status(Response.Status.BAD_REQUEST)
						.entity("Invalid Date")
						.build();
				request.abortWith(error);
				return;
				
			}
			
			try {
				
				userId = Long.parseLong(request.getHeaderString("userId"));
				nonce = Long.parseLong(request.getHeaderString("nonce"));
				
			} catch(NumberFormatException ex) {
				
				Response error = Response.status(Response.Status.BAD_REQUEST)
						.entity("The userId, or nonce isn't appropriate")
						.build();
				request.abortWith(error);
				return;
				
			}
			
			
			try {
				
				user = userBean.getUser(userId);
				password = user.getPassword();
				
			} catch(UserNotFoundException ex) {
				
				Response error = Response.status(Response.Status.BAD_REQUEST)
						.entity("The user isn't available")
						.build();
				request.abortWith(error);
				return;
				
			} catch(Exception ex) {
				
				Response error = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(String.format("Couldn't get the user for unknown reason, the error message is: %s", ex.getMessage()))
						.build();
				request.abortWith(error);
				return;
				
			}
			
			if(!user.getUsername().equals(request.getHeaderString("username"))) {
				
				Response error = Response.status(Response.Status.BAD_REQUEST)
						.entity("Invalid credentials")
						.build();
				request.abortWith(error);
				return;
				
			}
			
			diff = differenceBetweenTwoDatesInMinutes(new Date(), date);
			
			if(0 > diff || diff > 2) {
				
				Response error = Response.status(Response.Status.BAD_REQUEST)
						.entity("The request couldn't be processed, date isn't appropriate")
						.build();
				request.abortWith(error);
				return;
				
			}
			
			String message = username +
					dateStr +
					nonce +
					HMACGenerator.API_SECRET_CODE +
					password;
			String generatedHmac = HMACGenerator.generateHMAC(user.getPassword(), message);
			
			if(authToken.equals(generatedHmac)) {
			
				return;
			
			}
			
		}
		
		Response unauthorizedStatus = Response.status(Response.Status.UNAUTHORIZED)
				.entity("User cannot access the resource")
				.build();
		request.abortWith(unauthorizedStatus);
		
	}
	
}
