package com.simplyrugby.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a squad (team) in the rugby club.
 */
public class Squad {
    private int squadId;
    private String squadName;
    private String ageGrade;
    private List<Player> players = new ArrayList<>();
    private List<Coach> coaches = new ArrayList<>();

    /**
     * Default constructor
     */
    public Squad() {
    }

    /**
     * Constructor with all fields
     */
    public Squad(int squadId, String squadName, String ageGrade) {
        this.squadId = squadId;
        this.squadName = squadName;
        this.ageGrade = ageGrade;
    }

    // Getters and Setters

    public int getSquadId() {
        return squadId;
    }

    public void setSquadId(int squadId) {
        this.squadId = squadId;
    }

    public String getSquadName() {
        return squadName;
    }

    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    public String getAgeGrade() {
        return ageGrade;
    }

    public void setAgeGrade(String ageGrade) {
        this.ageGrade = ageGrade;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players != null ? players : new ArrayList<>();
    }

    public List<Coach> getCoaches() {
        return coaches;
    }

    public void setCoaches(List<Coach> coaches) {
        this.coaches = coaches != null ? coaches : new ArrayList<>();
    }

    /**
     * Adds a player to the squad
     * 
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }

        // Check if player already in squad
        for (Player p : players) {
            if (p.getPlayerId() == player.getPlayerId()) {
                return; // Already in squad
            }
        }

        this.players.add(player);
        player.setSquad(this);
    }

    /**
     * Removes a player from the squad
     * 
     * @param playerId The ID of the player to remove
     * @return true if the player was removed, false otherwise
     */
    public boolean removePlayer(int playerId) {
        if (this.players == null) {
            return false;
        }

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerId() == playerId) {
                Player player = players.get(i);
                players.remove(i);
                player.setSquad(null);
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a coach to the squad
     * 
     * @param coach The coach to add
     */
    public void addCoach(Coach coach) {
        if (this.coaches == null) {
            this.coaches = new ArrayList<>();
        }

        // Check if coach already assigned
        for (Coach c : coaches) {
            if (c.getCoachId() == coach.getCoachId()) {
                return; // Already assigned
            }
        }

        this.coaches.add(coach);
        coach.assignSquad(this);
    }

    /**
     * Removes a coach from the squad
     * 
     * @param coachId The ID of the coach to remove
     * @return true if the coach was removed, false otherwise
     */
    public boolean removeCoach(int coachId) {
        if (this.coaches == null) {
            return false;
        }

        for (int i = 0; i < coaches.size(); i++) {
            if (coaches.get(i).getCoachId() == coachId) {
                Coach coach = coaches.get(i);
                coaches.remove(i);
                coach.removeSquad(squadId);
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the number of players in the squad
     * 
     * @return The number of players
     */
    public int getSquadSize() {
        return players != null ? players.size() : 0;
    }

    /**
     * Checks if the squad has the minimum required number of coaches
     * 
     * @return true if the squad has enough coaches, false otherwise
     */
    public boolean hasMinimumCoaches() {
        if (coaches == null) {
            return false;
        }

        // Mini and midi teams need at least 2 coaches
        if (isJuniorSquad() && !"U18".equals(ageGrade)) {
            return coaches.size() >= 2;
        }

        // Senior teams need at least 3 coaches
        return coaches.size() >= 3;
    }

    /**
     * Checks if this is a junior squad (U8-U18)
     * 
     * @return true if this is a junior squad, false otherwise
     */
    public boolean isJuniorSquad() {
        return ageGrade != null && ageGrade.startsWith("U");
    }

    /**
     * Gets the descriptive name of the squad
     * 
     * @return Squad name with age grade
     */
    public String getFullSquadName() {
        return squadName + " (" + ageGrade + ")";
    }

    @Override
    public String toString() {
        return "Squad{" +
                "squadId=" + squadId +
                ", squadName='" + squadName + '\'' +
                ", ageGrade='" + ageGrade + '\'' +
                ", players=" + (players != null ? players.size() : 0) +
                ", coaches=" + (coaches != null ? coaches.size() : 0) +
                '}';
    }
}