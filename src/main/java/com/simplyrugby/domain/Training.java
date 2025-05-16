package com.simplyrugby.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a training session for a squad.
 */
public class Training {
    private int trainingId;
    private Date date;
    private Squad squad;
    private String focusAreas;
    private String coachNotes;
    private List<TrainingAttendance> attendanceRecords = new ArrayList<>();
    
    /**
     * Default constructor
     */
    public Training() {
    }
    
    /**
     * Constructor with all fields
     */
    public Training(int trainingId, Date date, Squad squad, String focusAreas, String coachNotes) {
        this.trainingId = trainingId;
        this.date = date;
        this.squad = squad;
        this.focusAreas = focusAreas;
        this.coachNotes = coachNotes;
    }
    
    // Getters and Setters
    
    public int getTrainingId() {
        return trainingId;
    }
    
    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public Squad getSquad() {
        return squad;
    }
    
    public void setSquad(Squad squad) {
        this.squad = squad;
    }
    
    public String getFocusAreas() {
        return focusAreas;
    }
    
    public void setFocusAreas(String focusAreas) {
        this.focusAreas = focusAreas;
    }
    
    public String getCoachNotes() {
        return coachNotes;
    }
    
    public void setCoachNotes(String coachNotes) {
        this.coachNotes = coachNotes;
    }
    
    public List<TrainingAttendance> getAttendanceRecords() {
        return attendanceRecords;
    }
    
    public void setAttendanceRecords(List<TrainingAttendance> attendanceRecords) {
        this.attendanceRecords = attendanceRecords != null ? attendanceRecords : new ArrayList<>();
    }
    
    /**
     * Adds an attendance record for this training session
     * 
     * @param attendance The attendance record to add
     */
    public void addAttendanceRecord(TrainingAttendance attendance) {
        if (this.attendanceRecords == null) {
            this.attendanceRecords = new ArrayList<>();
        }
        
        // Check if player already has an attendance record
        for (int i = 0; i < attendanceRecords.size(); i++) {
            if (attendanceRecords.get(i).getPlayerId() == attendance.getPlayerId()) {
                attendanceRecords.set(i, attendance); // Replace existing record
                return;
            }
        }
        
        attendanceRecords.add(attendance);
    }
    
    /**
     * Gets the attendance record for a specific player
     * 
     * @param playerId The ID of the player
     * @return The attendance record, or null if not found
     */
    public TrainingAttendance getPlayerAttendance(int playerId) {
        if (attendanceRecords == null) {
            return null;
        }
        
        for (TrainingAttendance attendance : attendanceRecords) {
            if (attendance.getPlayerId() == playerId) {
                return attendance;
            }
        }
        
        return null;
    }
    
    /**
     * Gets the list of player IDs who attended the training session
     * 
     * @return List of player IDs who attended
     */
    public List<Integer> getAttendingPlayerIds() {
        List<Integer> attendingPlayers = new ArrayList<>();
        
        if (attendanceRecords == null) {
            return attendingPlayers;
        }
        
        for (TrainingAttendance attendance : attendanceRecords) {
            if (attendance.isPresent()) {
                attendingPlayers.add(attendance.getPlayerId());
            }
        }
        
        return attendingPlayers;
    }
    
    /**
     * Gets the attendance rate for this training session
     * 
     * @return The attendance rate as a percentage
     */
    public double getAttendanceRate() {
        if (attendanceRecords == null || attendanceRecords.isEmpty()) {
            return 0.0;
        }
        
        int present = 0;
        for (TrainingAttendance attendance : attendanceRecords) {
            if (attendance.isPresent()) {
                present++;
            }
        }
        
        return (double) present / attendanceRecords.size() * 100.0;
    }
    
    /**
     * Determines if this training session is in the future
     * 
     * @return true if the training session is upcoming, false otherwise
     */
    public boolean isUpcoming() {
        if (date == null) {
            return false;
        }
        
        return date.after(new Date());
    }
    
    @Override
    public String toString() {
        return "Training{" + 
               "trainingId=" + trainingId + 
               ", date=" + date + 
               ", squad=" + (squad != null ? squad.getSquadName() : "None") + 
               ", focusAreas='" + focusAreas + '\'' + 
               ", attendanceRecords=" + (attendanceRecords != null ? attendanceRecords.size() : 0) + 
               '}';
    }
}