package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.domain.GameStats;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.repository.PlayerRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLitePlayerRepository implements PlayerRepository {
    private final ConnectionManager connectionManager;
    
    public SQLitePlayerRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public Player findById(int id) {
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE p.playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                
                // Load player stats
                player.setPlayerStats(getPlayerStats(conn, id));
                
                // Load training attendance
                player.setTrainingAttendance(getPlayerTrainingAttendance(conn, id));
                
                return player;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding player with ID: " + id, e);
        }
    }
    
    @Override
    public Player findByMemberId(int memberId) {
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE p.memberID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                
                // Load player stats
                player.setPlayerStats(getPlayerStats(conn, player.getPlayerId()));
                
                // Load training attendance
                player.setTrainingAttendance(getPlayerTrainingAttendance(conn, player.getPlayerId()));
                
                return player;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding player with member ID: " + memberId, e);
        }
    }
    
    @Override
    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }
            
            // Load stats and attendance for each player
            for (Player player : players) {
                player.setPlayerStats(getPlayerStats(conn, player.getPlayerId()));
                player.setTrainingAttendance(getPlayerTrainingAttendance(conn, player.getPlayerId()));
            }
            
            return players;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all players", e);
        }
    }
    
    @Override
    public List<Player> findByName(String name) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE m.firstName LIKE ? OR m.lastName LIKE ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + name + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }
            
            // Load stats and attendance for each player
            for (Player player : players) {
                player.setPlayerStats(getPlayerStats(conn, player.getPlayerId()));
                player.setTrainingAttendance(getPlayerTrainingAttendance(conn, player.getPlayerId()));
            }
            
            return players;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding players by name: " + name, e);
        }
    }
    
    @Override
    public List<Player> findBySquad(int squadId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE p.squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }
            
            // Load stats and attendance for each player
            for (Player player : players) {
                player.setPlayerStats(getPlayerStats(conn, player.getPlayerId()));
                player.setTrainingAttendance(getPlayerTrainingAttendance(conn, player.getPlayerId()));
            }
            
            return players;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding players by squad: " + squadId, e);
        }
    }
    
    @Override
    public List<Player> findByPosition(String position) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE p.position = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, position);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }
            
            // Load stats and attendance for each player
            for (Player player : players) {
                player.setPlayerStats(getPlayerStats(conn, player.getPlayerId()));
                player.setTrainingAttendance(getPlayerTrainingAttendance(conn, player.getPlayerId()));
            }
            
            return players;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding players by position: " + position, e);
        }
    }
    
    @Override
    public List<Player> findByAgeGrade(String ageGrade) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone " +
                     "FROM Player p " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE s.ageGrade = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ageGrade);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }
            
            // Load stats and attendance for each player
            for (Player player : players) {
                player.setPlayerStats(getPlayerStats(conn, player.getPlayerId()));
                player.setTrainingAttendance(getPlayerTrainingAttendance(conn, player.getPlayerId()));
            }
            
            return players;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding players by age grade: " + ageGrade, e);
        }
    }
    
    @Override
    public int save(Player player) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // First save or update the member
            int memberId;
            if (player.getMemberId() > 0) {
                // Update existing member
                updateMember(conn, player);
                memberId = player.getMemberId();
            } else {
                // Insert new member
                memberId = insertMember(conn, player);
                player.setMemberId(memberId);
            }
            
            // Now save the player
            String sql = "INSERT INTO Player (memberID, position, squadID, emergencyContactID, medicalConditions) " +
                         "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, memberId);
                pstmt.setString(2, player.getPosition());
                
                if (player.getSquad() != null) {
                    pstmt.setInt(3, player.getSquad().getSquadId());
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }
                
                if (player.getEmergencyContact() != null) {
                    pstmt.setInt(4, player.getEmergencyContact().getMemberId());
                } else {
                    pstmt.setNull(4, Types.INTEGER);
                }
                
                pstmt.setString(5, player.getMedicalConditions());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int playerId = generatedKeys.getInt(1);
                            player.setPlayerId(playerId);
                            conn.commit();
                            return playerId;
                        }
                    }
                }
                
                conn.rollback();
                throw new SQLException("Creating player failed, no ID generated.");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new RepositoryException("Error saving player", e);
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
    public boolean update(Player player) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // Update member details
            updateMember(conn, player);
            
            // Update player details
            String sql = "UPDATE Player SET position = ?, squadID = ?, emergencyContactID = ?, " +
                         "medicalConditions = ? WHERE playerID = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, player.getPosition());
                
                if (player.getSquad() != null) {
                    pstmt.setInt(2, player.getSquad().getSquadId());
                } else {
                    pstmt.setNull(2, Types.INTEGER);
                }
                
                if (player.getEmergencyContact() != null) {
                    pstmt.setInt(3, player.getEmergencyContact().getMemberId());
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }
                
                pstmt.setString(4, player.getMedicalConditions());
                pstmt.setInt(5, player.getPlayerId());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
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
            throw new RepositoryException("Error updating player", e);
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
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT memberID FROM Player WHERE playerID = ?")) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    memberId = rs.getInt("memberID");
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            // Delete game stats for player
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM GameStats WHERE playerID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete training attendance for player
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM TrainingAttendance WHERE playerID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete player
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Player WHERE playerID = ?")) {
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
            throw new RepositoryException("Error deleting player", e);
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
    public boolean assignToSquad(int playerId, int squadId) {
        String sql = "UPDATE Player SET squadID = ? WHERE playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            pstmt.setInt(2, playerId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error assigning player to squad", e);
        }
    }
    
    @Override
    public boolean removeFromSquad(int playerId) {
        String sql = "UPDATE Player SET squadID = NULL WHERE playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error removing player from squad", e);
        }
    }
    
    @Override
    public boolean setEmergencyContact(int playerId, int emergencyContactId) {
        String sql = "UPDATE Player SET emergencyContactID = ? WHERE playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, emergencyContactId);
            pstmt.setInt(2, playerId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error setting emergency contact", e);
        }
    }
    
   @Override
    public List<Player> findPlayersWithStatsByGame(int gameId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone, " +
                     "gs.* " +
                     "FROM GameStats gs " +
                     "JOIN Player p ON gs.playerID = p.playerID " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE gs.gameID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                
                // Create game stats
                GameStats stats = new GameStats();
                stats.setGameStatsId(rs.getInt("gameStatsID"));
                stats.setPlayerId(rs.getInt("playerID"));
                stats.setGameId(rs.getInt("gameID"));
                stats.setTackles(rs.getInt("tackles"));
                stats.setPasses(rs.getInt("passes"));
                stats.setTries(rs.getInt("tries"));
                stats.setKicks(rs.getInt("kicks"));
                stats.setOverallRating(rs.getInt("overallRating"));
                stats.setAttended(rs.getBoolean("attended"));
                
                // Add game stats to player
                player.addGameStats(stats);
                
                players.add(player);
            }
            
            return players;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding players with stats for game: " + gameId, e);
        }
    }
    
    @Override
    public List<Player> findPlayersWithAttendanceByTraining(int trainingId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT p.*, m.*, s.squadID, s.squadName, s.ageGrade, " +
                     "ec.memberID as ecID, ec.firstName as ecFirstName, ec.lastName as ecLastName, " +
                     "ec.email as ecEmail, ec.phone as ecPhone, " +
                     "ta.* " +
                     "FROM TrainingAttendance ta " +
                     "JOIN Player p ON ta.playerID = p.playerID " +
                     "JOIN Member m ON p.memberID = m.memberID " +
                     "LEFT JOIN Squad s ON p.squadID = s.squadID " +
                     "LEFT JOIN Member ec ON p.emergencyContactID = ec.memberID " +
                     "WHERE ta.trainingID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trainingId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                
                // Create training attendance
                TrainingAttendance attendance = new TrainingAttendance();
                attendance.setAttendanceId(rs.getInt("attendanceID"));
                attendance.setPlayerId(rs.getInt("playerID"));
                attendance.setTrainingId(rs.getInt("trainingID"));
                attendance.setPresent(rs.getBoolean("present"));
                attendance.setPlayerNotes(rs.getString("playerNotes"));
                
                // Add training attendance to player
                player.addTrainingAttendance(attendance);
                
                players.add(player);
            }
            
            return players;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding players with attendance for training: " + trainingId, e);
        }
    }
    
    // Helper method to map ResultSet to Player object
    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
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
        
        // Set squad if available
        int squadId = rs.getInt("squadID");
        if (!rs.wasNull()) {
            Squad squad = new Squad();
            squad.setSquadId(squadId);
            squad.setSquadName(rs.getString("squadName"));
            squad.setAgeGrade(rs.getString("ageGrade"));
            player.setSquad(squad);
        }
        
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
        
        return player;
    }
    
    // Helper method to get player stats
    private List<GameStats> getPlayerStats(Connection conn, int playerId) throws SQLException {
        List<GameStats> stats = new ArrayList<>();
        String sql = "SELECT * FROM GameStats WHERE playerID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GameStats gameStats = new GameStats();
                gameStats.setGameStatsId(rs.getInt("gameStatsID"));
                gameStats.setPlayerId(rs.getInt("playerID"));
                gameStats.setGameId(rs.getInt("gameID"));
                gameStats.setTackles(rs.getInt("tackles"));
                gameStats.setPasses(rs.getInt("passes"));
                gameStats.setTries(rs.getInt("tries"));
                gameStats.setKicks(rs.getInt("kicks"));
                gameStats.setOverallRating(rs.getInt("overallRating"));
                gameStats.setAttended(rs.getBoolean("attended"));
                
                stats.add(gameStats);
            }
        }
        
        return stats;
    }
    
    // Helper method to get player training attendance
    private List<TrainingAttendance> getPlayerTrainingAttendance(Connection conn, int playerId) throws SQLException {
        List<TrainingAttendance> attendance = new ArrayList<>();
        String sql = "SELECT ta.*, t.date FROM TrainingAttendance ta " +
                     "JOIN Training t ON ta.trainingID = t.trainingID " +
                     "WHERE ta.playerID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TrainingAttendance ta = new TrainingAttendance();
                ta.setAttendanceId(rs.getInt("attendanceID"));
                ta.setPlayerId(rs.getInt("playerID"));
                ta.setTrainingId(rs.getInt("trainingID"));
                ta.setPresent(rs.getBoolean("present"));
                ta.setPlayerNotes(rs.getString("playerNotes"));
                
                // Set training date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date trainingDate = sdf.parse(rs.getString("date"));
                    ta.setTrainingDate(trainingDate);
                } catch (ParseException e) {
                    throw new SQLException("Error parsing training date", e);
                }
                
                attendance.add(ta);
            }
        }
        
        return attendance;
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