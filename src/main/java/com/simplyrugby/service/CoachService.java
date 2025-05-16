package com.simplyrugby.service;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.List;

/**
 * Service interface for Coach entity operations.
 */
public interface CoachService {
    /**
     * Get a coach by ID
     *
     * @param id The coach ID
     * @return The coach
     * @throws EntityNotFoundException If the coach doesn't exist
     */
    Coach getCoachById(int id);

    /**
     * Get a coach by member ID
     *
     * @param memberId The member ID
     * @return The coach
     * @throws EntityNotFoundException If the coach doesn't exist
     */
    Coach getCoachByMemberId(int memberId);

    /**
     * Get all coaches
     *
     * @return List of all coaches
     */
    List<Coach> getAllCoaches();

    /**
     * Get coaches by name (partial match)
     *
     * @param name The name to search for
     * @return List of matching coaches
     */
    List<Coach> getCoachesByName(String name);

    /**
     * Get coaches assigned to a specific squad
     *
     * @param squadId The squad ID
     * @return List of coaches assigned to the squad
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    List<Coach> getCoachesBySquad(int squadId);

    /**
     * Get coaches by qualification (partial match)
     *
     * @param qualification The qualification to search for
     * @return List of coaches with matching qualifications
     */
    List<Coach> getCoachesByQualification(String qualification);

    /**
     * Add a new coach
     *
     * @param coach The coach to add
     * @return The ID of the newly created coach
     * @throws ValidationException If the coach data is invalid
     */
    int addCoach(Coach coach);

    /**
     * Update an existing coach
     *
     * @param coach The coach to update
     * @return True if the update was successful
     * @throws ValidationException If the coach data is invalid
     * @throws EntityNotFoundException If the coach doesn't exist
     */
    boolean updateCoach(Coach coach);

    /**
     * Delete a coach by ID
     *
     * @param id The coach ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the coach doesn't exist
     */
    boolean deleteCoach(int id);

    /**
     * Assign a coach to a squad
     *
     * @param coachId The coach ID
     * @param squadId The squad ID
     * @return True if the assignment was successful
     * @throws EntityNotFoundException If the coach or squad doesn't exist
     */
    boolean assignCoachToSquad(int coachId, int squadId);

    /**
     * Remove a coach from a squad
     *
     * @param coachId The coach ID
     * @param squadId The squad ID
     * @return True if the removal was successful
     * @throws EntityNotFoundException If the coach or squad doesn't exist
     */
    boolean removeCoachFromSquad(int coachId, int squadId);

    /**
     * Get the squads assigned to a coach
     *
     * @param coachId The coach ID
     * @return List of squads assigned to the coach
     * @throws EntityNotFoundException If the coach doesn't exist
     */
    List<Squad> getAssignedSquads(int coachId);

    /**
     * Validate coach data
     *
     * @param coach The coach to validate
     * @throws ValidationException If the coach data is invalid
     */
    void validateCoach(Coach coach);
}