package com.simplyrugby.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a coach in the rugby club.
 * Extends the Member class to include coach-specific information.
 */
public class Coach extends Member {
    private int coachId;
    private String qualifications;
    private List<Squad> assignedSquads = new ArrayList<>();

    /**
     * Default constructor
     */
    public Coach() {
        super();
    }

    /**
     * Constructor with member fields
     */
    public Coach(int memberId, String firstName, String lastName, Date dateOfBirth,
            String email, String phone, String address) {
        super(memberId, firstName, lastName, dateOfBirth, email, phone, address);
    }

    /**
     * Constructor with all fields
     */
    public Coach(int memberId, String firstName, String lastName, Date dateOfBirth,
            String email, String phone, String address, int coachId, String qualifications) {
        super(memberId, firstName, lastName, dateOfBirth, email, phone, address);
        this.coachId = coachId;
        this.qualifications = qualifications;
    }

    // Getters and Setters

    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public List<Squad> getAssignedSquads() {
        return assignedSquads;
    }

    public void setAssignedSquads(List<Squad> assignedSquads) {
        this.assignedSquads = assignedSquads != null ? assignedSquads : new ArrayList<>();
    }

    /**
     * Adds a squad to the coach's assigned squads
     * 
     * @param squad The squad to assign to the coach
     */
    public void assignSquad(Squad squad) {
        if (this.assignedSquads == null) {
            this.assignedSquads = new ArrayList<>();
        }

        // Check if already assigned
        for (Squad s : assignedSquads) {
            if (s.getSquadId() == squad.getSquadId()) {
                return; // Already assigned
            }
        }

        this.assignedSquads.add(squad);
    }

    /**
     * Removes a squad from the coach's assigned squads
     * 
     * @param squadId The ID of the squad to remove
     * @return true if the squad was removed, false otherwise
     */
    public boolean removeSquad(int squadId) {
        if (this.assignedSquads == null) {
            return false;
        }

        for (int i = 0; i < assignedSquads.size(); i++) {
            if (assignedSquads.get(i).getSquadId() == squadId) {
                assignedSquads.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the coach is assigned to a specific squad
     * 
     * @param squadId The ID of the squad to check
     * @return true if the coach is assigned to the squad, false otherwise
     */
    public boolean isAssignedToSquad(int squadId) {
        if (this.assignedSquads == null) {
            return false;
        }

        for (Squad squad : assignedSquads) {
            if (squad.getSquadId() == squadId) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the number of squads assigned to the coach
     * 
     * @return The number of assigned squads
     */
    public int getAssignedSquadCount() {
        return assignedSquads != null ? assignedSquads.size() : 0;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "coachId=" + coachId +
                ", qualifications='" + qualifications + '\'' +
                ", assignedSquads=" + (assignedSquads != null ? assignedSquads.size() : 0) +
                ", memberInfo=" + super.toString() +
                '}';
    }
}