package com.simplyrugby.service.impl;

import com.simplyrugby.domain.Training;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.repository.PlayerRepository;
import com.simplyrugby.repository.SquadRepository;
import com.simplyrugby.repository.TrainingRepository;
import com.simplyrugby.service.TrainingService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final PlayerRepository playerRepository;
    private final SquadRepository squadRepository;

    public TrainingServiceImpl(TrainingRepository trainingRepository, PlayerRepository playerRepository,
                               SquadRepository squadRepository) {
        this.trainingRepository = trainingRepository;
        this.playerRepository = playerRepository;
        this.squadRepository = squadRepository;
    }

    @Override
    public Training getTrainingById(int id) {
        Training training = trainingRepository.findById(id);
        if (training == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + id);
        }
        return training;
    }

    @Override
    public List<Training> getAllTrainingSessions() {
        return trainingRepository.findAll();
    }

    @Override
    public List<Training> getTrainingSessionsBySquad(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        return trainingRepository.findBySquad(squadId);
    }

    @Override
    public List<Training> getTrainingAfterDate(Date date) {
        return trainingRepository.findTrainingAfterDate(date);
    }

    @Override
    public List<Training> getTrainingBeforeDate(Date date) {
        return trainingRepository.findTrainingBeforeDate(date);
    }

    @Override
    public List<Training> getTrainingBetweenDates(Date startDate, Date endDate) {
        return trainingRepository.findTrainingBetweenDates(startDate, endDate);
    }

    @Override
    public int addTraining(Training training) {
        validateTraining(training);
        return trainingRepository.save(training);
    }

    @Override
    public boolean updateTraining(Training training) {
        if (trainingRepository.findById(training.getTrainingId()) == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + training.getTrainingId());
        }
        validateTraining(training);
        return trainingRepository.update(training);
    }

    @Override
    public boolean deleteTraining(int id) {
        if (trainingRepository.findById(id) == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + id);
        }
        return trainingRepository.delete(id);
    }

    @Override
    public boolean addAttendance(TrainingAttendance attendance) {
        validateAttendance(attendance);

        // Check if player and training session exist
        if (playerRepository.findById(attendance.getPlayerId()) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + attendance.getPlayerId());
        }

        if (trainingRepository.findById(attendance.getTrainingId()) == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + attendance.getTrainingId());
        }

        return trainingRepository.addAttendance(attendance);
    }

    @Override
    public boolean updateAttendance(TrainingAttendance attendance) {
        validateAttendance(attendance);

        // Check if attendance record exists
        TrainingAttendance existingAttendance = trainingRepository.getPlayerAttendance(
                attendance.getTrainingId(), attendance.getPlayerId());

        if (existingAttendance == null) {
            throw new EntityNotFoundException("Attendance record not found for player ID: " +
                    attendance.getPlayerId() + " and training session ID: " +
                    attendance.getTrainingId());
        }

        return trainingRepository.updateAttendance(attendance);
    }

    @Override
    public List<TrainingAttendance> getAttendanceRecords(int trainingId) {
        if (trainingRepository.findById(trainingId) == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + trainingId);
        }
        return trainingRepository.getAttendanceRecords(trainingId);
    }

    @Override
    public List<TrainingAttendance> getAttendanceByPlayer(int playerId) {
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }
        return trainingRepository.getAttendanceByPlayer(playerId);
    }

    @Override
    public TrainingAttendance getPlayerAttendance(int trainingId, int playerId) {
        if (trainingRepository.findById(trainingId) == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + trainingId);
        }

        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }

        return trainingRepository.getPlayerAttendance(trainingId, playerId);
    }

    @Override
    public List<Training> getUpcomingTraining() {
        return trainingRepository.findUpcomingTraining();
    }

    @Override
    public List<Training> getRecentTraining(int limit) {
        return trainingRepository.findRecentTraining(limit);
    }

    @Override
    public List<Training> getTrainingByFocusArea(String focusArea) {
        return trainingRepository.findByFocusArea(focusArea);
    }

    @Override
    public double getAttendanceRate(int trainingId) {
        if (trainingRepository.findById(trainingId) == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + trainingId);
        }
        return trainingRepository.getAttendanceRate(trainingId);
    }

    @Override
    public double getPlayerAttendanceRate(int playerId) {
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }
        return trainingRepository.getPlayerAttendanceRate(playerId);
    }

    @Override
    public void validateTraining(Training training) {
        List<String> errors = new ArrayList<>();

        // Validate date
        if (training.getDate() == null) {
            errors.add("Training session date is required");
        } else if (training.getDate().after(new Date())) {
            errors.add("Training session date cannot be in the future");
        }

        // Validate squad
        if (training.getSquad() == null) {
            errors.add("Squad is required");
        } else if (training.getSquad().getSquadId() <= 0) {
            errors.add("Squad must be a valid squad");
        }

        // Validate focus areas
        if (training.getFocusAreas() == null || training.getFocusAreas().trim().isEmpty()) {
            errors.add("Focus areas are required");
        } else if (training.getFocusAreas().length() > 75) {
            errors.add("Focus areas must be 75 characters or less");
        }

        // Validate coach notes if provided
        if (training.getCoachNotes() != null && training.getCoachNotes().length() > 200) {
            errors.add("Coach notes must be 200 characters or less");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Training session validation failed", errors);
        }
    }

    @Override
    public void validateAttendance(TrainingAttendance attendance) {
        List<String> errors = new ArrayList<>();

        // Validate player ID
        if (attendance.getPlayerId() <= 0) {
            errors.add("Player ID is required");
        }

        // Validate training session ID
        if (attendance.getTrainingId() <= 0) {
            errors.add("Training session ID is required");
        }

        // Validate player notes if provided
        if (attendance.getPlayerNotes() != null && attendance.getPlayerNotes().length() > 200) {
            errors.add("Player notes must be 200 characters or less");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Attendance record validation failed", errors);
        }
    }
}