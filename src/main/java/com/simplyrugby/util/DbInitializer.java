package com.simplyrugby.util;

import com.simplyrugby.repository.impl.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the database schema and default data.
 */
public class DbInitializer {
    
    /**
     * Initializes the database, creating tables if they don't exist.
     * 
     * @param connectionManager The connection manager to use
     * @throws SQLException If a database error occurs
     */
    public static void initialize(ConnectionManager connectionManager) throws SQLException {
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Member table
            stmt.execute("CREATE TABLE IF NOT EXISTS Member (" +
                         "memberID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "firstName TEXT NOT NULL," +
                         "lastName TEXT NOT NULL," +
                         "dob TEXT NOT NULL," +
                         "email TEXT," +
                         "phone TEXT," +
                         "address TEXT)");
            
            // Create Coach table
            stmt.execute("CREATE TABLE IF NOT EXISTS Coach (" +
                         "coachID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "memberID INTEGER NOT NULL," +
                         "qualifications TEXT," +
                         "FOREIGN KEY (memberID) REFERENCES Member(memberID))");
            
            // Create Squad table
            stmt.execute("CREATE TABLE IF NOT EXISTS Squad (" +
                         "squadID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "squadName TEXT NOT NULL," +
                         "ageGrade TEXT NOT NULL)");
            
            // Create Player table
            stmt.execute("CREATE TABLE IF NOT EXISTS Player (" +
                         "playerID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "memberID INTEGER NOT NULL," +
                         "position TEXT," +
                         "squadID INTEGER," +
                         "emergencyContactID INTEGER," +
                         "medicalConditions TEXT," +
                         "FOREIGN KEY (memberID) REFERENCES Member(memberID)," +
                         "FOREIGN KEY (squadID) REFERENCES Squad(squadID)," +
                         "FOREIGN KEY (emergencyContactID) REFERENCES Member(memberID))");
            
            // Create CoachSquad (many-to-many relationship)
            stmt.execute("CREATE TABLE IF NOT EXISTS CoachSquad (" +
                         "coachID INTEGER," +
                         "squadID INTEGER," +
                         "PRIMARY KEY (coachID, squadID)," +
                         "FOREIGN KEY (coachID) REFERENCES Coach(coachID)," +
                         "FOREIGN KEY (squadID) REFERENCES Squad(squadID))");
            
            // Create Game table
            stmt.execute("CREATE TABLE IF NOT EXISTS Game (" +
                         "gameID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "date TEXT NOT NULL," +
                         "opponent TEXT NOT NULL," +
                         "finalScore TEXT," +
                         "venue TEXT," +
                         "squadID INTEGER," +
                         "FOREIGN KEY (squadID) REFERENCES Squad(squadID))");
            
            // Create GameStats table
            stmt.execute("CREATE TABLE IF NOT EXISTS GameStats (" +
                         "gameStatsID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "playerID INTEGER," +
                         "gameID INTEGER," +
                         "tackles INTEGER," +
                         "passes INTEGER," +
                         "tries INTEGER," +
                         "kicks INTEGER," +
                         "overallRating INTEGER," +
                         "attended BOOLEAN," +
                         "FOREIGN KEY (playerID) REFERENCES Player(playerID)," +
                         "FOREIGN KEY (gameID) REFERENCES Game(gameID))");
            
            // Create Training table
            stmt.execute("CREATE TABLE IF NOT EXISTS Training (" +
                         "trainingID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "date TEXT NOT NULL," +
                         "squadID INTEGER," +
                         "focusAreas TEXT," +
                         "coachNotes TEXT," +
                         "FOREIGN KEY (squadID) REFERENCES Squad(squadID))");
            
            // Create TrainingAttendance table
            stmt.execute("CREATE TABLE IF NOT EXISTS TrainingAttendance (" +
                         "attendanceID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "playerID INTEGER," +
                         "trainingID INTEGER," +
                         "present BOOLEAN," +
                         "playerNotes TEXT," +
                         "FOREIGN KEY (playerID) REFERENCES Player(playerID)," +
                         "FOREIGN KEY (trainingID) REFERENCES Training(trainingID))");
            
            // Create User table for authentication
            stmt.execute("CREATE TABLE IF NOT EXISTS User (" +
                         "userID INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "username TEXT NOT NULL UNIQUE," +
                         "password TEXT NOT NULL," +
                         "role TEXT NOT NULL," +
                         "memberID INTEGER," +
                         "FOREIGN KEY (memberID) REFERENCES Member(memberID))");
            
            // Create default users if needed
            createDefaultUsers(conn);
            
            System.out.println("Database initialized successfully.");
        }
    }
    
    /**
     * Creates default users if they don't exist.
     * 
     * @param conn The database connection
     * @throws SQLException If a database error occurs
     */
    private static void createDefaultUsers(Connection conn) throws SQLException {
        // Check if default users exist
        int userCount = 0;
        try (Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM User");
            if (rs.next()) {
                userCount = rs.getInt(1);
            }
        }
        
        // If no users exist, create default users
        if (userCount == 0) {
            // Create admin member
            try (var pstmt = conn.prepareStatement(
                    "INSERT INTO Member (firstName, lastName, dob, email, phone, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)", 
                    Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "Admin");
                pstmt.setString(2, "User");
                pstmt.setString(3, "1980-01-01");
                pstmt.setString(4, "admin@simplyrugby.org");
                pstmt.setString(5, "12345678901");
                pstmt.setString(6, "Simply Rugby Club, Main Street");
                
                pstmt.executeUpdate();
                
                int adminMemberId = 0;
                try (var keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        adminMemberId = keys.getInt(1);
                    }
                }
                
                // Create admin user
                if (adminMemberId > 0) {
                    try (var userStmt = conn.prepareStatement(
                            "INSERT INTO User (username, password, role, memberID) " +
                            "VALUES (?, ?, ?, ?)")) {
                        userStmt.setString(1, "admin");
                        userStmt.setString(2, "admin"); // In production, use hashed passwords
                        userStmt.setString(3, "Secretary");
                        userStmt.setInt(4, adminMemberId);
                        
                        userStmt.executeUpdate();
                    }
                }
            }
            
            // Create coach member
            try (var pstmt = conn.prepareStatement(
                    "INSERT INTO Member (firstName, lastName, dob, email, phone, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)", 
                    Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, "Coach");
                pstmt.setString(2, "User");
                pstmt.setString(3, "1985-01-01");
                pstmt.setString(4, "coach@simplyrugby.org");
                pstmt.setString(5, "12345678902");
                pstmt.setString(6, "Simply Rugby Club, Main Street");
                
                pstmt.executeUpdate();
                
                int coachMemberId = 0;
                try (var keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        coachMemberId = keys.getInt(1);
                    }
                }
                
                // Create coach user
                if (coachMemberId > 0) {
                    // Create coach record
                    try (var coachStmt = conn.prepareStatement(
                            "INSERT INTO Coach (memberID, qualifications) VALUES (?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                        coachStmt.setInt(1, coachMemberId);
                        coachStmt.setString(2, "Level 2 Rugby Coaching Certificate");
                        
                        coachStmt.executeUpdate();
                    }
                    
                    // Create coach user
                    try (var userStmt = conn.prepareStatement(
                            "INSERT INTO User (username, password, role, memberID) " +
                            "VALUES (?, ?, ?, ?)")) {
                        userStmt.setString(1, "coach");
                        userStmt.setString(2, "coach"); // In production, use hashed passwords
                        userStmt.setString(3, "Coach");
                        userStmt.setInt(4, coachMemberId);
                        
                        userStmt.executeUpdate();
                    }
                }
            }
            
            System.out.println("Default users created successfully.");
        }
    }
}