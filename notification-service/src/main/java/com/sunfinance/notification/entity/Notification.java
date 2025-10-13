package com.sunfinance.notification.entity;

import java.util.UUID;

import jakarta.persistence.*;


@Entity
public class Notification {
	@Id
	@GeneratedValue
	private UUID id;
   
    private String recipient;
    
	private String channel;   // email or sms
	
    private String body;
   
	private boolean dispatched = false;

    protected Notification() {}

    public Notification(String recipient, String channel, String body) {
        this.id = UUID.randomUUID();
        this.recipient = recipient;
        this.channel = channel;
        this.body = body;
    }

    public void markDispatched() {
        this.dispatched = true;
    }
    
    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
