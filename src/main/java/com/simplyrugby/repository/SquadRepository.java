package com.simplyrugby.repository;

import com.simplyrugby.domain.Squad;
import java.util.List;

/**
 * Repository interface for Squad entity operations.
 */
public interface SquadRepository {
    /**
     * Find a squad by ID
     * 
     * @param id The squad ID
     * @return The squad or null if not found
     */
    Squad findById(int id);
    
    /**
     * Find all squads
     * 
     * @return List of all squads
     */
    List<Squad> findAll();
    
    /**
     * Find squads by name (partial match)
     * 
     * @param name The name to search for
     * @return List of matching squads
     */
    List<Squad> findByName(String name);
    
    /**
     * Find squads by age grade
     * 
     * @param ageGrade The age grade to search for
     * @return List of squads with the given age grade
     */
    List<Squad> findByAgeGrade(String ageGrade);
    
    /**
     * Find squads associated with a coach
     * 
     * @param coachId The coach ID
     * @return List of squads assigned to the coach
     */
    List<Squad> findByCoach(int coachId);
    
    /**
     * Find the squad that a player belongs to
     * 
     * @param playerId The player ID
     * @return The squad or null if not found
     */
    Squad findByPlayer(int playerId);
    
    /**
     * Save a new squad
     * 
     * @param squad The squad to save
     * @return The ID of the newly created squad
     */
    int save(Squad squad);
    
    /**
     * Update an existing squad
     * 
     * @param squad The squad to update
     * @return True if the update was successful
     */
    boolean update(Squad squad);
    
    /**
     * Delete a squad by ID
     * 
     * @param id The squad ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Add a player to a squad
     * 
     * @param squadId The squad ID
     * @param playerId The player ID
     * @return True if the addition was successful
     */
    boolean addPlayer(int squadId, int playerId);
    
    /**
     * Remove a player from a squad
     * 
     * @param squadId The squad ID
     * @param playerId The player ID
     * @return True if the removal was successful
     */
    boolean removePlayer(int squadId, int playerId);
    
    /**
     * Add a coach to a squad
     * 
     * @param squadId The squad ID
     * @param coachId The coach ID
     * @return True if the addition was successful
     */
    boolean addCoach(int squadId, int coachId);
    
    /**
     * Remove a coach from a squad
     * 
     * @param squadId The squad ID
     * @param coachId The coach ID
     * @return True if the removal was successful
     */
    boolean removeCoach(int squadId, int coachId);
    
    /**
     * Get the number of players in a squad
     * 
     * @param squadId The squad ID
     * @return The number of players
     */
    int getPlayerCount(int squadId);
    
    /**
     * Get the number of coaches assigned to a squad
     * 
     * @param squadId The squad ID
     * @return The number of coaches
     */
    int getCoachCount(int squadId);
}