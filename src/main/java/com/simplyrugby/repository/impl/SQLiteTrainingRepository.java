package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Training;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.repository.TrainingRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteTrainingRepository implements TrainingRepository {
    private final ConnectionManager connectionManager;
    
    public SQLiteTrainingRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public Training findById(int id) {
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.trainingID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                
                // Load attendance records
                training.setAttendanceRecords(getAttendanceRecords(conn, id));
                
                return training;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding training with ID: " + id, e);
        }
    }
    
    @Override
    public List<Training> findAll() {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all trainings", e);
        }
    }
    
    @Override
    public List<Training> findBySquad(int squadId) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding trainings by squad: " + squadId, e);
        }
    }
    
    @Override
    public List<Training> findTrainingAfterDate(Date date) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.date > ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(date));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding trainings after date", e);
        }
    }
    
    @Override
    public List<Training> findTrainingBeforeDate(Date date) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.date < ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(date));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding trainings before date", e);
        }
    }
    
    @Override
    public List<Training> findTrainingBetweenDates(Date startDate, Date endDate) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.date BETWEEN ? AND ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(startDate));
            pstmt.setString(2, sdf.format(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding trainings between dates", e);
        }
    }
    
    @Override
    public int save(Training training) {
        String sql = "INSERT INTO Training (date, squadID, focusAreas, coachNotes) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(training.getDate()));
            
            if (training.getSquad() != null) {
                pstmt.setInt(2, training.getSquad().getSquadId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setString(3, training.getFocusAreas());
            pstmt.setString(4, training.getCoachNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int trainingId = generatedKeys.getInt(1);
                        training.setTrainingId(trainingId);
                        return trainingId;
                    }
                }
            }
            
            throw new RepositoryException("Creating training failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RepositoryException("Error saving training", e);
        }
    }
    
    @Override
    public boolean update(Training training) {
        String sql = "UPDATE Training SET date = ?, squadID = ?, focusAreas = ?, coachNotes = ? " +
                     "WHERE trainingID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(training.getDate()));
            
            if (training.getSquad() != null) {
                pstmt.setInt(2, training.getSquad().getSquadId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setString(3, training.getFocusAreas());
            pstmt.setString(4, training.getCoachNotes());
            pstmt.setInt(5, training.getTrainingId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating training", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // Delete attendance records
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM TrainingAttendance WHERE trainingID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete training
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Training WHERE trainingID = ?")) {
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
            throw new RepositoryException("Error deleting training", e);
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
    public boolean addAttendance(TrainingAttendance attendance) {
        String sql = "INSERT INTO TrainingAttendance (playerID, trainingID, present, playerNotes) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, attendance.getPlayerId());
            pstmt.setInt(2, attendance.getTrainingId());
            pstmt.setBoolean(3, attendance.isPresent());
            pstmt.setString(4, attendance.getPlayerNotes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        attendance.setAttendanceId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            throw new RepositoryException("Error adding attendance record", e);
        }
    }
    
    @Override
    public boolean updateAttendance(TrainingAttendance attendance) {
        String sql = "UPDATE TrainingAttendance SET present = ?, playerNotes = ? " +
                     "WHERE attendanceID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, attendance.isPresent());
            pstmt.setString(2, attendance.getPlayerNotes());
            pstmt.setInt(3, attendance.getAttendanceId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating attendance record", e);
        }
    }
    
    @Override
    public List<TrainingAttendance> getAttendanceRecords(int trainingId) {
        List<TrainingAttendance> records = new ArrayList<>();
        String sql = "SELECT ta.*, t.date FROM TrainingAttendance ta " +
                     "JOIN Training t ON ta.trainingID = t.trainingID " +
                     "WHERE ta.trainingID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trainingId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TrainingAttendance attendance = new TrainingAttendance();
                attendance.setAttendanceId(rs.getInt("attendanceID"));
                attendance.setPlayerId(rs.getInt("playerID"));
                attendance.setTrainingId(rs.getInt("trainingID"));
                attendance.setPresent(rs.getBoolean("present"));
                attendance.setPlayerNotes(rs.getString("playerNotes"));
                
                // Set training date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date trainingDate = sdf.parse(rs.getString("date"));
                    attendance.setTrainingDate(trainingDate);
                } catch (ParseException e) {
                    throw new SQLException("Error parsing training date", e);
                }
                
                records.add(attendance);
            }
            
            return records;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting attendance records", e);
        }
    }
    
    @Override
    public List<TrainingAttendance> getAttendanceByPlayer(int playerId) {
        List<TrainingAttendance> records = new ArrayList<>();
        String sql = "SELECT ta.*, t.date FROM TrainingAttendance ta " +
                     "JOIN Training t ON ta.trainingID = t.trainingID " +
                     "WHERE ta.playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TrainingAttendance attendance = new TrainingAttendance();
                attendance.setAttendanceId(rs.getInt("attendanceID"));
                attendance.setPlayerId(rs.getInt("playerID"));
                attendance.setTrainingId(rs.getInt("trainingID"));
                attendance.setPresent(rs.getBoolean("present"));
                attendance.setPlayerNotes(rs.getString("playerNotes"));
                
                // Set training date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date trainingDate = sdf.parse(rs.getString("date"));
                    attendance.setTrainingDate(trainingDate);
                } catch (ParseException e) {
                    throw new SQLException("Error parsing training date", e);
                }
                
                records.add(attendance);
            }
            
            return records;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting player attendance records", e);
        }
    }
    
    @Override
    public TrainingAttendance getPlayerAttendance(int trainingId, int playerId) {
        String sql = "SELECT ta.*, t.date FROM TrainingAttendance ta " +
                     "JOIN Training t ON ta.trainingID = t.trainingID " +
                     "WHERE ta.trainingID = ? AND ta.playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trainingId);
            pstmt.setInt(2, playerId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                TrainingAttendance attendance = new TrainingAttendance();
                attendance.setAttendanceId(rs.getInt("attendanceID"));
                attendance.setPlayerId(rs.getInt("playerID"));
                attendance.setTrainingId(rs.getInt("trainingID"));
                attendance.setPresent(rs.getBoolean("present"));
                attendance.setPlayerNotes(rs.getString("playerNotes"));
                
                // Set training date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date trainingDate = sdf.parse(rs.getString("date"));
                    attendance.setTrainingDate(trainingDate);
                } catch (ParseException e) {
                    throw new SQLException("Error parsing training date", e);
                }
                
                return attendance;
            }
            
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting player attendance record", e);
        }
    }
    
    @Override
    public List<Training> findUpcomingTraining() {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.date >= date('now') " +
                     "ORDER BY t.date ASC";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding upcoming trainings", e);
        }
    }
    
    @Override
    public List<Training> findRecentTraining(int limit) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.date < date('now') " +
                     "ORDER BY t.date DESC " +
                     "LIMIT ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding recent trainings", e);
        }
    }
    
    @Override
    public List<Training> findByFocusArea(String focusArea) {
        List<Training> trainings = new ArrayList<>();
        String sql = "SELECT t.*, s.squadName, s.ageGrade FROM Training t " +
                     "LEFT JOIN Squad s ON t.squadID = s.squadID " +
                     "WHERE t.focusAreas LIKE ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + focusArea + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Training training = mapResultSetToTraining(rs);
                trainings.add(training);
            }
            
            // Load attendance records for each training
            for (Training training : trainings) {
                training.setAttendanceRecords(getAttendanceRecords(conn, training.getTrainingId()));
            }
            
            return trainings;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding trainings by focus area", e);
        }
    }
    
    @Override
    public double getAttendanceRate(int trainingId) {
        String sql = "SELECT COUNT(*) as total, SUM(CASE WHEN present = 1 THEN 1 ELSE 0 END) as present " +
                     "FROM TrainingAttendance WHERE trainingID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trainingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                
                if (total > 0) {
                    return (double) present / total * 100.0;
                }
            }
            
            return 0.0;
        } catch (SQLException e) {
            throw new RepositoryException("Error calculating attendance rate", e);
        }
    }
    
    @Override
    public double getPlayerAttendanceRate(int playerId) {
        String sql = "SELECT COUNT(*) as total, SUM(CASE WHEN present = 1 THEN 1 ELSE 0 END) as present " +
                     "FROM TrainingAttendance WHERE playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                
                if (total > 0) {
                    return (double) present / total * 100.0;
                }
            }
            
            return 0.0;
        } catch (SQLException e) {
            throw new RepositoryException("Error calculating player attendance rate", e);
        }
    }
    
    // Helper method to map ResultSet to Training object
    private Training mapResultSetToTraining(ResultSet rs) throws SQLException {
        Training training = new Training();
        training.setTrainingId(rs.getInt("trainingID"));
        
        // Parse date from string
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date trainingDate = sdf.parse(rs.getString("date"));
            training.setDate(trainingDate);
        } catch (ParseException e) {
            throw new SQLException("Error parsing training date", e);
        }
        
        training.setFocusAreas(rs.getString("focusAreas"));
        training.setCoachNotes(rs.getString("coachNotes"));
        
        // Set squad if available
        int squadId = rs.getInt("squadID");
        if (!rs.wasNull()) {
            Squad squad = new Squad();
            squad.setSquadId(squadId);
            squad.setSquadName(rs.getString("squadName"));
            squad.setAgeGrade(rs.getString("ageGrade"));
            training.setSquad(squad);
        }
        
        return training;
    }
}