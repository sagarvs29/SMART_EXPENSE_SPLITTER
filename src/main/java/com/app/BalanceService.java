package com.app;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service class is responsible for calculating aggregated data,
 * primarily the net balance between all users in the application.
 * It uses data retrieved from the repositories.
 */
public class BalanceService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    
    // The BalanceService requires instances of the repositories to pull data
    public BalanceService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Calculates the net debt/credit for every user across all expenses and settlements.
     * The final balance is relative to the user:
     * - Positive value: The user is owed money (Net Credit).
     * - Negative value: The user owes money (Net Debt).
     * @param conn The active database connection.
     * @return A Map where Key=UserId and Value=Net Balance.
     */
    public Map<Integer, Double> calculateNetBalances(Connection conn) {
        Map<Integer, Double> netBalances = new HashMap<>();

        // 1. Process EXPENSE SPLITS (The initial debts)
        // The payer gets credited (positive), the members splitting the expense get debited (negative).
        List<Expense> allExpenses = expenseRepository.listAllExpenses(conn);
        
        for (Expense expense : allExpenses) {
            double amount = expense.getAmount();

            // A. Credit the Payer: Payer is owed the full amount they spent
            int payerId = expense.getPayerId();
            netBalances.merge(payerId, amount, Double::sum);

            // B. Debit the Split Members: Members who owe money receive a negative amount
            
            List<ExpenseSplit> splits = expenseRepository.listExpenseSplits(conn, expense.getExpenseId());

            for (ExpenseSplit split : splits) {
                int memberId = split.getMemberId();
                double owedAmount = split.getOwedAmount(); // This is a debit (negative balance)
                
                // Merge adds the negative owed amount to the user's balance
                netBalances.merge(memberId, -owedAmount, Double::sum);
            }
        }
        
        // 2. Process SETTLEMENTS (Clearing the debts)
        // A settlement reduces the net debt: Payer's balance INCREASES (less debt), Receiver's DECREASES (less owed).
        List<Settlement> allSettlements = expenseRepository.listAllSettlements(conn);

        for (Settlement settlement : allSettlements) {
            int payerId = settlement.getPayerId(); // Debtor
            int receiverId = settlement.getReceiverId(); // Creditor
            double amount = settlement.getAmount();

            // Payer's balance increases (clearing debt)
            netBalances.merge(payerId, amount, Double::sum); 
            
            // Receiver's balance decreases (less money owed to them)
            netBalances.merge(receiverId, -amount, Double::sum);
        }
        
        // Remove users with a zero balance from the final report
        netBalances.entrySet().removeIf(entry -> Math.abs(entry.getValue()) < 0.01);
        
        return netBalances;
    }
}