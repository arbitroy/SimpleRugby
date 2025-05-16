package com.simplyrugby.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connections for the application.
 */
public class ConnectionManager {
    private final String dbUrl;
    private Connection connection;
    
    /**
     * Creates a new ConnectionManager for the specified database.
     * 
     * @param dbName The name of the database file
     */
    public ConnectionManager(String dbName) {
        this.dbUrl = "jdbc:sqlite:" + dbName;
    }
    
    /**
     * Gets a connection to the database.
     * If a connection already exists and is valid, it will be reused.
     * 
     * @return A connection to the database
     * @throws SQLException If a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");
                
                // Create a new connection
                connection = DriverManager.getConnection(dbUrl);
                connection.setAutoCommit(true);
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found", e);
            }
        }
        return connection;
    }
    
    /**
     * Closes the database connection if it is open.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                // Log the error but don't throw it
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}