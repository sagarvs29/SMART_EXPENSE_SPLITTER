package com.app;

/**
 * The Model Class (or Data Entity) for the 'users' table.
 * Now includes the database-generated userId for easier client mapping.
 */
public class User {

    private final int userId;        // -1 when not yet persisted
    private final String username;
    private final String email;
    private final String passwordHash;
    private final String fullName;

    /**
     * Full constructor used when the userId is known (retrieved from DB).
     */
    /**
     * Preferred constructor when id is available.
     */
    public User(int userId, String username, String email, String passwordHash, String fullName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    /**
     * Convenience constructor for new users before insertion (userId unknown).
     */
    /**
     * Backward-compatible constructor (no id known yet).
     */
    public User(String username, String email, String passwordHash, String fullName) {
        this.userId = -1;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
}