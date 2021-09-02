package com.ramsey.racebook.ejb;

import com.ramsey.racebook.entity.Friendship;
import com.ramsey.racebook.entity.FriendshipStatus;
import com.ramsey.racebook.entity.User;
import com.ramsey.racebook.exception.FriendshipNotFoundException;
import com.ramsey.racebook.exception.UserNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class FriendshipBean {
	
	@PersistenceContext private EntityManager em;
	@EJB private UserBean userBean;
	
	public void addFriendshipBetween(Long sourceId, Long targetId) throws UserNotFoundException {
		
		User source = userBean.getUser(sourceId);
		User target = userBean.getUser(targetId);
		Friendship friendship = new Friendship(sourceId, targetId, FriendshipStatus.PENDING);
		source.getFriendships().add(friendship);
		target.getFriendships().add(friendship);
		em.persist(friendship);
		em.merge(source);
		em.merge(target);
		
	}
	
	public Friendship getFriendship(Long source, Long target) throws UserNotFoundException, FriendshipNotFoundException {
		
		userBean.getUser(source);
		userBean.getUser(target);
		Friendship friendship;
		
		try {
			
			friendship = (Friendship) em.createQuery("SELECT f FROM Friendship f WHERE " +
							"(f.source=:sourceId AND f.target=:targetId) OR (f.source=:targetId AND f.target=:sourceId)")
					.setParameter("sourceId", source)
					.setParameter("targetId", target)
					.getSingleResult();
			
		} catch(NoResultException ex) {
			
			throw new FriendshipNotFoundException(source, target);
			
		}
		
		if(friendship == null) {
			
			throw new FriendshipNotFoundException(source, target);
			
		}
		
		return friendship;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Friendship> getFriendships(Long userId) throws UserNotFoundException, FriendshipNotFoundException {
		
		userBean.getUser(userId);
		List<Friendship> friendships = (List<Friendship>) em.createQuery("SELECT f FROM Friendship f WHERE " +
				"f.target=:userId OR f.source=:userId")
				.setParameter("userId", userId)
				.getResultList();
		
		if(friendships == null || friendships.size() == 0) {
			
			throw new FriendshipNotFoundException(userId);
			
		}
		
		return friendships;
		
	}
	
	public void setFriendshipStatus(
			Long source,
			Long target,
			FriendshipStatus status
	) throws UserNotFoundException, FriendshipNotFoundException {
		
		Friendship friendship = getFriendship(source, target);
		friendship.setFriendshipStatus(status);
		em.merge(friendship);
		
	}
	
	public Boolean deleteFriendship(Long sourceId, Long targetId) throws UserNotFoundException, FriendshipNotFoundException {
		
		Friendship friendship = getFriendship(sourceId, targetId);
		User source = userBean.getUser(sourceId);
		User target = userBean.getUser(targetId);
		source.getFriendships().removeIf(f -> (
				(f.getSource().equals(sourceId) && f.getTarget().equals(targetId))
				|| (f.getSource().equals(targetId) && f.getTarget().equals(sourceId))
		));
		target.getFriendships().removeIf(f -> (
				(f.getSource().equals(sourceId) && f.getTarget().equals(targetId))
						|| (f.getSource().equals(targetId) && f.getTarget().equals(sourceId))
		));
		em.merge(source);
		em.merge(target);
		em.remove(friendship);
		
		try {
			
			getFriendship(sourceId, targetId);
			
		} catch(FriendshipNotFoundException ex) {
			
			return true;
			
		}
		
		return false;
		
	}
	
}
