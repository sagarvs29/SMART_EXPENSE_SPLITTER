package com.app;

/**
 * The Model Class for the 'expense_splits' table.
 * Represents a single debt record for a given expense.
 */
public class ExpenseSplit {
    
    // Corresponds to expense_splits table columns
    private final long expenseId; // The transaction being split
    private final int memberId; // The user who owes money
    private final double owedAmount;
    private final long splitId; // Unique ID from the database

    /**
     * Constructor used when saving a new split record.
     */
    public ExpenseSplit(long expenseId, int memberId, double owedAmount) {
        this.expenseId = expenseId;
        this.memberId = memberId;
        this.owedAmount = owedAmount;
        this.splitId = -1;
    }

    /**
     * Constructor used when retrieving a split record from the database.
     */
    public ExpenseSplit(long splitId, long expenseId, int memberId, double owedAmount) {
        this.splitId = splitId;
        this.expenseId = expenseId;
        this.memberId = memberId;
        this.owedAmount = owedAmount;
    }

    // --- Getter Methods ---
    public long getSplitId() { return splitId; }
    public long getExpenseId() { return expenseId; }
    public int getMemberId() { return memberId; }
    public double getOwedAmount() { return owedAmount; }
}