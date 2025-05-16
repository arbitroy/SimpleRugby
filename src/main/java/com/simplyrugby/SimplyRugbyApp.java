package com.simplyrugby;

import com.simplyrugby.cli.SimplyRugbyCommand;
import com.simplyrugby.util.DbInitializer;
import com.simplyrugby.util.DependencyManager;
import com.simplyrugby.repository.impl.ConnectionManager;
import picocli.CommandLine;

/**
 * Main application entry point.
 */
public class SimplyRugbyApp {
    private static final String DB_NAME = "simplyrugby.db";
    
    public static void main(String[] args) {
        try {
            // Initialize the system
            initialize();
            
            // Create command line interface
            CommandLine cmd = new CommandLine(new SimplyRugbyCommand());
            
            // Execute the command
            int exitCode = cmd.execute(args);
            
            // Check if we should exit
            if (exitCode != 0) {
                System.exit(exitCode);
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            // Clean up resources
            DependencyManager.shutdown();
        }
    }
    
    /**
     * Initializes the application components.
     * 
     * @throws Exception If initialization fails
     */
    private static void initialize() throws Exception {
        // Setup the dependency manager
        DependencyManager.initialize(DB_NAME);
        
        // Initialize the database
        ConnectionManager connectionManager = new ConnectionManager(DB_NAME);
        DbInitializer.initialize(connectionManager);
    }
}