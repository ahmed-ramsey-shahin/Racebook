package com.ramsey.racebook.entity;

import jakarta.json.bind.annotation.JsonbProperty;
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
@Table(name = "USERS")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1727272090609192287L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[\\d])[a-zA-Z\\d]{4,}$")
	@NotNull
	@NotEmpty
	@Column(unique = true)
	private String username;
	
	@Pattern(regexp = "^(?=[A-Z][a-z])[A-Za-z]{3,}$")
	@NotNull
	@NotEmpty
	private String firstName;
	
	@Pattern(regexp = "^(?=[A-Z][a-z])[A-Za-z]{3,}$")
	@NotNull
	@NotEmpty
	private String lastName;
	
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{8,}$")
	@NotNull
	@NotEmpty
	private String password;
	
	@Temporal(TemporalType.DATE)
	@Column(updatable = false)
	private Date joinDate;
	
	@OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "owner")
	@JsonbTransient
	private List<Post> posts;
	
	@OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "owner")
	@JsonbTransient
	private List<Comment> comments;
	
	@OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
	@JsonbTransient
	private List<Friendship> friendships;
	
	{
		
		joinDate = new Date();
		
	}
	
	public User(
			Long id,
			String username,
			String firstName,
			String lastName,
			String password,
			List<Post> posts,
			List<Comment> comments,
			List<Friendship> friendships
	) {
		
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.posts = posts;
		this.comments = comments;
		this.friendships = friendships;
		
	}
	
	public User() {
		
		posts = new ArrayList<>();
		comments = new ArrayList<>();
		friendships = new ArrayList<>();
		
	}
	
	public Long getId() {
		
		return id;
		
	}
	
	public void setId(Long id) {
		
		this.id = id;
		
	}
	
	public String getUsername() {
		
		return username;
		
	}
	
	public void setUsername(String username) {
		
		this.username = username;
		
	}
	
	public String getFirstName() {
		
		return firstName;
		
	}
	
	public void setFirstName(String firstName) {
		
		this.firstName = firstName;
		
	}
	
	public String getLastName() {
		
		return lastName;
		
	}
	
	public void setLastName(String lastName) {
		
		this.lastName = lastName;
		
	}
	
	@JsonbTransient
	public String getPassword() {
		
		return password;
		
	}
	
	@JsonbProperty
	public void setPassword(String password) {
		
		this.password = password;
		
	}
	
	public Date getJoinDate() {
		
		return joinDate;
		
	}
	
	public void setJoinDate(Date joinDate) {
		
		this.joinDate = joinDate;
		
	}
	
	public List<Post> getPosts() {
		
		return posts;
		
	}
	
	public void setPosts(List<Post> posts) {
		
		this.posts = posts;
		
	}
	
	public List<Comment> getComments() {
		
		return comments;
		
	}
	
	public void setComments(List<Comment> comments) {
		
		this.comments = comments;
		
	}
	
	public List<Friendship> getFriendships() {
		
		return friendships;
		
	}
	
	public void setFriendships(List<Friendship> friendships) {
		
		this.friendships = friendships;
		
	}
	
	public void copy(@org.jetbrains.annotations.NotNull User user) {
		
		setUsername(user.getUsername());
		setFirstName(user.getFirstName());
		setLastName(user.getLastName());
		setPassword(user.getPassword());
		
	}
	
}
