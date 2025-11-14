package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    
    // IMPORTANT: Replace these placeholders with your actual Supabase connection details!
    private static final String DB_HOST = "db.xgtnepssnpdrodutqolk.supabase.co";
    private static final String DB_USER = "postgres"; 
    private static final String DB_PASS = "Sagar@javanew2025"; // Your Supabase password
    private static final String DB_NAME = "postgres";
    private static final String DB_PORT = "5432";

    private static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}