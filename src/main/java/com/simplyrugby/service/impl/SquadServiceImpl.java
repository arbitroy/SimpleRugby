package com.simplyrugby.service.impl;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.repository.CoachRepository;
import com.simplyrugby.repository.PlayerRepository;
import com.simplyrugby.repository.SquadRepository;
import com.simplyrugby.service.SquadService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class SquadServiceImpl implements SquadService {
    private final SquadRepository squadRepository;
    private final PlayerRepository playerRepository;
    private final CoachRepository coachRepository;

    public SquadServiceImpl(SquadRepository squadRepository, PlayerRepository playerRepository,
                            CoachRepository coachRepository) {
        this.squadRepository = squadRepository;
        this.playerRepository = playerRepository;
        this.coachRepository = coachRepository;
    }

    @Override
    public Squad getSquadById(int id) {
        Squad squad = squadRepository.findById(id);
        if (squad == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + id);
        }
        return squad;
    }

    @Override
    public List<Squad> getAllSquads() {
        return squadRepository.findAll();
    }

    @Override
    public List<Squad> getSquadsByName(String name) {
        return squadRepository.findByName(name);
    }

    @Override
    public List<Squad> getSquadsByAgeGrade(String ageGrade) {
        return squadRepository.findByAgeGrade(ageGrade);
    }

    @Override
    public List<Squad> getSquadsByCoach(int coachId) {
        if (coachRepository.findById(coachId) == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + coachId);
        }
        return squadRepository.findByCoach(coachId);
    }

    @Override
    public Squad getSquadByPlayer(int playerId) {
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }

        Squad squad = squadRepository.findByPlayer(playerId);
        if (squad == null) {
            throw new EntityNotFoundException("Player is not assigned to any squad");
        }

        return squad;
    }

    @Override
    public int addSquad(Squad squad) {
        validateSquad(squad);
        return squadRepository.save(squad);
    }

    @Override
    public boolean updateSquad(Squad squad) {
        if (squadRepository.findById(squad.getSquadId()) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squad.getSquadId());
        }
        validateSquad(squad);
        return squadRepository.update(squad);
    }

    @Override
    public boolean deleteSquad(int id) {
        if (squadRepository.findById(id) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + id);
        }
        return squadRepository.delete(id);
    }

    @Override
    public boolean addPlayer(int squadId, int playerId) {
        // Check if squad and player exist
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }

        return squadRepository.addPlayer(squadId, playerId);
    }

    @Override
    public boolean removePlayer(int squadId, int playerId) {
        // Check if squad and player exist
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }

        return squadRepository.removePlayer(squadId, playerId);
    }

    @Override
    public boolean addCoach(int squadId, int coachId) {
        // Check if squad and coach exist
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        if (coachRepository.findById(coachId) == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + coachId);
        }

        return squadRepository.addCoach(squadId, coachId);
    }

    @Override
    public boolean removeCoach(int squadId, int coachId) {
        // Check if squad and coach exist
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        if (coachRepository.findById(coachId) == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + coachId);
        }

        return squadRepository.removeCoach(squadId, coachId);
    }

    @Override
    public List<Player> getPlayers(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return playerRepository.findBySquad(squadId);
    }

    @Override
    public List<Coach> getCoaches(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return coachRepository.findBySquad(squadId);
    }

    @Override
    public int getPlayerCount(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return squadRepository.getPlayerCount(squadId);
    }

    @Override
    public int getCoachCount(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return squadRepository.getCoachCount(squadId);
    }

    @Override
    public boolean hasMinimumCoaches(int squadId) {
        Squad squad = getSquadById(squadId);

        int coachCount = getCoachCount(squadId);

        // Mini and midi teams need at least 2 coaches
        if (squad.getAgeGrade().startsWith("U") && !squad.getAgeGrade().equals("U18")) {
            return coachCount >= 2;
        }

        // Senior teams need at least 3 coaches
        return coachCount >= 3;
    }

    @Override
    public void validateSquad(Squad squad) {
        List<String> errors = new ArrayList<>();

        // Validate squad name
        if (squad.getSquadName() == null || squad.getSquadName().trim().isEmpty()) {
            errors.add("Squad name is required");
        } else if (!squad.getSquadName().matches("[a-zA-Z0-9 ]{1,20}")) {
            errors.add("Squad name must contain only letters, numbers, and spaces, and be 20 characters or less");
        }

        // Validate age grade
        if (squad.getAgeGrade() == null || squad.getAgeGrade().trim().isEmpty()) {
            errors.add("Age grade is required");
        } else if (!squad.getAgeGrade().matches("U\\d{1,2}|Senior")) {
            errors.add("Age grade must be in the format 'U' followed by a number (e.g. U12) or 'Senior'");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Squad validation failed", errors);
        }
    }
}