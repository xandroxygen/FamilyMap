package com.moffatt.xander.familymap.model;

/**
 * User class is a Person, who also has a username,
 * password, and authorization token.
 * Created by Xander on 7/27/2016.
 */
public class User extends Person {
    private String username;
    private String password;
    private String authToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
