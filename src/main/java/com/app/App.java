package com.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application class for the SMART EXPENSE SPLITTER.
 * This file is the final orchestrator, coordinating between all modular components.
 */
public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        System.out.println("\n--- Starting Expense Splitter Application ---");

        UserRepository userRepository = new UserRepository();
        ExpenseRepository expenseRepository = new ExpenseRepository();
        BalanceService balanceService = new BalanceService(expenseRepository, userRepository);
        
        try (Connection connection = DatabaseConnector.getConnection()) {
            System.out.println("🎉 Connection successful! Connected to Expense Splitter DB.");
            
            // Define the payer and the list of members
            int aliceUserId = 1; // Alice's ID 
            int bobUserId = 6; // Bob's current ID 
            List<Integer> partyMembers = Arrays.asList(aliceUserId, bobUserId); 

            // --- 1. USER MANAGEMENT & GROUP CREATION (NEW FIX) ---
            System.out.println("\n--- 1. USER AND GROUP SETUP ---");
            User alice = new User("alice_j", "alice.j@example.com", "hash1", "Alice Johnson");
            User bob = new User("bob_s", "bob.s@example.com", "hash2", "Bob Smith");
            
            userRepository.registerNewUser(connection, alice);
            userRepository.registerNewUser(connection, bob);

            // FIX: Create a required Group record and capture its ID
            long groupId = userRepository.createGroup(connection, "Team Dinner Group", aliceUserId);
            
            // --- 2. DEBT CREATION (Initial State) ---
            System.out.println("\n--- 2. DEBT CREATION ---");

            // A. Create the Expense
            Expense dinnerExpense = new Expense(
                90.00, // $90.00 total expense
                "Team Dinner", 
                aliceUserId, 
                LocalDate.now()
            );

            long newExpenseId = expenseRepository.addExpense(connection, dinnerExpense);

            // B. Record the Splits (Bob owes $45.00)
            if (newExpenseId > 0) {
                Expense recordedExpense = new Expense(newExpenseId, dinnerExpense.getAmount(), dinnerExpense.getDescription(), dinnerExpense.getPayerId(), dinnerExpense.getDate());
                expenseRepository.splitExpenseAndRecordDebts(connection, recordedExpense, partyMembers);
            }

            // --- 3. INTERMEDIATE BALANCE CHECK ---
            System.out.println("\n--- 3. INTERMEDIATE BALANCE CHECK (Before Settlement) ---");
            Map<Integer, Double> initialBalances = balanceService.calculateNetBalances(connection);
            printNetBalances(initialBalances);
            
            // --- 4. SETTLEMENT (Debt Clearance) ---
            System.out.println("\n--- 4. SETTLEMENT ---");
            // Bob (6) pays Alice (1) the owed amount of $45.00
            // FIX: Pass the actual groupId generated above
            Settlement payment = new Settlement((int)groupId, bobUserId, aliceUserId, 45.00); 
            expenseRepository.recordSettlement(connection, payment);
            
            // --- 5. FINAL NET BALANCE REPORT ---
            System.out.println("\n--- 5. FINAL NET BALANCE REPORT (After Settlement) ---");
            Map<Integer, Double> finalBalances = balanceService.calculateNetBalances(connection);
            printNetBalances(finalBalances);
            
            // --- 6. UTILITY MODULE DEMO ---
            System.out.println("\n--- 6. UTILITY DEMO ---");
            double dinnerTotal = 55.75;
            int partySize = 3;
            double splitAmount = MathOperations.calculateSplit(dinnerTotal, partySize); 
            System.out.printf("Splitting $%.2f among %d people. Each person owes: $%.2f\n", dinnerTotal, partySize, splitAmount);

        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "❌ APPLICATION ERROR: Database operation failed.", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ FAILED TO CONNECT TO DATABASE.", e);
        }
        System.out.println("--- Application End ---\n");
    }

    private static void printNetBalances(Map<Integer, Double> balances) {
        if (balances.isEmpty()) {
            System.out.println("All balances are settled (Net Zero).");
            return;
        }
        balances.forEach((userId, balance) -> {
            String status = balance > 0 ? "IS OWED" : "OWES";
            double absBalance = Math.abs(balance);
            System.out.printf("User ID %d: %s $%.2f\n", userId, status, absBalance);
        });
    }

    private static void listAndPrintUsers(Connection connection, UserRepository repository, String title) {
        System.out.println("\n--- " + title + " ---");
        List<User> users = repository.listAllUsers(connection);
        
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        for (User user : users) {
            System.out.printf("Username: %s | Email: %s | Name: %s\n", user.getUsername(), user.getEmail(), user.getFullName());
        }
    }
}