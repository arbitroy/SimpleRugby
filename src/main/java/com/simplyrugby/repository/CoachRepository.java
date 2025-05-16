package com.simplyrugby.repository;

import com.simplyrugby.domain.Coach;
import java.util.List;

/**
 * Repository interface for Coach entity operations.
 */
public interface CoachRepository {
    /**
     * Find a coach by coach ID
     * 
     * @param id The coach ID
     * @return The coach or null if not found
     */
    Coach findById(int id);
    
    /**
     * Find a coach by member ID
     * 
     * @param memberId The member ID
     * @return The coach or null if not found
     */
    Coach findByMemberId(int memberId);
    
    /**
     * Find all coaches
     * 
     * @return List of all coaches
     */
    List<Coach> findAll();
    
    /**
     * Find coaches by name (partial match)
     * 
     * @param name The name to search for
     * @return List of matching coaches
     */
    List<Coach> findByName(String name);
    
    /**
     * Find coaches assigned to a specific squad
     * 
     * @param squadId The squad ID
     * @return List of coaches assigned to the squad
     */
    List<Coach> findBySquad(int squadId);
    
    /**
     * Save a new coach
     * 
     * @param coach The coach to save
     * @return The ID of the newly created coach
     */
    int save(Coach coach);
    
    /**
     * Update an existing coach
     * 
     * @param coach The coach to update
     * @return True if the update was successful
     */
    boolean update(Coach coach);
    
    /**
     * Delete a coach by ID
     * 
     * @param id The coach ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Assign a coach to a squad
     * 
     * @param coachId The coach ID
     * @param squadId The squad ID
     * @return True if the assignment was successful
     */
    boolean assignToSquad(int coachId, int squadId);
    
    /**
     * Remove a coach from a squad
     * 
     * @param coachId The coach ID
     * @param squadId The squad ID
     * @return True if the removal was successful
     */
    boolean removeFromSquad(int coachId, int squadId);
    
    /**
     * Find coaches by qualification (partial match)
     * 
     * @param qualification The qualification to search for
     * @return List of coaches with matching qualifications
     */
    List<Coach> findByQualification(String qualification);
}