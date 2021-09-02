package com.ramsey.racebook.ejb;

import com.ramsey.racebook.entity.User;
import com.ramsey.racebook.exception.UserNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class UserBean {

	@PersistenceContext(name = "default")
	private EntityManager em;
	
	public void addUser(User user) {
		
		em.persist(user);
		
	}
	
	public User getUser(Long userId) throws UserNotFoundException {
		
		User user;
		
		try {
			
			user = em.find(User.class, userId);
			
		} catch(NoResultException ex) {
			
			throw new UserNotFoundException(userId);
			
		}
		
		if(user == null) throw new UserNotFoundException(userId);
		return user;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUsers(String username, Integer max) throws UserNotFoundException {
		
		var query = em.createQuery("SELECT u FROM User u WHERE u.username LIKE :username " +
						"OR u.firstName LIKE :username OR u.lastName LIKE :username ORDER BY u.id")
				.setParameter("username", String.format("%%%s%%", username));
		
		if(max > 0) {
			
			query = query.setMaxResults(max);
			
		}
		
		List<User> users = query.getResultList();
		
		if(users == null || users.size() == 0) {
			
			throw new UserNotFoundException(username);
			
		}
		
		return users;
		
	}
	
	public List<User> getUsers(Integer max) {
		
		try {
			
			return getUsers("", max);
			
		} catch(UserNotFoundException ex) {
			
			return null;
			
		}
		
	}
	
	public User updateUser(Long id, User user) throws UserNotFoundException {
		
		User oldUser = getUser(id);
		oldUser.copy(user);
		em.merge(oldUser);
		return oldUser;
		
	}
	
	public Boolean deleteUser(Long id) throws UserNotFoundException {
		
		User user = getUser(id);
		em.remove(user);
		
		try {
			
			getUser(id);
			
		} catch(UserNotFoundException ex) {
			
			return true;
			
		}
		
		return false;
		
	}
	
}
