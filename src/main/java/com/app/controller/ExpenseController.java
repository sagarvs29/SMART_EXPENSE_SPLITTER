package com.app.controller;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.BalanceService;
import com.app.DatabaseConnector;
import com.app.Expense;
import com.app.ExpenseRepository;
import com.app.Settlement;
import com.app.User;
import com.app.UserRepository;
import com.app.dto.ExpenseRequest;
import com.app.dto.FriendRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseRepository expenseRepository = new ExpenseRepository();
    private final UserRepository userRepository = new UserRepository();
    private final BalanceService balanceService = new BalanceService(expenseRepository, userRepository);

    // --- FRIENDS ---
    @PostMapping("/friends")
    public Map<String, String> addFriend(@RequestBody FriendRequest req) throws Exception {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Map FriendRequest to existing User model: username=name, email, passwordHash blank, fullName=name
            User user = new User(req.getName(), req.getEmail(), "", req.getName());
            userRepository.registerNewUser(conn, user);
            return Map.of("status", "ok");
        }
    }

    @GetMapping("/friends")
    public List<User> getAllFriends() throws Exception {
        try (Connection conn = DatabaseConnector.getConnection()) {
            return userRepository.listAllUsers(conn);
        }
    }

    @DeleteMapping("/friends/{id}")
    public Map<String, String> deleteFriend(@PathVariable int id) throws Exception {
        try (Connection conn = DatabaseConnector.getConnection()) {
            List<User> users = userRepository.listAllUsers(conn);
            User target = users.stream().filter(u -> u.getUserId() == id).findFirst().orElse(null);
            if (target == null) {
                return Map.of("status", "not_found");
            }
            userRepository.deleteUser(conn, target.getUsername());
            return Map.of("status", "deleted");
        }
    }

    // --- EXPENSES ---
    @PostMapping("/expenses")
    public Map<String, Object> saveExpense(@RequestBody ExpenseRequest req) throws Exception {
        try (Connection conn = DatabaseConnector.getConnection()) {
            Expense expense = new Expense(req.getAmount(), req.getDescription(), req.getPayerId(), req.getDate());
            long id = expenseRepository.addExpense(conn, expense);

            // Equal split among participants if provided
            if (req.getParticipantIds() != null && !req.getParticipantIds().isEmpty()) {
                Expense persisted = new Expense(id, req.getAmount(), req.getDescription(), req.getPayerId(), req.getDate());
                expenseRepository.splitExpenseAndRecordDebts(conn, persisted, req.getParticipantIds());
            }

            return Map.of("expenseId", id);
        }
    }

    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() throws Exception {
        try (Connection conn = DatabaseConnector.getConnection()) {
            return expenseRepository.listAllExpenses(conn);
        }
    }

    // --- BALANCES ---
    @GetMapping("/balances")
    public Map<Integer, Double> getBalances() throws Exception {
        try (Connection conn = DatabaseConnector.getConnection()) {
            return balanceService.calculateNetBalances(conn);
        }
    }

    // --- SETTLEMENTS ---
    @GetMapping("/settlements")
    public List<Settlement> getSettlements() throws Exception {
        try (Connection conn = DatabaseConnector.getConnection()) {
            return expenseRepository.listAllSettlements(conn);
        }
    }

    // --- ADMIN / MAINTENANCE ---
    @PostMapping("/admin/reset")
    public Map<String, Object> resetAllData() throws Exception {
        try (Connection conn = DatabaseConnector.getConnection();
             java.sql.Statement st = conn.createStatement()) {

            // Order matters due to foreign keys (splits, settlements depend on expenses/users)
            try {
                st.executeUpdate("TRUNCATE TABLE public.expense_splits RESTART IDENTITY CASCADE");
            } catch (Exception ignore) {}
            try {
                st.executeUpdate("TRUNCATE TABLE public.settlements RESTART IDENTITY CASCADE");
            } catch (Exception ignore) {}
            try {
                st.executeUpdate("TRUNCATE TABLE public.expenses RESTART IDENTITY CASCADE");
            } catch (Exception ignore) {}
            try {
                st.executeUpdate("TRUNCATE TABLE public.users RESTART IDENTITY CASCADE");
            } catch (Exception ignore) {}
            return Map.of("status", "reset", "message", "All primary tables truncated.");
        }
    }
}
