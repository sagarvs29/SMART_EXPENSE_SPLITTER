package com.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class serves as the Data Access Object (DAO) for the User entity.
 */
public class UserRepository {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

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
                        System.out.printf("  [CREATE] Successfully registered user '%s' with ID: %d\n", user.getUsername(), userId);
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

    public List<User> listAllUsers(Connection conn) {
        String sql = "SELECT user_id, username, email, full_name, password_hash FROM public.users ORDER BY user_id";
        List<User> userList = new ArrayList<>();
        
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String fullName = resultSet.getString("full_name");
                String passwordHash = resultSet.getString("password_hash");

                User user = new User(username, email, passwordHash, fullName);
                userList.add(user);
            }
        } catch (SQLException e) {
             throw new DatabaseException("Failed to list all users.", e);
        }
        return userList; 
    }
    
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
}