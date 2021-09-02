package com.ramsey.racebook.entity.pk;

import java.io.Serializable;

public class FriendshipPK implements Serializable {
	
	private static final long serialVersionUID = 4882651191613890822L;
	private Long source;
	private Long target;
	
	public FriendshipPK(Long source, Long target) {
		
		this.source = source;
		this.target = target;
		
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		FriendshipPK that = (FriendshipPK) o;
		
		if(!source.equals(that.source)) return false;
		return target.equals(that.target);
		
	}
	
	@Override
	public int hashCode() {
		
		int result = source.hashCode();
		result = 31 * result + target.hashCode();
		return result;
		
	}
	
}
