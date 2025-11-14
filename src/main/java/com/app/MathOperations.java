package com.app;

/**
 * This class contains reusable business logic (mathematical operations).
 */
public class MathOperations {

    public int add(int a, int b) { return a + b; }
    public int subtract(int a, int b) { return a - b; }
    public int multiply(int a, int b) { return a * b; }

    public double divide(double numerator, double denominator) {
        if (denominator == 0.0) {
            System.err.println("  [ERROR] Cannot divide by zero. Returning 0.");
            return 0.0;
        }
        return numerator / denominator;
    }

    /**
     * Calculates the equal split amount for a total expense among a group of people.
     */
    public static double calculateSplit(double totalAmount, int numPeople) {
        if (numPeople <= 0) {
            System.err.println("  [ERROR] Split requires at least one person. Returning 0.0.");
            return 0.0;
        }
        return totalAmount / numPeople;
    }
}