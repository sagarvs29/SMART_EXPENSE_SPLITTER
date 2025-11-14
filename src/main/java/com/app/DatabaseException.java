package com.app;

/**
 * Custom Exception class for all database-related errors.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(String message) {
        super(message);
    }
}