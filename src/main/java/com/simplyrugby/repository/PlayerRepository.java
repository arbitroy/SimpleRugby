package com.simplyrugby.repository;

import com.simplyrugby.domain.Player;
import java.util.List;

/**
 * Repository interface for Player entity operations.
 */
public interface PlayerRepository {
    /**
     * Find a player by player ID
     * 
     * @param id The player ID
     * @return The player or null if not found
     */
    Player findById(int id);
    
    /**
     * Find a player by member ID
     * 
     * @param memberId The member ID
     * @return The player or null if not found
     */
    Player findByMemberId(int memberId);
    
    /**
     * Find all players
     * 
     * @return List of all players
     */
    List<Player> findAll();
    
    /**
     * Find players by name (partial match)
     * 
     * @param name The name to search for
     * @return List of matching players
     */
    List<Player> findByName(String name);
    
    /**
     * Find players assigned to a specific squad
     * 
     * @param squadId The squad ID
     * @return List of players in the squad
     */
    List<Player> findBySquad(int squadId);
    
    /**
     * Find players by position
     * 
     * @param position The position to search for
     * @return List of players with the given position
     */
    List<Player> findByPosition(String position);
    
    /**
     * Find players by age grade
     * 
     * @param ageGrade The age grade to search for
     * @return List of players in the given age grade
     */
    List<Player> findByAgeGrade(String ageGrade);
    
    /**
     * Save a new player
     * 
     * @param player The player to save
     * @return The ID of the newly created player
     */
    int save(Player player);
    
    /**
     * Update an existing player
     * 
     * @param player The player to update
     * @return True if the update was successful
     */
    boolean update(Player player);
    
    /**
     * Delete a player by ID
     * 
     * @param id The player ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Assign a player to a squad
     * 
     * @param playerId The player ID
     * @param squadId The squad ID
     * @return True if the assignment was successful
     */
    boolean assignToSquad(int playerId, int squadId);
    
    /**
     * Remove a player from their current squad
     * 
     * @param playerId The player ID
     * @return True if the removal was successful
     */
    boolean removeFromSquad(int playerId);
    
    /**
     * Set a player's emergency contact
     * 
     * @param playerId The player ID
     * @param emergencyContactId The emergency contact member ID
     * @return True if the operation was successful
     */
    boolean setEmergencyContact(int playerId, int emergencyContactId);
    
    /**
     * Get players with game statistics for a specific game
     * 
     * @param gameId The game ID
     * @return List of players with their statistics
     */
    List<Player> findPlayersWithStatsByGame(int gameId);
    
    /**
     * Get players with attendance records for a specific training session
     * 
     * @param trainingId The training session ID
     * @return List of players with their attendance records
     */
    List<Player> findPlayersWithAttendanceByTraining(int trainingId);
}