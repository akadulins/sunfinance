package com.sunfinance.common.model;



public class Subject {
	private String identity;
	private SubjectType type;
	
	public Subject() {}
	
	public Subject(String identity, SubjectType type) {
        this.identity = identity;
        this.type = type;
        validate();
    }

    private void validate() {
        switch (type) {
            case EMAIL_CONFIRMATION :
                if (!identity.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    throw new IllegalArgumentException("Invalid email: " + identity);
                }
                break;
            
            case MOBILE_CONFIRMATION :
                if (!identity.matches("^\\+\\d{7,15}$")) {
                    throw new IllegalArgumentException("Invalid mobile number: " + identity);
                }
                break;
        }
    }

    public String getIdentity() {
        return identity;
    }

    public SubjectType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{ \"identity\": \"" + identity + "\", \"type\": \"" + type.getType() + "\" }";
    }
}
