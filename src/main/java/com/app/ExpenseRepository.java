package com.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class serves as the Data Access Object (DAO) for the Expense entity.
 * It contains all the SQL logic (CREATE, READ, SPLIT, SETTLEMENT) for expenses, splits, and settlements.
 */
public class ExpenseRepository {

    private static final Logger LOGGER = Logger.getLogger(ExpenseRepository.class.getName());

    // --- CREATE OPERATIONS ---

    /**
     * Inserts a new expense record into the 'expenses' table.
     * @return The generated expense_id, or -1 if creation failed.
     */
    public long addExpense(Connection conn, Expense expense) {
        String sql = "INSERT INTO public.expenses (amount, description, payer_id, expense_date) VALUES (?, ?, ?, ?)";
        long expenseId = -1;
        
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setDouble(1, expense.getAmount());
            statement.setString(2, expense.getDescription());
            statement.setInt(3, expense.getPayerId());
            statement.setDate(4, java.sql.Date.valueOf(expense.getDate())); 

            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        expenseId = rs.getLong(1);
                        System.out.printf("  [CREATE] Successfully added expense (ID: %d) of $%.2f paid by User ID: %d.\n", 
                                          expenseId, expense.getAmount(), expense.getPayerId());
                    }
                }
            }
        } catch (SQLException e) {
             throw new DatabaseException("Failed to add new expense: " + expense.getDescription(), e);
        }
        return expenseId;
    }

    /**
     * Calculates the split amount and records the debt (expense_splits) for each member.
     */
    public void splitExpenseAndRecordDebts(Connection conn, Expense expense, List<Integer> memberIds) {
        if (memberIds.isEmpty() || expense.getAmount() <= 0) return;

        double splitAmount = expense.getAmount() / memberIds.size();
        String sql = "INSERT INTO public.expense_splits (expense_id, member_id, owed_amount) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (int memberId : memberIds) {
                // If the member is the payer, we assume they owe nothing in this split.
                if (memberId != expense.getPayerId()) {
                    statement.setLong(1, expense.getExpenseId());
                    statement.setInt(2, memberId);
                    statement.setDouble(3, splitAmount);
                    statement.addBatch(); // Add the insert to the batch
                }
            }
            
            int[] results = statement.executeBatch();
            System.out.printf("  [SPLIT] Recorded %d debt records for Expense ID: %d.\n", results.length, expense.getExpenseId());
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record expense splits for ID: " + expense.getExpenseId(), e);
        }
    }
    
    /**
     * Records a payment between a payer (debtor) and a receiver (creditor).
     * This is used to adjust the net balance calculation.
     */
    public void recordSettlement(Connection conn, Settlement settlement) {
        // NOTE: Your schema specifies group_id, which we simplify to 0 for this demo.
        String sql = "INSERT INTO public.settlements (group_id, payer_id, receiver_id, amount, settlement_date) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, settlement.getGroupId());
            statement.setInt(2, settlement.getPayerId());
            statement.setInt(3, settlement.getReceiverId());
            statement.setDouble(4, settlement.getAmount());
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(settlement.getSettlementDate()));

            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        long settlementId = rs.getLong(1);
                        System.out.printf("  [SETTLE] Settlement ID %d recorded: User %d paid User %d $%.2f.\n", 
                                          settlementId, settlement.getPayerId(), settlement.getReceiverId(), settlement.getAmount());
                    }
                }
            }
        } catch (SQLException e) {
             throw new DatabaseException("Failed to record settlement.", e);
        }
    }


    // --- READ OPERATIONS ---

    /**
     * Retrieves all expense records.
     * @return A List of Expense objects.
     */
    public List<Expense> listAllExpenses(Connection conn) {
        String sql = "SELECT expense_id, amount, description, payer_id, expense_date FROM public.expenses ORDER BY expense_id";
        List<Expense> expenseList = new ArrayList<>();
        
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                long id = resultSet.getLong("expense_id");
                double amount = resultSet.getDouble("amount");
                String description = resultSet.getString("description");
                int payerId = resultSet.getInt("payer_id");
                LocalDate date = resultSet.getDate("expense_date").toLocalDate(); 

                Expense expense = new Expense(id, amount, description, payerId, date);
                expenseList.add(expense);
            }
        } catch (SQLException e) {
             throw new DatabaseException("Failed to list all expenses.", e);
        }
        return expenseList;
    }
    
    /**
     * Retrieves the individual debt records for a single expense.
     * @return A List of ExpenseSplit objects (debts).
     */
    public List<ExpenseSplit> listExpenseSplits(Connection conn, long expenseId) {
        String sql = "SELECT split_id, expense_id, member_id, owed_amount FROM public.expense_splits WHERE expense_id = ?";
        List<ExpenseSplit> splitList = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, expenseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long splitId = resultSet.getLong("split_id");
                    int memberId = resultSet.getInt("member_id");
                    double owedAmount = resultSet.getDouble("owed_amount");

                    ExpenseSplit split = new ExpenseSplit(splitId, expenseId, memberId, owedAmount);
                    splitList.add(split);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list expense splits for ID: " + expenseId, e);
        }
        return splitList;
    }

    /**
     * Retrieves all settlement records made to clear debts.
     * @return A List of Settlement objects.
     */
    public List<Settlement> listAllSettlements(Connection conn) {
        String sql = "SELECT settlement_id, group_id, payer_id, receiver_id, amount, settlement_date FROM public.settlements";
        List<Settlement> settlementList = new ArrayList<>();

        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                long id = resultSet.getLong("settlement_id");
                int groupId = resultSet.getInt("group_id");
                int payerId = resultSet.getInt("payer_id");
                int receiverId = resultSet.getInt("receiver_id");
                double amount = resultSet.getDouble("amount");
                // Need to convert Timestamp (from SQL) to LocalDateTime (in Java model)
                LocalDateTime date = resultSet.getTimestamp("settlement_date").toLocalDateTime();

                Settlement settlement = new Settlement(id, groupId, payerId, receiverId, amount, date);
                settlementList.add(settlement);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to list all settlements.", e);
        }
        return settlementList;
    }
}