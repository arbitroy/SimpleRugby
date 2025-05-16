package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.repository.SquadRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteSquadRepository implements SquadRepository {
    private final ConnectionManager connectionManager;
    
    public SQLiteSquadRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public Squad findById(int id) {
        String sql = "SELECT * FROM Squad WHERE squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Squad squad = mapResultSetToSquad(rs);
                
                // Load players
                squad.setPlayers(getPlayersInSquad(conn, id));
                
                // Load coaches
                squad.setCoaches(getCoachesForSquad(conn, id));
                
                return squad;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding squad with ID: " + id, e);
        }
    }
    
    @Override
    public List<Squad> findAll() {
        List<Squad> squads = new ArrayList<>();
        String sql = "SELECT * FROM Squad";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Squad squad = mapResultSetToSquad(rs);
                squads.add(squad);
            }
            
            // Load players and coaches for each squad
            for (Squad squad : squads) {
                squad.setPlayers(getPlayersInSquad(conn, squad.getSquadId()));
                squad.setCoaches(getCoachesForSquad(conn, squad.getSquadId()));
            }
            
            return squads;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all squads", e);
        }
    }
    
    @Override
    public List<Squad> findByName(String name) {
        List<Squad> squads = new ArrayList<>();
        String sql = "SELECT * FROM Squad WHERE squadName LIKE ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Squad squad = mapResultSetToSquad(rs);
                squads.add(squad);
            }
            
            // Load players and coaches for each squad
            for (Squad squad : squads) {
                squad.setPlayers(getPlayersInSquad(conn, squad.getSquadId()));
                squad.setCoaches(getCoachesForSquad(conn, squad.getSquadId()));
            }
            
            return squads;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding squads by name: " + name, e);
        }
    }
    
    @Override
    public List<Squad> findByAgeGrade(String ageGrade) {
        List<Squad> squads = new ArrayList<>();
        String sql = "SELECT * FROM Squad WHERE ageGrade = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ageGrade);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Squad squad = mapResultSetToSquad(rs);
                squads.add(squad);
            }
            
            // Load players and coaches for each squad
            for (Squad squad : squads) {
                squad.setPlayers(getPlayersInSquad(conn, squad.getSquadId()));
                squad.setCoaches(getCoachesForSquad(conn, squad.getSquadId()));
            }
            
            return squads;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding squads by age grade: " + ageGrade, e);
        }
    }
    
    @Override
    public List<Squad> findByCoach(int coachId) {
        List<Squad> squads = new ArrayList<>();
        String sql = "SELECT s.* FROM Squad s " +
                     "JOIN CoachSquad cs ON s.squadID = cs.squadID " +
                     "WHERE cs.coachID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, coachId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Squad squad = mapResultSetToSquad(rs);
                squads.add(squad);
            }
            
            // Load players and coaches for each squad
            for (Squad squad : squads) {
                squad.setPlayers(getPlayersInSquad(conn, squad.getSquadId()));
                squad.setCoaches(getCoachesForSquad(conn, squad.getSquadId()));
            }
            
            return squads;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding squads by coach: " + coachId, e);
        }
    }
    
    @Override
    public Squad findByPlayer(int playerId) {
        String sql = "SELECT s.* FROM Squad s " +
                     "JOIN Player p ON s.squadID = p.squadID " +
                     "WHERE p.playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Squad squad = mapResultSetToSquad(rs);
                
                // Load players
                squad.setPlayers(getPlayersInSquad(conn, squad.getSquadId()));
                
                // Load coaches
                squad.setCoaches(getCoachesForSquad(conn, squad.getSquadId()));
                
                return squad;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding squad by player: " + playerId, e);
        }
    }
    
    @Override
    public int save(Squad squad) {
        String sql = "INSERT INTO Squad (squadName, ageGrade) VALUES (?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, squad.getSquadName());
            pstmt.setString(2, squad.getAgeGrade());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int squadId = generatedKeys.getInt(1);
                        squad.setSquadId(squadId);
                        return squadId;
                    }
                }
            }
            
            throw new RepositoryException("Creating squad failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RepositoryException("Error saving squad", e);
        }
    }
    
    @Override
    public boolean update(Squad squad) {
        String sql = "UPDATE Squad SET squadName = ?, ageGrade = ? WHERE squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, squad.getSquadName());
            pstmt.setString(2, squad.getAgeGrade());
            pstmt.setInt(3, squad.getSquadId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating squad", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // Remove squad from players
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Player SET squadID = NULL WHERE squadID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete coach squad assignments
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM CoachSquad WHERE squadID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete games associated with squad
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Game WHERE squadID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete training sessions associated with squad
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Training WHERE squadID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete squad
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Squad WHERE squadID = ?")) {
                pstmt.setInt(1, id);
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
            throw new RepositoryException("Error deleting squad", e);
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
    public boolean addPlayer(int squadId, int playerId) {
        String sql = "UPDATE Player SET squadID = ? WHERE playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            pstmt.setInt(2, playerId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error adding player to squad", e);
        }
    }
    
    @Override
    public boolean removePlayer(int squadId, int playerId) {
        String sql = "UPDATE Player SET squadID = NULL WHERE playerID = ? AND squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, squadId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error removing player from squad", e);
        }
    }
    
    @Override
    public boolean addCoach(int squadId, int coachId) {
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
            throw new RepositoryException("Error adding coach to squad", e);
        }
    }
    
    @Override
    public boolean removeCoach(int squadId, int coachId) {
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
    public int getPlayerCount(int squadId) {
        String sql = "SELECT COUNT(*) FROM Player WHERE squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting player count for squad", e);
        }
    }
    
    @Override
    public int getCoachCount(int squadId) {
        String sql = "SELECT COUNT(*) FROM CoachSquad WHERE squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting coach count for squad", e);
        }
    }
    
    // Helper method to map ResultSet to Squad object
    private Squad mapResultSetToSquad(ResultSet rs) throws SQLException {
        Squad squad = new Squad();
        squad.setSquadId(rs.getInt("squadID"));
        squad.setSquadName(rs.getString("squadName"));
        squad.setAgeGrade(rs.getString("ageGrade"));
        return squad;
    }
    
    // Helper method to get players in a squad
    private List<Player> getPlayersInSquad(Connection conn, int squadId) throws SQLException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE p.squadID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = new Player();
                
                // Set player fields
                player.setPlayerId(rs.getInt("playerID"));
                player.setPosition(rs.getString("position"));
                player.setMedicalConditions(rs.getString("medicalConditions"));
                
                // Set member fields
                player.setMemberId(rs.getInt("memberID"));
                player.setFirstName(rs.getString("firstName"));
                player.setLastName(rs.getString("lastName"));
                
                // Parse date from string
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dob = sdf.parse(rs.getString("dob"));
                    player.setDateOfBirth(dob);
                } catch (ParseException e) {
                    throw new SQLException("Error parsing date of birth", e);
                }
                
                player.setEmail(rs.getString("email"));
                player.setPhone(rs.getString("phone"));
                player.setAddress(rs.getString("address"));
                
                // Set emergency contact if available
                int ecId = rs.getInt("ecID");
                if (!rs.wasNull()) {
                    com.simplyrugby.domain.Member emergencyContact = new com.simplyrugby.domain.Member();
                    emergencyContact.setMemberId(ecId);
                    emergencyContact.setFirstName(rs.getString("ecFirstName"));
                    emergencyContact.setLastName(rs.getString("ecLastName"));
                    emergencyContact.setEmail(rs.getString("ecEmail"));
                    emergencyContact.setPhone(rs.getString("ecPhone"));
                    player.setEmergencyContact(emergencyContact);
                }
                
                players.add(player);
            }
        }
        
        return players;
    }
    
    // Helper method to get coaches for a squad
    private List<Coach> getCoachesForSquad(Connection conn, int squadId) throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT c.*, m.* " +
                     "FROM Coach c " +
                     "JOIN Member m ON c.memberID = m.memberID " +
                     "JOIN CoachSquad cs ON c.coachID = cs.coachID " +
                     "WHERE cs.squadID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
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
                
                coaches.add(coach);
            }
        }
        
        return coaches;
    }
}