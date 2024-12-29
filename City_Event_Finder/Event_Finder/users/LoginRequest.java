package com.coms309.users;


/**
 *
 * @author Udip Shrestha
 *
 */



public class LoginRequest {
    private String loginUsername;
    private String loginPassword;

    LoginRequest(String username, String password){
        this.loginUsername = username;
        this.loginPassword = password;
    }

    // Getter for username
    public String getLoginUsername() {
        return loginUsername;
    }

    // Setter for username
    public void setLoginUsername(String username) {
        this.loginUsername = username;
    }

    // Getter for password
    public String getLoginPassword() {
        return loginPassword;
    }

    // Setter for password
    public void setLoginPassword(String password) {
        this.loginPassword = password;
    }
}
