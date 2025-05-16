package com.simplyrugby.service;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.List;

/**
 * Service interface for Squad entity operations.
 */
public interface SquadService {
    /**
     * Get a squad by ID
     *
     * @param id The squad ID
     * @return The squad
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    Squad getSquadById(int id);

    /**
     * Get all squads
     *
     * @return List of all squads
     */
    List<Squad> getAllSquads();

    /**
     * Get squads by name (partial match)
     *
     * @param name The name to search for
     * @return List of matching squads
     */
    List<Squad> getSquadsByName(String name);

    /**
     * Get squads by age grade
     *
     * @param ageGrade The age grade
     * @return List of squads with the given age grade
     */
    List<Squad> getSquadsByAgeGrade(String ageGrade);

    /**
     * Get squads associated with a coach
     *
     * @param coachId The coach ID
     * @return List of squads assigned to the coach
     * @throws EntityNotFoundException If the coach doesn't exist
     */
    List<Squad> getSquadsByCoach(int coachId);

    /**
     * Get the squad that a player belongs to
     *
     * @param playerId The player ID
     * @return The squad
     * @throws EntityNotFoundException If the player doesn't exist
     */
    Squad getSquadByPlayer(int playerId);

    /**
     * Add a new squad
     *
     * @param squad The squad to add
     * @return The ID of the newly created squad
     * @throws ValidationException If the squad data is invalid
     */
    int addSquad(Squad squad);

    /**
     * Update an existing squad
     *
     * @param squad The squad to update
     * @return True if the update was successful
     * @throws ValidationException If the squad data is invalid
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    boolean updateSquad(Squad squad);

    /**
     * Delete a squad by ID
     *
     * @param id The squad ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    boolean deleteSquad(int id);

    /**
     * Add a player to a squad
     *
     * @param squadId The squad ID
     * @param playerId The player ID
     * @return True if the addition was successful
     * @throws EntityNotFoundException If the squad or player doesn't exist
     */
    boolean addPlayer(int squadId, int playerId);

    /**
     * Remove a player from a squad
     *
     * @param squadId The squad ID
     * @param playerId The player ID
     * @return True if the removal was successful
     * @throws EntityNotFoundException If the squad or player doesn't exist
     */
    boolean removePlayer(int squadId, int playerId);

    /**
     * Add a coach to a squad
     *
     * @param squadId The squad ID
     * @param coachId The coach ID
     * @return True if the addition was successful
     * @throws EntityNotFoundException If the squad or coach doesn't exist
     */
    boolean addCoach(int squadId, int coachId);

    /**
     * Remove a coach from a squad
     *
     * @param squadId The squad ID
     * @param coachId The coach ID
     * @return True if the removal was successful
     * @throws EntityNotFoundException If the squad or coach doesn't exist
     */
    boolean removeCoach(int squadId, int coachId);

    /**
     * Get the players in a squad
     *
     * @param squadId The squad ID
     * @return List of players in the squad
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    List<Player> getPlayers(int squadId);

    /**
     * Get the coaches assigned to a squad
     *
     * @param squadId The squad ID
     * @return List of coaches assigned to the squad
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    List<Coach> getCoaches(int squadId);

    /**
     * Get the number of players in a squad
     *
     * @param squadId The squad ID
     * @return The number of players
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    int getPlayerCount(int squadId);

    /**
     * Get the number of coaches assigned to a squad
     *
     * @param squadId The squad ID
     * @return The number of coaches
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    int getCoachCount(int squadId);

    /**
     * Check if a squad has the minimum required number of coaches
     *
     * @param squadId The squad ID
     * @return True if the squad has enough coaches, false otherwise
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    boolean hasMinimumCoaches(int squadId);

    /**
     * Validate squad data
     *
     * @param squad The squad to validate
     * @throws ValidationException If the squad data is invalid
     */
    void validateSquad(Squad squad);
}