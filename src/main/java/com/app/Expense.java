package com.app;

import java.time.LocalDate;

/**
 * The Model Class (or Data Entity) for the 'expenses' table.
 * Updated to use payerId (int) to match database foreign key.
 */
public class Expense {

    private final double amount;
    private final String description;
    private final int payerId; // Changed from String username to int id
    private final LocalDate date;
    private final long expenseId;

    /**
     * Constructor for creating a new Expense object BEFORE it has an ID.
     */
    public Expense(double amount, String description, int payerId, LocalDate date) {
        this.amount = amount;
        this.description = description;
        this.payerId = payerId;
        this.date = date;
        this.expenseId = -1;
    }
    
    /**
     * Constructor for creating an Expense object retrieved from the database (WITH ID).
     */
    public Expense(long expenseId, double amount, String description, int payerId, LocalDate date) {
        this.expenseId = expenseId;
        this.amount = amount;
        this.description = description;
        this.payerId = payerId;
        this.date = date;
    }

    public long getExpenseId() { return expenseId; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public int getPayerId() { return payerId; } // New getter
    public LocalDate getDate() { return date; }
}