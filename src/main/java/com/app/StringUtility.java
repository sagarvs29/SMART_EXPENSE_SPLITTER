package com.app;

/**
 * Utility classes typically contain 'static' methods for simple helper functions.
 */
public class StringUtility {

    public static String reverseString(String input) {
        if (input == null) {
            return null;
        }
        return new StringBuilder(input).reverse().toString();
    }
}