package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.repository.CoachRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteCoachRepository implements CoachRepository {
    private final ConnectionManager connectionManager;
    
    public SQLiteCoachRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public Coach findById(int id) {
        String sql = "SELECT c.*, m.* FROM Coach c " +
                     "JOIN Member m ON c.memberID = m.memberID " +
                     "WHERE c.coachID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Coach coach = mapResultSetToCoach(rs);
                
                // Load assigned squads
                coach.setAssignedSquads(getAssignedSquads(conn, id));
                
                return coach;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding coach with ID: " + id, e);
        }
    }
    
    @Override
    public Coach findByMemberId(int memberId) {
        String sql = "SELECT c.*, m.* FROM Coach c " +
                     "JOIN Member m ON c.memberID = m.memberID " +
                     "WHERE c.memberID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Coach coach = mapResultSetToCoach(rs);
                
                // Load assigned squads
                coach.setAssignedSquads(getAssignedSquads(conn, coach.getCoachId()));
                
                return coach;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding coach with member ID: " + memberId, e);
        }
    }
    
    @Override
    public List<Coach> findAll() {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT c.*, m.* FROM Coach c " +
                     "JOIN Member m ON c.memberID = m.memberID";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Coach coach = mapResultSetToCoach(rs);
                coaches.add(coach);
            }
            
            // Load assigned squads for each coach
            for (Coach coach : coaches) {
                coach.setAssignedSquads(getAssignedSquads(conn, coach.getCoachId()));
            }
            
            return coaches;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all coaches", e);
        }
    }
    
    @Override
    public List<Coach> findByName(String name) {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT c.*, m.* FROM Coach c " +
                     "JOIN Member m ON c.memberID = m.memberID " +
                     "WHERE m.firstName LIKE ? OR m.lastName LIKE ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + name + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Coach coach = mapResultSetToCoach(rs);
                coaches.add(coach);
            }
            
            // Load assigned squads for each coach
            for (Coach coach : coaches) {
                coach.setAssignedSquads(getAssignedSquads(conn, coach.getCoachId()));
            }
            
            return coaches;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding coaches by name: " + name, e);
        }
    }
    
   @Override
    public List<Coach> findBySquad(int squadId) {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT c.*, m.* FROM Coach c " +
                     "JOIN Member m ON c.memberID = m.memberID " +
                     "JOIN CoachSquad cs ON c.coachID = cs.coachID " +
                     "WHERE cs.squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Coach coach = mapResultSetToCoach(rs);
                coaches.add(coach);
            }
            
            // Load assigned squads for each coach
            for (Coach coach : coaches) {
                coach.setAssignedSquads(getAssignedSquads(conn, coach.getCoachId()));
            }
            
            return coaches;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding coaches by squad: " + squadId, e);
        }
    }
    
    @Override
    public int save(Coach coach) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // First save or update the member
            int memberId;
            if (coach.getMemberId() > 0) {
                // Update existing member
                updateMember(conn, coach);
                memberId = coach.getMemberId();
            } else {
                // Insert new member
                memberId = insertMember(conn, coach);
                coach.setMemberId(memberId);
            }
            
            // Now save the coach
            String sql = "INSERT INTO Coach (memberID, qualifications) VALUES (?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, memberId);
                pstmt.setString(2, coach.getQualifications());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int coachId = generatedKeys.getInt(1);
                            coach.setCoachId(coachId);
                            
                            // Save assigned squads
                            saveAssignedSquads(conn, coach);
                            
                            conn.commit();
                            return coachId;
                        }
                    }
                }
                
                conn.rollback();
                throw new SQLException("Creating coach failed, no ID generated.");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new RepositoryException("Error saving coach", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    // Log autocommit reset error
                }
            }
        }
    }
    
    @Override
    public boolean update(Coach coach) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // Update member details
            updateMember(conn, coach);
            
            // Update coach details
            String sql = "UPDATE Coach SET qualifications = ? WHERE coachID = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, coach.getQualifications());
                pstmt.setInt(2, coach.getCoachId());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    // Update assigned squads
                    updateAssignedSquads(conn, coach);
                    
                    conn.commit();
                    return true;
                }
                
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new RepositoryException("Error updating coach", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    // Log autocommit reset error
                }
            }
        }
    }
    
    @Override
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // Get member ID first
            int memberId;
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT memberID FROM Coach WHERE coachID = ?")) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    memberId = rs.getInt("memberID");
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            // Delete coach squad assignments
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM CoachSquad WHERE coachID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete coach
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Coach WHERE coachID = ?")) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Delete member
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Member WHERE memberID = ?")) {
                pstmt.setInt(1, memberId);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new RepositoryException("Error deleting coach", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    // Log autocommit reset error
                }
            }
        }
    }
    
    @Override
    public boolean assignToSquad(int coachId, int squadId) {
        String sql = "INSERT INTO CoachSquad (coachID, squadID) VALUES (?, ?)";
        
        try (Connection conn = connectionManager.getConnection()) {
            // Check if the relationship already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT 1 FROM CoachSquad WHERE coachID = ? AND squadID = ?")) {
                checkStmt.setInt(1, coachId);
                checkStmt.setInt(2, squadId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Relationship already exists
                    return true;
                }
            }
            
            // Create the relationship
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, coachId);
                pstmt.setInt(2, squadId);
                
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error assigning coach to squad", e);
        }
    }
    
    @Override
    public boolean removeFromSquad(int coachId, int squadId) {
        String sql = "DELETE FROM CoachSquad WHERE coachID = ? AND squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, coachId);
            pstmt.setInt(2, squadId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error removing coach from squad", e);
        }
    }
    
    @Override
    public List<Coach> findByQualification(String qualification) {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT c.*, m.* FROM Coach c " +
                     "JOIN Member m ON c.memberID = m.memberID " +
                     "WHERE c.qualifications LIKE ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + qualification + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Coach coach = mapResultSetToCoach(rs);
                coaches.add(coach);
            }
            
            // Load assigned squads for each coach
            for (Coach coach : coaches) {
                coach.setAssignedSquads(getAssignedSquads(conn, coach.getCoachId()));
            }
            
            return coaches;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding coaches by qualification: " + qualification, e);
        }
    }
    
    // Helper method to map ResultSet to Coach object
    private Coach mapResultSetToCoach(ResultSet rs) throws SQLException {
        Coach coach = new Coach();
        
        // Set coach fields
        coach.setCoachId(rs.getInt("coachID"));
        coach.setQualifications(rs.getString("qualifications"));
        
        // Set member fields
        coach.setMemberId(rs.getInt("memberID"));
        coach.setFirstName(rs.getString("firstName"));
        coach.setLastName(rs.getString("lastName"));
        
        // Parse date from string
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = sdf.parse(rs.getString("dob"));
            coach.setDateOfBirth(dob);
        } catch (ParseException e) {
            throw new SQLException("Error parsing date of birth", e);
        }
        
        coach.setEmail(rs.getString("email"));
        coach.setPhone(rs.getString("phone"));
        coach.setAddress(rs.getString("address"));
        
        return coach;
    }
    
    // Helper method to get squads assigned to a coach
    private List<Squad> getAssignedSquads(Connection conn, int coachId) throws SQLException {
        List<Squad> squads = new ArrayList<>();
        String sql = "SELECT s.* FROM Squad s " +
                     "JOIN CoachSquad cs ON s.squadID = cs.squadID " +
                     "WHERE cs.coachID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, coachId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Squad squad = new Squad();
                squad.setSquadId(rs.getInt("squadID"));
                squad.setSquadName(rs.getString("squadName"));
                squad.setAgeGrade(rs.getString("ageGrade"));
                squads.add(squad);
            }
        }
        
        return squads;
    }
    
    // Helper method to save assigned squads
    private void saveAssignedSquads(Connection conn, Coach coach) throws SQLException {
        // First delete existing assignments
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM CoachSquad WHERE coachID = ?")) {
            pstmt.setInt(1, coach.getCoachId());
            pstmt.executeUpdate();
        }
        
        // Then add new assignments
        if (coach.getAssignedSquads() != null && !coach.getAssignedSquads().isEmpty()) {
            String sql = "INSERT INTO CoachSquad (coachID, squadID) VALUES (?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Squad squad : coach.getAssignedSquads()) {
                    pstmt.setInt(1, coach.getCoachId());
                    pstmt.setInt(2, squad.getSquadId());
                    pstmt.executeUpdate();
                }
            }
        }
    }
    
    // Helper method to update assigned squads
    private void updateAssignedSquads(Connection conn, Coach coach) throws SQLException {
        saveAssignedSquads(conn, coach);
    }
    
    // Helper method to insert a new member
    private int insertMember(Connection conn, com.simplyrugby.domain.Member member) throws SQLException {
        String sql = "INSERT INTO Member (firstName, lastName, dob, email, phone, address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(member.getDateOfBirth()));
            
            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getPhone());
            pstmt.setString(6, member.getAddress());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            throw new SQLException("Creating member failed, no ID generated.");
        }
    }
    
    // Helper method to update an existing member
    private void updateMember(Connection conn, com.simplyrugby.domain.Member member) throws SQLException {
        String sql = "UPDATE Member SET firstName = ?, lastName = ?, dob = ?, " +
                     "email = ?, phone = ?, address = ? WHERE memberID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(member.getDateOfBirth()));
            
            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getPhone());
            pstmt.setString(6, member.getAddress());
            pstmt.setInt(7, member.getMemberId());
            
            pstmt.executeUpdate();
        }
    }
}