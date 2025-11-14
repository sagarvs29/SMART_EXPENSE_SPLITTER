package com.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for the SMART EXPENSE SPLITTER.
 * App.java is now purely the orchestrator and entry point.
 */
public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        System.out.println("\n--- Starting Expense Splitter Application ---");

        UserRepository userRepository = new UserRepository();
        
        try (Connection connection = DatabaseConnector.getConnection()) {
            System.out.println("🎉 Connection successful! Connected to Expense Splitter DB.");
            
            // --- 1. CREATE Operation: Register two users ---
            System.out.println("\n--- 1. CREATE USERS ---");
            User alice = new User("alice_j", "alice.j@example.com", "hash1", "Alice Johnson");
            User bob = new User("bob_s", "bob.s@example.com", "hash2", "Bob Smith");
            
            userRepository.registerNewUser(connection, alice);
            userRepository.registerNewUser(connection, bob);

            // --- 2. READ Operation: List all users ---
            listAndPrintUsers(connection, userRepository, "Initial User List");

            // --- 3. UPDATE Operation: Update Alice's email ---
            System.out.println("\n--- 3. UPDATE USER ---");
            User aliceUpdated = new User("alice_j", "alice.johnson.new@corp.com", "hash1", "Alice M. Johnson");
            userRepository.updateUser(connection, aliceUpdated);
            
            // Re-read to confirm update
            listAndPrintUsers(connection, userRepository, "After Alice Update");
            
            // --- 4. DELETE Operation: Remove Bob ---
            System.out.println("\n--- 4. DELETE USER ---");
            userRepository.deleteUser(connection, "bob_s");

            // Final read to confirm delete
            listAndPrintUsers(connection, userRepository, "After Bob Deletion");
            
            // --- 5. UTILITY MODULE DEMO (New Split Feature) ---
            System.out.println("\n--- 5. UTILITY DEMO (New Split Feature) ---");
            
            // MathOperations Demo: Calculate expenses
            MathOperations calculator = new MathOperations();
            int expense1 = 150;
            int expense2 = 80;
            int total = calculator.add(expense1, expense2);
            System.out.printf("Total expense for Alice and Bob: %d + %d = %d\n", expense1, expense2, total);
            
            // Split Calculation Demo
            double dinnerTotal = 55.75;
            int partySize = 3;
            // Correctly calling the static method
            double splitAmount = MathOperations.calculateSplit(dinnerTotal, partySize); 
            System.out.printf("Splitting $%.2f among %d people. Each person owes: $%.2f\n", 
                              dinnerTotal, partySize, splitAmount);
            
            // StringUtility Demo: Reverse a status message
            String originalStatus = "App is running smoothly";
            String reversedStatus = StringUtility.reverseString(originalStatus);
            System.out.printf("Original Status: '%s'\nReversed Status: '%s'\n", originalStatus, reversedStatus);

        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "❌ APPLICATION ERROR: Database operation failed.", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ FAILED TO CONNECT TO DATABASE.", e);
            if (e.getSQLState().equals("28P01")) {
                 System.err.println("\nHint: Check your DB_HOST and DB_PASS credentials in DatabaseConnector.java.");
            }
        }
        System.out.println("--- Application End ---\n");
    }

    private static void listAndPrintUsers(Connection connection, UserRepository repository, String title) {
        System.out.println("\n--- " + title + " ---");
        List<User> users = repository.listAllUsers(connection);
        
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        for (User user : users) {
            System.out.printf("Username: %s | Email: %s | Name: %s\n", 
                              user.getUsername(), user.getEmail(), user.getFullName());
        }
    }
}