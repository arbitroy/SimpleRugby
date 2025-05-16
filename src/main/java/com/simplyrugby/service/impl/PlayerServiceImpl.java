package com.simplyrugby.service.impl;

import com.simplyrugby.domain.GameStats;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.repository.GameRepository;
import com.simplyrugby.repository.PlayerRepository;
import com.simplyrugby.repository.SquadRepository;
import com.simplyrugby.repository.TrainingRepository;
import com.simplyrugby.service.PlayerService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final SquadRepository squadRepository;
    private final GameRepository gameRepository;
    private final TrainingRepository trainingRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, SquadRepository squadRepository,
                             GameRepository gameRepository, TrainingRepository trainingRepository) {
        this.playerRepository = playerRepository;
        this.squadRepository = squadRepository;
        this.gameRepository = gameRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    public Player getPlayerById(int id) {
        Player player = playerRepository.findById(id);
        if (player == null) {
            throw new EntityNotFoundException("Player not found with ID: " + id);
        }
        return player;
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public List<Player> getPlayersByName(String name) {
        return playerRepository.findByName(name);
    }

    @Override
    public List<Player> getPlayersBySquad(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        return playerRepository.findBySquad(squadId);
    }

    @Override
    public List<Player> getPlayersByPosition(String position) {
        return playerRepository.findByPosition(position);
    }

    @Override
    public List<Player> getPlayersByAgeGrade(String ageGrade) {
        return playerRepository.findByAgeGrade(ageGrade);
    }

    @Override
    public int addPlayer(Player player) {
        validatePlayer(player);
        return playerRepository.save(player);
    }

    @Override
    public boolean updatePlayer(Player player) {
        if (playerRepository.findById(player.getPlayerId()) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + player.getPlayerId());
        }
        validatePlayer(player);
        return playerRepository.update(player);
    }

    @Override
    public boolean deletePlayer(int id) {
        if (playerRepository.findById(id) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + id);
        }
        return playerRepository.delete(id);
    }

    @Override
    public boolean assignPlayerToSquad(int playerId, int squadId) {
        // Check if both player and squad exist
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return playerRepository.assignToSquad(playerId, squadId);
    }

    @Override
    public boolean removePlayerFromSquad(int playerId) {
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }

        return playerRepository.removeFromSquad(playerId);
    }

    @Override
    public boolean setEmergencyContact(int playerId, int emergencyContactId) {
        // Check if both player and emergency contact exist
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }

        // Members are stored in a different table, so get the repository through our player repository
        if (playerRepository.findById(emergencyContactId) == null) {
            throw new EntityNotFoundException("Emergency contact not found with ID: " + emergencyContactId);
        }

        return playerRepository.setEmergencyContact(playerId, emergencyContactId);
    }

    @Override
    public List<Player> getPlayersWithStatsByGame(int gameId) {
        if (gameRepository.findById(gameId) == null) {
            throw new EntityNotFoundException("Game not found with ID: " + gameId);
        }

        return playerRepository.findPlayersWithStatsByGame(gameId);
    }

    @Override
    public List<Player> getPlayersWithAttendanceByTraining(int trainingId) {
        if (trainingRepository.findById(trainingId) == null) {
            throw new EntityNotFoundException("Training session not found with ID: " + trainingId);
        }

        return playerRepository.findPlayersWithAttendanceByTraining(trainingId);
    }

    @Override
    public List<GameStats> getPlayerGameStats(int playerId) {
        Player player = getPlayerById(playerId);
        return player.getPlayerStats();
    }

    @Override
    public List<TrainingAttendance> getPlayerTrainingAttendance(int playerId) {
        Player player = getPlayerById(playerId);
        return player.getTrainingAttendance();
    }

    @Override
    public double calculateOverallSkillRating(int playerId) {
        Player player = getPlayerById(playerId);
        return player.calculateOverallSkillRating();
    }

    @Override
    public double calculateTrainingAttendanceRate(int playerId) {
        Player player = getPlayerById(playerId);
        return player.calculateTrainingAttendanceRate();
    }

    @Override
    public void validatePlayer(Player player) {
        List<String> errors = new ArrayList<>();

        // Validate member fields first
        validateMemberFields(player, errors);

        // Validate player fields
        if (player.getPosition() == null || player.getPosition().trim().isEmpty()) {
            errors.add("Position is required");
        }

        if (player.getEmergencyContact() != null) {
            // Validate emergency contact
            if (player.getEmergencyContact().getMemberId() <= 0) {
                errors.add("Emergency contact must be a valid member");
            }

            if (player.getEmergencyContact().getMemberId() == player.getMemberId()) {
                errors.add("Emergency contact cannot be the player");
            }
        }

        if (player.getMedicalConditions() != null && player.getMedicalConditions().length() > 100) {
            errors.add("Medical conditions must be 100 characters or less");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Player validation failed", errors);
        }
    }

    private void validateMemberFields(Player player, List<String> errors) {
        // Validate first name
        if (player.getFirstName() == null || player.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        } else if (!player.getFirstName().matches("[a-zA-Z ]{1,20}")) {
            errors.add("First name must contain only letters and spaces, and be 20 characters or less");
        }

        // Validate last name
        if (player.getLastName() == null || player.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        } else if (!player.getLastName().matches("[a-zA-Z ]{1,20}")) {
            errors.add("Last name must contain only letters and spaces, and be 20 characters or less");
        }

        // Validate date of birth
        if (player.getDateOfBirth() == null) {
            errors.add("Date of birth is required");
        } else if (player.getDateOfBirth().after(new java.util.Date())) {
            errors.add("Date of birth cannot be in the future");
        }

        // Validate email
        if (player.getEmail() != null && !player.getEmail().isEmpty()) {
            if (!player.getEmail().contains("@") || !player.getEmail().contains(".")) {
                errors.add("Email must be a valid email address");
            } else if (player.getEmail().length() > 50) {
                errors.add("Email must be 50 characters or less");
            }
        }

        // Validate phone
        if (player.getPhone() != null && !player.getPhone().isEmpty()) {
            if (!player.getPhone().matches("\\d{11}")) {
                errors.add("Phone number must be 11 digits");
            }
        }

        // Validate address
        if (player.getAddress() != null && player.getAddress().length() > 60) {
            errors.add("Address must be 60 characters or less");
        }
    }
}