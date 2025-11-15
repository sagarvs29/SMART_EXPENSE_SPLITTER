package com.app;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import spark.Spark;

/**
 * This class serves as the embedded REST API (Controller Layer) using Spark Java.
 * It exposes endpoints for the React frontend to fetch calculated data from the modular backend.
 */
public class WebApp {

    private static final Logger LOGGER = Logger.getLogger(WebApp.class.getName());
    private static final int PORT = 4567; // Default Spark port

    public static void main(String[] args) {
        
        // Configuration: Set up Gson for JSON serialization
        Gson gson = new Gson();
        
        // Initialize modular components
        UserRepository userRepository = new UserRepository();
        ExpenseRepository expenseRepository = new ExpenseRepository();
        BalanceService balanceService = new BalanceService(expenseRepository, userRepository);
        
        // Configure Spark Server
        Spark.port(PORT);
    Spark.staticFiles.location("/public"); // Serves index.html from src/main/resources/public
        
        // Enable CORS (Critical for running React and Java on different ports/addresses)
        enableCORS("*", "*", "*");

        LOGGER.info("Spark Server starting on port " + PORT);
        
        // --- DATA SETUP (Initial demo data run to populate the DB) ---
        // This is done once when the server starts to ensure data exists for the frontend demo.
        setupInitialData(userRepository, expenseRepository, balanceService);


        // ----------------- API ENDPOINTS -----------------

        // Endpoint 1: Get list of all users
        Spark.get("/api/users", "application/json", (req, res) -> {
            try (Connection conn = DatabaseConnector.getConnection()) {
                List<User> users = userRepository.listAllUsers(conn);
                res.status(200);
                return gson.toJson(users);
            } catch (DatabaseException e) {
                res.status(500);
                LOGGER.log(Level.SEVERE, "Database error fetching users.", e);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint 2: Get Net Balances Report
        Spark.get("/api/balances", "application/json", (req, res) -> {
            try (Connection conn = DatabaseConnector.getConnection()) {
                Map<Integer, Double> balances = balanceService.calculateNetBalances(conn);
                res.status(200);
                return gson.toJson(balances);
            } catch (DatabaseException e) {
                res.status(500);
                LOGGER.log(Level.SEVERE, "Database error calculating balances.", e);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Endpoint 3: Record a new expense (POST request)
        Spark.post("/api/expense", "application/json", (req, res) -> {
            try {
                // NOTE: Using Gson to parse the request body dynamically
                Map<String, Object> expenseData = gson.fromJson(req.body(), Map.class);
                
                double amount = ((Number) expenseData.get("amount")).doubleValue();
                String description = (String) expenseData.get("description");
                int payerId = ((Number) expenseData.get("payerId")).intValue();

                // Create Expense Model (Date defaults to today)
                Expense newExpense = new Expense(amount, description, payerId, LocalDate.now());
                
                try (Connection conn = DatabaseConnector.getConnection()) {
                    long newExpenseId = expenseRepository.addExpense(conn, newExpense);
                    
                    if (newExpenseId > 0) {
                        // After creating the expense, usually we'd go straight to the split endpoint
                        // For simplicity here, we just confirm creation.
                        res.status(201);
                        return gson.toJson(Map.of("message", "Expense recorded successfully.", "id", newExpenseId));
                    }
                }
                res.status(500);
                return gson.toJson(Map.of("error", "Failed to record expense."));

            } catch (Exception e) {
                res.status(400); // Bad Request
                LOGGER.log(Level.SEVERE, "Error processing expense POST request.", e);
                return gson.toJson(Map.of("error", "Invalid data format or missing fields: " + e.getMessage()));
            }
        });

        // Root route -> serve frontend
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });
        
        // Force the main thread to wait for the server to initialize and stay running
    Spark.awaitInitialization();
    LOGGER.info("Spark Server started. Open http://localhost:" + PORT + "/index.html");
    }
    
    // --- Helper for Initial Setup ---
    
    // This method ensures demo data exists when the server first starts.
    private static void setupInitialData(UserRepository userRepository, ExpenseRepository expenseRepository, BalanceService balanceService) {
         try (Connection connection = DatabaseConnector.getConnection()) {
            
            // Define user IDs for consistent demo (from console demo)
            int aliceUserId = 1; 
            int bobUserId = 6; 
            List<Integer> partyMembers = Arrays.asList(aliceUserId, bobUserId); 

            // 1. Setup (Run once, errors ignored)
            userRepository.registerNewUser(connection, new User("alice_j", "alice.j@example.com", "hash1", "Alice Johnson"));
            userRepository.registerNewUser(connection, new User("bob_s", "bob.s@example.com", "hash2", "Bob Smith"));
            userRepository.createGroup(connection, "Web Demo Group", aliceUserId); // Creates Group ID 1
            
            // 2. Demo Transaction (Run once, errors ignored)
            Expense dinnerExpense = new Expense(90.00, "Initial Web Demo Dinner", aliceUserId, LocalDate.now());
            long expenseId = expenseRepository.addExpense(connection, dinnerExpense);
            
            if (expenseId > 0) {
                Expense recordedExpense = new Expense(expenseId, dinnerExpense.getAmount(), dinnerExpense.getDescription(), dinnerExpense.getPayerId(), dinnerExpense.getDate());
                expenseRepository.splitExpenseAndRecordDebts(connection, recordedExpense, partyMembers);
            }
            
            // 3. Record Initial Settlement (to ensure the balance report isn't massive)
            // Bob owes Alice $45.00 for the dinner split.
            Settlement initialPayment = new Settlement(1, bobUserId, aliceUserId, 45.00); 
            expenseRepository.recordSettlement(connection, initialPayment);

        } catch (Exception e) {
             // We catch all exceptions here because setup must not crash the web server startup
             LOGGER.log(Level.WARNING, "Initial data setup failed (Likely duplicate expense records). Proceeding with server start.", e);
        }
    }

    // Utility method to enable CORS (allows frontend access)
    private static void enableCORS(final String origin, final String methods, final String headers) {
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            // Handle preflight requests
            if (request.requestMethod().equals("OPTIONS")) {
                response.header("Access-Control-Allow-Methods", methods);
                response.status(200);
            }
        });
    }
}