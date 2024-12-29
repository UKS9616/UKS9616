package com.coms309.notifServer;

import jakarta.websocket.Session;



public class UserSession {
    private Session session;
    private String user;

    public UserSession(Session session, String user){
        this.session = session;
        this.user = user;
    }

    // Getter for username
    public Session getSession() {
        return session;
    }

    // Setter for username
    public void setSession(Session session) {
        this.session = session;
    }

    // Getter for password
    public String getUser() {
        return user;
    }

    // Setter for password
    public void setUser(String user) {
        this.user = user;
    }
}
