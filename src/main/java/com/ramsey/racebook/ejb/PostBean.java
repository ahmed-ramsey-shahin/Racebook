package com.ramsey.racebook.ejb;

import com.ramsey.racebook.entity.Post;
import com.ramsey.racebook.entity.User;
import com.ramsey.racebook.exception.PostNotFoundException;
import com.ramsey.racebook.exception.UserNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Stateless
public class PostBean {

	@PersistenceContext(name = "default")
	private EntityManager em;
	@EJB private UserBean userBean;
	
	public void addPost(Long userId, @NotNull Post post) throws UserNotFoundException {
		
		User user = userBean.getUser(userId);
		post.setOwner(user);
		em.persist(post);
		user.getPosts().add(post);
		em.merge(post);
		
	}
	
	public Post getPost(Long postId) throws PostNotFoundException {
		
		Post post = em.find(Post.class, postId);
		
		if(post == null) {
			
			throw new PostNotFoundException(postId);
			
		}
		
		post.setOwnerId(post.getOwner().getId());
		return post;
		
	}
	
	public Post getPost(Long postId, Long userId) throws PostNotFoundException {
		
		Post post = getPost(postId);
		
		if(!post.getOwnerId().equals(userId)) {
			
			throw new PostNotFoundException(postId);
			
		}
		
		return post;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Post> getPosts(String content, Integer max, Long userId) throws PostNotFoundException, UserNotFoundException {
		
		Query query;
		
		if(userId == null) {
			
			query = em.createQuery("SELECT p FROM Post p WHERE p.content LIKE :postContent ORDER BY p.postDate");
			
		} else {
			
			userBean.getUser(userId);
			query = em.createQuery("SELECT p FROM User u, IN(u.posts) p WHERE u.id=:userId AND " +
					"p.content LIKE :postContent ORDER BY p.postDate")
					.setParameter("userId", userId);
			
		}
		
		query.setParameter("postContent", String.format("%%%s%%", content));
		
		if(max > 0) {
			
			query.setMaxResults(max);
			
		}
		
		List<Post> posts = query.getResultList();
		
		if(posts == null || posts.size() == 0) {
			
			throw new PostNotFoundException(content);
			
		}
		
		posts.forEach(post -> post.setOwnerId(post.getOwner().getId()));
		return posts;
		
	}
	
	public List<Post> getPosts(String content, Integer max) throws PostNotFoundException {
		
		try {
			
			return getPosts(content, max, null);
			
		} catch(UserNotFoundException ex) {
			
			return null;
			
		}
		
	}
	
	public List<Post> getUserPosts(Long userId, String content, Integer max) throws UserNotFoundException, PostNotFoundException {
		
		return getPosts(content, max, userId);
		
	}
	
	public Post updatePostContent(Long postId, Long userId, String content) throws PostNotFoundException {
		
		Post post = getPost(postId, userId);
		post.setContent(content);
		return post;
		
	}
	
	public Boolean deletePost(Long postId, Long userId) throws PostNotFoundException {
		
		Post post = getPost(postId, userId);
		User owner;
		
		try {
			
			owner = userBean.getUser(post.getOwner().getId());
			
		} catch(UserNotFoundException ex) {
			
			return false;
			
		}
		
		owner.getPosts().removeIf(p -> p.getId().equals(postId));
		em.merge(owner);
		em.remove(post);
		
		try {
			
			getPost(postId);
			
		} catch(PostNotFoundException ex) {
			
			return true;
			
		}
		
		return false;
		
	}
	
}
