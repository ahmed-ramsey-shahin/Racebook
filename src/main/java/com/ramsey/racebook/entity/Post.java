package com.ramsey.racebook.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "POSTS")
public class Post implements Serializable {
	
	private static final long serialVersionUID = 6757832336690659175L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@NotEmpty
	@Pattern(regexp = "^(?!.*[<>]).*$")
	private String content;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date postDate;
	
	@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "post")
	@JsonbTransient
	private List<Comment> comments;
	
	@ManyToOne
	@JsonbTransient
	@JoinColumn(updatable = false)
	private User owner;
	
	@Transient
	private Long ownerId;
	
	{
		
		postDate = new Date();
		
	}
	
	public Post(
			Long id,
			String content,
			Date postDate,
			List<Comment> comments,
			User owner
	) {
		
		this.id = id;
		this.content = content;
		this.postDate = postDate;
		this.comments = comments;
		this.owner = owner;
		
	}
	
	public Post() {
		
		comments = new ArrayList<>();
		
	}
	
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
	
	public Date getPostDate() {
		
		return postDate;
		
	}
	
	public void setPostDate(Date postDate) {
		
		this.postDate = postDate;
		
	}
	
	public List<Comment> getComments() {
		
		return comments;
		
	}
	
	public void setComments(List<Comment> comments) {
		
		this.comments = comments;
		
	}
	
	public User getOwner() {
		
		return owner;
		
	}
	
	public void setOwner(User owner) {
		
		this.owner = owner;
		
	}
	
	public Long getOwnerId() {
		
		return ownerId;
		
	}
	
	public void setOwnerId(Long ownerId) {
		
		this.ownerId = ownerId;
		
	}
	
}
