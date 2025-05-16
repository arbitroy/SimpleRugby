package com.simplyrugby.domain;

import java.util.Date;

/**
 * Represents a player's attendance record for a specific training session.
 */
public class TrainingAttendance {
    private int attendanceId;
    private int playerId;
    private int trainingId;
    private Date trainingDate;
    private boolean present;
    private String playerNotes;

    /**
     * Default constructor
     */
    public TrainingAttendance() {
        this.present = false;
        this.playerNotes = "";
    }

    /**
     * Constructor with all fields
     */
    public TrainingAttendance(int attendanceId, int playerId, int trainingId,
            Date trainingDate, boolean present, String playerNotes) {
        this.attendanceId = attendanceId;
        this.playerId = playerId;
        this.trainingId = trainingId;
        this.trainingDate = trainingDate;
        this.present = present;
        this.playerNotes = playerNotes;
    }

    // Getters and Setters

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getPlayerNotes() {
        return playerNotes;
    }

    public void setPlayerNotes(String playerNotes) {
        this.playerNotes = playerNotes;
    }

    /**
     * Marks the player as present for this training session
     */
    public void markAsPresent() {
        this.present = true;
    }

    /**
     * Marks the player as absent for this training session
     */
    public void markAsAbsent() {
        this.present = false;
    }

    @Override
    public String toString() {
        return "TrainingAttendance{" +
                "attendanceId=" + attendanceId +
                ", playerId=" + playerId +
                ", trainingId=" + trainingId +
                ", trainingDate=" + trainingDate +
                ", present=" + present +
                ", notes='" + (playerNotes != null ? playerNotes : "") + '\'' +
                '}';
    }
}