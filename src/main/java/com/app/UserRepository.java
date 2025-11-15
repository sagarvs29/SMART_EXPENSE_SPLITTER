package com.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class serves as the Data Access Object (DAO) for user and group entities.
 */
public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    /**
     * Inserts a new user record into the 'users' table.
     */
    public void registerNewUser(Connection conn, User user) {
        String sql = "INSERT INTO public.users (username, email, password_hash, full_name) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getFullName());

            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        System.out.printf("  [CREATE] Successfully registered user '%s' with ID: %d.\n", user.getUsername(), userId);
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { 
                 System.err.printf("  [ERROR] User registration failed: Username or email '%s' already exists.\n", user.getUsername());
            } else {
                 throw new DatabaseException("Failed to register user: " + user.getUsername(), e);
            }
        }
    }

    /**
     * Retrieves all user records.
     */
    public List<User> listAllUsers(Connection conn) {
    String sql = "SELECT user_id, username, email, full_name, password_hash FROM public.users ORDER BY user_id";
        List<User> userList = new ArrayList<>();
        
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String fullName = resultSet.getString("full_name");
                String passwordHash = resultSet.getString("password_hash");

                User user = new User(userId, username, email, passwordHash, fullName);
                userList.add(user);
            }
        } catch (SQLException e) {
             throw new DatabaseException("Failed to list all users.", e);
        }
        return userList;
    }
    
    /**
     * Updates an existing user record.
     */
    public void updateUser(Connection conn, User user) {
        String sql = "UPDATE public.users SET email = ?, full_name = ? WHERE username = ?";
        
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getUsername());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.printf("  [UPDATE] Successfully updated user details for '%s'.\n", user.getUsername());
            } else {
                System.err.printf("  [WARNING] Could not find user to update: '%s'.\n", user.getUsername());
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user: " + user.getUsername(), e);
        }
    }
    
    /**
     * Deletes a user record.
     */
    public void deleteUser(Connection conn, String username) {
        String sql = "DELETE FROM public.users WHERE username = ?";
        
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, username);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.printf("  [DELETE] Successfully deleted user: '%s'.\n", username);
            } else {
                System.err.printf("  [WARNING] Could not find user to delete: '%s'.\n", username);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user: " + username, e);
        }
    }
    
    /**
     * Creates a new group and returns its generated ID.
     * This is required for the settlements foreign key constraint.
     * @param conn The active database connection.
     * @param groupName The name of the group.
     * @param creatorId The user ID of the group creator.
     * @return The generated group_id.
     */
    public long createGroup(Connection conn, String groupName, int creatorId) {
        String sql = "INSERT INTO public.groups (group_name, creator_id) VALUES (?, ?)";
        long groupId = -1;
        
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, groupName);
            statement.setInt(2, creatorId);

            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        groupId = rs.getLong(1);
                        System.out.printf("  [CREATE] Successfully created group '%s' with ID: %d.\n", groupName, groupId);
                    }
                }
            }
        } catch (SQLException e) {
             throw new DatabaseException("Failed to create group: " + groupName, e);
        }
        return groupId;
    }
}