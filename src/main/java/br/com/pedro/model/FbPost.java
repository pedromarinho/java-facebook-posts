package br.com.pedro.model;

import java.sql.Date;

/**
 * Post Model Object
 * 
 * @author Pedro Marinho Medeiros
 *
 */
public class FbPost {
	private String id;
	private String message;
	private Date createdTime;

	
	public FbPost(String id, String message, Date createdTime) {
		this.id = id;
		this.message = message;
		this.createdTime = createdTime;
	}

	public FbPost() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public String toString() {
		return "id: " + this.getId() + "\nmessage: " + this.getMessage() + "\ncreated_time: " + this.getCreatedTime();
	}
}
