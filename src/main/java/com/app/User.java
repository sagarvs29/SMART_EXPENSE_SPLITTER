package com.app;

/**
 * The Model Class (or Data Entity) for the 'users' table.
 */
public class User {
    
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String fullName;

    public User(String username, String email, String passwordHash, String fullName) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
}