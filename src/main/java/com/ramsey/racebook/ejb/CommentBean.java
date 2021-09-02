package com.ramsey.racebook.ejb;

import com.ramsey.racebook.entity.Comment;
import com.ramsey.racebook.entity.Post;
import com.ramsey.racebook.entity.User;
import com.ramsey.racebook.exception.CommentNotFoundException;
import com.ramsey.racebook.exception.PostNotFoundException;
import com.ramsey.racebook.exception.UserNotFoundException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

@Stateless
public class CommentBean {

	@PersistenceContext
	private EntityManager em;
	@EJB private UserBean userBean;
	@EJB private PostBean postBean;
	
	public void addComment(
			Long postId,
			Long userId,
			Comment comment
	) throws UserNotFoundException, PostNotFoundException {
		
		User user = userBean.getUser(userId);
		Post post = postBean.getPost(postId);
		em.persist(comment);
		user.getComments().add(comment);
		post.getComments().add(comment);
		em.merge(user);
		em.merge(post);
		comment.setOwner(user);
		comment.setPost(post);
		em.merge(comment);
		
	}
	
	public Comment getComment(Long commentId, Long postId, Long userId) throws CommentNotFoundException {
		
		Comment comment = em.find(Comment.class, commentId);
		var exception = new CommentNotFoundException(commentId);
		
		if(comment == null) {
			
			throw exception;
			
		}
		
		comment.setOwnerId(comment.getOwner().getId());
		comment.setPostId(comment.getPost().getId());
		
		if(!comment.getPost().getOwner().getId().equals(userId) || !comment.getPostId().equals(postId)) {
			
			throw exception;
			
		}
		
		return comment;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Comment> getComments(String content, Integer max, Long postId) throws CommentNotFoundException {
		
		Query query;
		
		if(postId == null) {
			
			query = em.createQuery("SELECT c FROM Comment c WHERE c.content LIKE :commentContent ORDER BY c.commentDate");
			
		} else {
			
			query = em.createQuery("SELECT c FROM Post p, IN(p.comments) c WHERE p.id=:postId " +
					"AND c.content LIKE :commentContent ORDER BY c.commentDate")
					.setParameter("postId", postId);
			
		}
		
		query.setParameter("commentContent", String.format("%%%s%%", content));
		
		if(max > 0) {
			
			query.setMaxResults(max);
			
		}
		
		List<Comment> comments = query.getResultList();
		
		if(comments == null || comments.size() == 0) {
			
			throw new CommentNotFoundException(content);
			
		}
		
		comments.forEach(comment -> {
			
			comment.setOwnerId(comment.getOwner().getId());
			comment.setPostId(comment.getPost().getId());
			
		});
		
		return comments;
		
	}
	
	public Comment updateComment(Long commentId, Long postId, Long userId, String content) throws CommentNotFoundException {
		
		Comment comment = getComment(commentId, postId, userId);
		comment.setContent(content);
		em.merge(comment);
		return comment;
		
	}

	public Boolean deleteComment(Long commentId, Long postId, Long userId) throws CommentNotFoundException {
		
		Comment comment = getComment(commentId, postId, userId);
		Post post;
		User owner;
		
		try {
			
			post = postBean.getPost(comment.getPost().getId());
			owner = userBean.getUser(comment.getOwner().getId());
			
		} catch(PostNotFoundException | UserNotFoundException ex) {
			
			return false;
			
		}
		
		post.getComments().removeIf(c -> c.getId().equals(commentId));
		owner.getComments().removeIf(c -> c.getId().equals(commentId));
		em.merge(post);
		em.merge(owner);
		em.remove(comment);
		
		try {
			
			getComment(commentId, postId, userId);
			
		} catch(CommentNotFoundException ex) {
			
			return true;
			
		}
		
		return false;
		
	}
	
}
