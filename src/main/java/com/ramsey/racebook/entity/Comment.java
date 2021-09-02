package com.ramsey.racebook.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "COMMENTS")
public class Comment implements Serializable {
	
	private static final long serialVersionUID = 8235405236903084221L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotEmpty
	@NotNull
	@Pattern(regexp = "^(?!.*[<>]).*$")
	private String content;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date commentDate;
	
	@ManyToOne
	@JsonbTransient
	@JoinColumn(updatable = false)
	private User owner;
	
	@ManyToOne
	@JsonbTransient
	@JoinColumn(updatable = false)
	private Post post;
	
	@Transient
	private Long postId;
	
	@Transient
	private Long ownerId;
	
	{
		
		commentDate = new Date();
		
	}
	
	public Comment(
			Long id,
			String content,
			Date commentDate,
			User owner,
			Post post
	) {
		
		this.id = id;
		this.content = content;
		this.commentDate = commentDate;
		this.owner = owner;
		this.post = post;
		
	}
	
	public Comment() {  }
	
	public Long getId() {
		
		return id;
		
	}
	
	public void setId(Long id) {
		
		this.id = id;
		
	}
	
	public String getContent() {
		
		return content;
		
	}
	
	public void setContent(String content) {
		
		this.content = content;
		
	}
	
	public Date getCommentDate() {
		
		return commentDate;
		
	}
	
	public void setCommentDate(Date commentDate) {
		
		this.commentDate = commentDate;
		
	}
	
	public User getOwner() {
		
		return owner;
		
	}
	
	public void setOwner(User owner) {
		
		this.owner = owner;
		
	}
	
	public Post getPost() {
		
		return post;
		
	}
	
	public void setPost(Post post) {
		
		this.post = post;
		
	}
	
	public Long getPostId() {
		
		return postId;
		
	}
	
	public void setPostId(Long postId) {
		
		this.postId = postId;
		
	}
	
	public Long getOwnerId() {
		
		return ownerId;
		
	}
	
	public void setOwnerId(Long ownerId) {
		
		this.ownerId = ownerId;
		
	}
	
}
