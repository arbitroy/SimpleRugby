package com.simplyrugby.service;

import com.simplyrugby.domain.Training;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.Date;
import java.util.List;

/**
 * Service interface for Training entity operations.
 */
public interface TrainingService {
    /**
     * Get a training session by ID
     *
     * @param id The training session ID
     * @return The training session
     * @throws EntityNotFoundException If the training session doesn't exist
     */
    Training getTrainingById(int id);

    /**
     * Get all training sessions
     *
     * @return List of all training sessions
     */
    List<Training> getAllTrainingSessions();

    /**
     * Get training sessions for a specific squad
     *
     * @param squadId The squad ID
     * @return List of training sessions for the squad
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    List<Training> getTrainingSessionsBySquad(int squadId);

    /**
     * Get training sessions after a specific date
     *
     * @param date The date
     * @return List of training sessions after the date
     */
    List<Training> getTrainingAfterDate(Date date);

    /**
     * Get training sessions before a specific date
     *
     * @param date The date
     * @return List of training sessions before the date
     */
    List<Training> getTrainingBeforeDate(Date date);

    /**
     * Get training sessions between two dates
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of training sessions between the dates
     */
    List<Training> getTrainingBetweenDates(Date startDate, Date endDate);

    /**
     * Add a new training session
     *
     * @param training The training session to add
     * @return The ID of the newly created training session
     * @throws ValidationException If the training session data is invalid
     */
    int addTraining(Training training);

    /**
     * Update an existing training session
     *
     * @param training The training session to update
     * @return True if the update was successful
     * @throws ValidationException If the training session data is invalid
     * @throws EntityNotFoundException If the training session doesn't exist
     */
    boolean updateTraining(Training training);

    /**
     * Delete a training session by ID
     *
     * @param id The training session ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the training session doesn't exist
     */
    boolean deleteTraining(int id);

    /**
     * Add attendance record for a player
     *
     * @param attendance The attendance record to add
     * @return True if the addition was successful
     * @throws ValidationException If the attendance record data is invalid
     * @throws EntityNotFoundException If the player or training session doesn't exist
     */
    boolean addAttendance(TrainingAttendance attendance);

    /**
     * Update attendance record for a player
     *
     * @param attendance The attendance record to update
     * @return True if the update was successful
     * @throws ValidationException If the attendance record data is invalid
     * @throws EntityNotFoundException If the attendance record doesn't exist
     */
    boolean updateAttendance(TrainingAttendance attendance);

    /**
     * Get attendance records for a specific training session
     *
     * @param trainingId The training session ID
     * @return List of attendance records for all players
     * @throws EntityNotFoundException If the training session doesn't exist
     */
    List<TrainingAttendance> getAttendanceRecords(int trainingId);

    /**
     * Get attendance records for a specific player
     *
     * @param playerId The player ID
     * @return List of attendance records for all training sessions
     * @throws EntityNotFoundException If the player doesn't exist
     */
    List<TrainingAttendance> getAttendanceByPlayer(int playerId);

    /**
     * Get the attendance record for a specific player in a specific training session
     *
     * @param trainingId The training session ID
     * @param playerId The player ID
     * @return The attendance record or null if not found
     * @throws EntityNotFoundException If the training session or player doesn't exist
     */
    TrainingAttendance getPlayerAttendance(int trainingId, int playerId);

    /**
     * Get upcoming training sessions (sessions in the future)
     *
     * @return List of upcoming training sessions
     */
    List<Training> getUpcomingTraining();

    /**
     * Get recent training sessions (sessions in the past, ordered by date descending)
     *
     * @param limit The maximum number of sessions to return
     * @return List of recent training sessions
     */
    List<Training> getRecentTraining(int limit);

    /**
     * Get training sessions by focus area (partial match)
     *
     * @param focusArea The focus area to search for
     * @return List of training sessions with matching focus areas
     */
    List<Training> getTrainingByFocusArea(String focusArea);

    /**
     * Get the attendance rate for a specific training session
     *
     * @param trainingId The training session ID
     * @return The attendance rate as a percentage
     * @throws EntityNotFoundException If the training session doesn't exist
     */
    double getAttendanceRate(int trainingId);

    /**
     * Get the attendance rate for a specific player across all training sessions
     *
     * @param playerId The player ID
     * @return The attendance rate as a percentage
     * @throws EntityNotFoundException If the player doesn't exist
     */
    double getPlayerAttendanceRate(int playerId);

    /**
     * Validate training session data
     *
     * @param training The training session to validate
     * @throws ValidationException If the training session data is invalid
     */
    void validateTraining(Training training);

    /**
     * Validate attendance record data
     *
     * @param attendance The attendance record to validate
     * @throws ValidationException If the attendance record data is invalid
     */
    void validateAttendance(TrainingAttendance attendance);
}