package com.simplyrugby.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a player in the rugby club.
 * Extends the Member class to include player-specific information.
 */
public class Player extends Member {
    private int playerId;
    private String position;
    private Squad squad;
    private Member emergencyContact;
    private String medicalConditions;
    private List<GameStats> playerStats = new ArrayList<>();
    private List<TrainingAttendance> trainingAttendance = new ArrayList<>();
    
    /**
     * Default constructor
     */
    public Player() {
        super();
    }
    
    /**
     * Constructor with member fields
     */
    public Player(int memberId, String firstName, String lastName, Date dateOfBirth, 
                 String email, String phone, String address) {
        super(memberId, firstName, lastName, dateOfBirth, email, phone, address);
    }
    
    /**
     * Constructor with all fields
     */
    public Player(int memberId, String firstName, String lastName, Date dateOfBirth, 
                 String email, String phone, String address, int playerId, String position, 
                 Squad squad, Member emergencyContact, String medicalConditions) {
        super(memberId, firstName, lastName, dateOfBirth, email, phone, address);
        this.playerId = playerId;
        this.position = position;
        this.squad = squad;
        this.emergencyContact = emergencyContact;
        this.medicalConditions = medicalConditions;
    }
    
    // Getters and Setters
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Squad getSquad() {
        return squad;
    }
    
    public void setSquad(Squad squad) {
        this.squad = squad;
    }
    
    public Member getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(Member emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getMedicalConditions() {
        return medicalConditions;
    }
    
    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }
    
    public List<GameStats> getPlayerStats() {
        return playerStats;
    }
    
    public void setPlayerStats(List<GameStats> playerStats) {
        this.playerStats = playerStats != null ? playerStats : new ArrayList<>();
    }
    
    public List<TrainingAttendance> getTrainingAttendance() {
        return trainingAttendance;
    }
    
    public void setTrainingAttendance(List<TrainingAttendance> trainingAttendance) {
        this.trainingAttendance = trainingAttendance != null ? trainingAttendance : new ArrayList<>();
    }
    
    /**
     * Adds game statistics for this player
     * 
     * @param stats The game statistics to add
     */
    public void addGameStats(GameStats stats) {
        if (this.playerStats == null) {
            this.playerStats = new ArrayList<>();
        }
        this.playerStats.add(stats);
    }
    
    /**
     * Adds training attendance for this player
     * 
     * @param attendance The training attendance to add
     */
    public void addTrainingAttendance(TrainingAttendance attendance) {
        if (this.trainingAttendance == null) {
            this.trainingAttendance = new ArrayList<>();
        }
        this.trainingAttendance.add(attendance);
    }
    
    /**
     * Gets game statistics for a specific game
     * 
     * @param gameId The ID of the game
     * @return The game statistics, or null if not found
     */
    public GameStats getGameStatsForGame(int gameId) {
        if (playerStats == null) {
            return null;
        }
        
        for (GameStats stats : playerStats) {
            if (stats.getGameId() == gameId) {
                return stats;
            }
        }
        
        return null;
    }
    
    /**
     * Calculates the overall skill rating based on all game performances
     * 
     * @return The average overall rating across all games
     */
    public double calculateOverallSkillRating() {
        if (playerStats == null || playerStats.isEmpty()) {
            return 0.0;
        }
        
        int totalRating = 0;
        int gamesPlayed = 0;
        
        for (GameStats stats : playerStats) {
            if (stats.isAttended()) {
                totalRating += stats.getOverallRating();
                gamesPlayed++;
            }
        }
        
        return gamesPlayed > 0 ? (double) totalRating / gamesPlayed : 0.0;
    }
    
    /**
     * Calculates the attendance rate for training sessions
     * 
     * @return The attendance rate as a percentage
     */
    public double calculateTrainingAttendanceRate() {
        if (trainingAttendance == null || trainingAttendance.isEmpty()) {
            return 0.0;
        }
        
        int attended = 0;
        
        for (TrainingAttendance attendance : trainingAttendance) {
            if (attendance.isPresent()) {
                attended++;
            }
        }
        
        return (double) attended / trainingAttendance.size() * 100.0;
    }
    
    /**
     * Gets the number of games attended
     * 
     * @return The number of games attended
     */
    public int getGamesAttended() {
        if (playerStats == null) {
            return 0;
        }
        
        int count = 0;
        for (GameStats stats : playerStats) {
            if (stats.isAttended()) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public String toString() {
        return "Player{" + 
               "playerId=" + playerId + 
               ", position='" + position + '\'' + 
               ", squad=" + (squad != null ? squad.getSquadName() : "None") + 
               ", medicalConditions='" + medicalConditions + '\'' + 
               ", memberInfo=" + super.toString() + 
               '}';
    }
}