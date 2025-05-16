package com.simplyrugby.repository;

import com.simplyrugby.domain.Training;
import com.simplyrugby.domain.TrainingAttendance;
import java.util.Date;
import java.util.List;

/**
 * Repository interface for Training entity operations.
 */
public interface TrainingRepository {
    /**
     * Find a training session by ID
     * 
     * @param id The training session ID
     * @return The training session or null if not found
     */
    Training findById(int id);
    
    /**
     * Find all training sessions
     * 
     * @return List of all training sessions
     */
    List<Training> findAll();
    
    /**
     * Find training sessions for a specific squad
     * 
     * @param squadId The squad ID
     * @return List of training sessions for the squad
     */
    List<Training> findBySquad(int squadId);
    
    /**
     * Find training sessions after a specific date
     * 
     * @param date The date
     * @return List of training sessions after the date
     */
    List<Training> findTrainingAfterDate(Date date);
    
    /**
     * Find training sessions before a specific date
     * 
     * @param date The date
     * @return List of training sessions before the date
     */
    List<Training> findTrainingBeforeDate(Date date);
    
    /**
     * Find training sessions between two dates
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of training sessions between the dates
     */
    List<Training> findTrainingBetweenDates(Date startDate, Date endDate);
    
    /**
     * Save a new training session
     * 
     * @param training The training session to save
     * @return The ID of the newly created training session
     */
    int save(Training training);
    
    /**
     * Update an existing training session
     * 
     * @param training The training session to update
     * @return True if the update was successful
     */
    boolean update(Training training);
    
    /**
     * Delete a training session by ID
     * 
     * @param id The training session ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Add attendance record for a player
     * 
     * @param attendance The attendance record to add
     * @return True if the addition was successful
     */
    boolean addAttendance(TrainingAttendance attendance);
    
    /**
     * Update attendance record for a player
     * 
     * @param attendance The attendance record to update
     * @return True if the update was successful
     */
    boolean updateAttendance(TrainingAttendance attendance);
    
    /**
     * Get attendance records for a specific training session
     * 
     * @param trainingId The training session ID
     * @return List of attendance records for all players
     */
    List<TrainingAttendance> getAttendanceRecords(int trainingId);
    
    /**
     * Get attendance records for a specific player
     * 
     * @param playerId The player ID
     * @return List of attendance records for all training sessions
     */
    List<TrainingAttendance> getAttendanceByPlayer(int playerId);
    
    /**
     * Get the attendance record for a specific player in a specific training session
     * 
     * @param trainingId The training session ID
     * @param playerId The player ID
     * @return The attendance record or null if not found
     */
    TrainingAttendance getPlayerAttendance(int trainingId, int playerId);
    
    /**
     * Find upcoming training sessions (sessions in the future)
     * 
     * @return List of upcoming training sessions
     */
    List<Training> findUpcomingTraining();
    
    /**
     * Find recent training sessions (sessions in the past, ordered by date descending)
     * 
     * @param limit The maximum number of sessions to return
     * @return List of recent training sessions
     */
    List<Training> findRecentTraining(int limit);
    
    /**
     * Find training sessions by focus area (partial match)
     * 
     * @param focusArea The focus area to search for
     * @return List of training sessions with matching focus areas
     */
    List<Training> findByFocusArea(String focusArea);
    
    /**
     * Get the attendance rate for a specific training session
     * 
     * @param trainingId The training session ID
     * @return The attendance rate as a percentage
     */
    double getAttendanceRate(int trainingId);
    
    /**
     * Get the attendance rate for a specific player across all training sessions
     * 
     * @param playerId The player ID
     * @return The attendance rate as a percentage
     */
    double getPlayerAttendanceRate(int playerId);
}