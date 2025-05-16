package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.User;
import com.simplyrugby.repository.UserRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteUserRepository implements UserRepository {
    private final ConnectionManager connectionManager;
    
    public SQLiteUserRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM User WHERE userID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding user with ID: " + id, e);
        }
    }
    
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all users", e);
        }
    }
    
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding user by username: " + username, e);
        }
    }
    
    @Override
    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE role = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding users by role: " + role, e);
        }
    }
    
    @Override
    public User findByMemberId(int memberId) {
        String sql = "SELECT * FROM User WHERE memberID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding user by member ID: " + memberId, e);
        }
    }
    
    @Override
    public int save(User user) {
        String sql = "INSERT INTO User (username, password, role, memberID) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            
            if (user.getMemberId() > 0) {
                pstmt.setInt(4, user.getMemberId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        user.setUserId(userId);
                        return userId;
                    }
                }
            }
            
            throw new RepositoryException("Creating user failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RepositoryException("Error saving user", e);
        }
    }
    
    @Override
    public boolean update(User user) {
        String sql = "UPDATE User SET username = ?, password = ?, role = ?, memberID = ? WHERE userID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            
            if (user.getMemberId() > 0) {
                pstmt.setInt(4, user.getMemberId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            pstmt.setInt(5, user.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating user", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM User WHERE userID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting user", e);
        }
    }
    
    @Override
    public String authenticate(String username, String password) {
        String sql = "SELECT role FROM User WHERE username = ? AND password = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("role");
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error during authentication", e);
        }
    }
    
   @Override
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE User SET password = ? WHERE userID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error changing password", e);
        }
    }
    
    @Override
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT 1 FROM User WHERE username = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            throw new RepositoryException("Error checking if username is taken", e);
        }
    }
    
    // Helper method to map ResultSet to User object
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("userID"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        
        int memberId = rs.getInt("memberID");
        if (!rs.wasNull()) {
            user.setMemberId(memberId);
        }
        
        return user;
    }
}