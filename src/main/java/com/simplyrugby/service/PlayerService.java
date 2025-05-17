package com.simplyrugby.service;

import com.simplyrugby.domain.GameStats;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.List;

public interface PlayerService {
    /**
     * Get a player by ID
     * @param id The player ID
     * @return The player
     * @throws EntityNotFoundException If the player does not exist
     */
    Player getPlayerById(int id);
    
    /**
     * Get all players
     * @return List of all players
     */
    List<Player> getAllPlayers();

    List<Player> getPlayersByName(String name);

    /**
     * Get players in a specific squad
     * @param squadId The squad ID
     * @return List of players in the squad
     */
    List<Player> getPlayersBySquad(int squadId);

    List<Player> getPlayersByPosition(String position);

    List<Player> getPlayersByAgeGrade(String ageGrade);

    /**
     * Add a new player
     * @param player The player to add
     * @return The ID of the newly created player
     * @throws ValidationException If the player data is invalid
     */
    int addPlayer(Player player);
    
    /**
     * Update an existing player
     * @param player The player to update
     * @return True if the update was successful
     * @throws ValidationException If the player data is invalid
     * @throws EntityNotFoundException If the player does not exist
     */
    boolean updatePlayer(Player player);
    
    /**
     * Delete a player
     * @param id The player ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the player does not exist
     */
    boolean deletePlayer(int id);
    
    /**
     * Assign a player to a squad
     * @param playerId The player ID
     * @param squadId The squad ID
     * @return True if the assignment was successful
     * @throws EntityNotFoundException If the player or squad does not exist
     */
    boolean assignPlayerToSquad(int playerId, int squadId);
    
    /**
     * Remove a player from their current squad
     * @param playerId The player ID
     * @return True if the removal was successful
     * @throws EntityNotFoundException If the player does not exist
     */
    boolean removePlayerFromSquad(int playerId);

    boolean setEmergencyContact(int playerId, int emergencyContactId);

    List<Player> getPlayersWithStatsByGame(int gameId);

    List<Player> getPlayersWithAttendanceByTraining(int trainingId);

    List<GameStats> getPlayerGameStats(int playerId);

    List<TrainingAttendance> getPlayerTrainingAttendance(int playerId);

    double calculateOverallSkillRating(int playerId);

    double calculateTrainingAttendanceRate(int playerId);

    void validatePlayer(Player player);
}