package com.ramsey.racebook.entity;

import com.ramsey.racebook.entity.pk.FriendshipPK;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@IdClass(FriendshipPK.class)
@Entity
@Table(name = "FRIENDSHIPS")
public class Friendship implements Serializable {
	
	private static final long serialVersionUID = 1574578705420791121L;
	
	@Id
	private Long source;
	
	@Id
	private Long target;
	
	@Enumerated(EnumType.STRING)
	private FriendshipStatus friendshipStatus;
	
	@Temporal(TemporalType.DATE)
	@Column(updatable = false)
	private Date creationDate;
	
	{
		
		creationDate = new Date();
		
	}
	
	public Friendship(Long source, Long target, FriendshipStatus friendshipStatus) {
		
		this.source = source;
		this.target = target;
		this.friendshipStatus = friendshipStatus;
		
	}
	
	public Friendship() {
		
		friendshipStatus = FriendshipStatus.PENDING;
		
	}
	
	public Long getSource() {
		
		return source;
		
	}
	
	public void setSource(Long source) {
		
		this.source = source;
		
	}
	
	public Long getTarget() {
		
		return target;
		
	}
	
	public void setTarget(Long target) {
		
		this.target = target;
		
	}
	
	public FriendshipStatus getFriendshipStatus() {
		
		return friendshipStatus;
		
	}
	
	public void setFriendshipStatus(FriendshipStatus friendshipStatus) {
		
		this.friendshipStatus = friendshipStatus;
		
	}
	
	public Date getCreationDate() {
		
		return creationDate;
		
	}
	
	public void setCreationDate(Date creationDate) {
		
		this.creationDate = creationDate;
		
	}
	
}
