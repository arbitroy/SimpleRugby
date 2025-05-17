package com.simplyrugby.service.impl;

import com.simplyrugby.domain.Game;
import com.simplyrugby.domain.GameStats;
import com.simplyrugby.repository.GameRepository;
import com.simplyrugby.repository.PlayerRepository;
import com.simplyrugby.repository.SquadRepository;
import com.simplyrugby.service.GameService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final SquadRepository squadRepository;

    public GameServiceImpl(GameRepository gameRepository, PlayerRepository playerRepository,
                           SquadRepository squadRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.squadRepository = squadRepository;
    }

    @Override
    public Game getGameById(int id) {
        Game game = gameRepository.findById(id);
        if (game == null) {
            throw new EntityNotFoundException("Game not found with ID: " + id);
        }
        return game;
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public List<Game> getGamesBySquad(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        return gameRepository.findBySquad(squadId);
    }

    @Override
    public List<Game> getGamesByOpponent(String opponent) {
        return gameRepository.findByOpponent(opponent);
    }

    @Override
    public List<Game> getGamesAfterDate(Date date) {
        return gameRepository.findGamesAfterDate(date);
    }

    @Override
    public List<Game> getGamesBeforeDate(Date date) {
        return gameRepository.findGamesBeforeDate(date);
    }

    @Override
    public List<Game> getGamesBetweenDates(Date startDate, Date endDate) {
        return gameRepository.findGamesBetweenDates(startDate, endDate);
    }

    @Override
    public int addGame(Game game) {
        validateGame(game);
        return gameRepository.save(game);
    }

    @Override
    public boolean updateGame(Game game) {
        if (gameRepository.findById(game.getGameId()) == null) {
            throw new EntityNotFoundException("Game not found with ID: " + game.getGameId());
        }
        validateGame(game);
        return gameRepository.update(game);
    }

    @Override
    public boolean deleteGame(int id) {
        if (gameRepository.findById(id) == null) {
            throw new EntityNotFoundException("Game not found with ID: " + id);
        }
        return gameRepository.delete(id);
    }

    @Override
    public boolean addGameStats(GameStats stats) {
        validateGameStats(stats);

        // Check if player and game exist
        if (playerRepository.findById(stats.getPlayerId()) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + stats.getPlayerId());
        }

        if (gameRepository.findById(stats.getGameId()) == null) {
            throw new EntityNotFoundException("Game not found with ID: " + stats.getGameId());
        }

        return gameRepository.addGameStats(stats);
    }

    @Override
    public boolean updateGameStats(GameStats stats) {
        validateGameStats(stats);

        // Check if game stats exist
        GameStats existingStats = gameRepository.getPlayerGameStats(stats.getGameId(), stats.getPlayerId());
        if (existingStats == null) {
            throw new EntityNotFoundException("Game statistics not found for player ID: " +
                    stats.getPlayerId() + " and game ID: " + stats.getGameId());
        }

        return gameRepository.updateGameStats(stats);
    }

    @Override
    public List<GameStats> getGameStats(int gameId) {
        if (gameRepository.findById(gameId) == null) {
            throw new EntityNotFoundException("Game not found with ID: " + gameId);
        }
        return gameRepository.getGameStats(gameId);
    }

    @Override
    public List<GameStats> getStatsByPlayer(int playerId) {
        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }
        return gameRepository.getStatsByPlayer(playerId);
    }

    @Override
    public GameStats getPlayerGameStats(int gameId, int playerId) {
        if (gameRepository.findById(gameId) == null) {
            throw new EntityNotFoundException("Game not found with ID: " + gameId);
        }

        if (playerRepository.findById(playerId) == null) {
            throw new EntityNotFoundException("Player not found with ID: " + playerId);
        }

        return gameRepository.getPlayerGameStats(gameId, playerId);
    }

    @Override
    public List<Game> getUpcomingGames() {
        return gameRepository.findUpcomingGames();
    }

    @Override
    public List<Game> getRecentGames(int limit) {
        return gameRepository.findRecentGames(limit);
    }

    @Override
    public int[] getSquadRecord(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return gameRepository.getSquadRecord(squadId);
    }

    // Helper method to validate game data
    public void validateGame(Game game) {
        List<String> errors = new ArrayList<>();

        // Validate date
        if (game.getDate() == null) {
            errors.add("Game date is required");
        } else if (game.getDate().after(new Date())) {
            errors.add("Game date cannot be in the future");
        }

        // Validate opponent
        if (game.getOpponent() == null || game.getOpponent().trim().isEmpty()) {
            errors.add("Opponent is required");
        } else if (game.getOpponent().length() > 20) {
            errors.add("Opponent name must be 20 characters or less");
        }

        // Validate final score if provided
        if (game.getFinalScore() != null && !game.getFinalScore().isEmpty()) {
            if (!game.getFinalScore().matches("\\d+ - \\d+")) {
                errors.add("Final score must be in format 'XX - XX'");
            }
        }

        // Validate venue
        if (game.getVenue() == null || game.getVenue().trim().isEmpty()) {
            errors.add("Venue is required");
        }

        // Validate squad
        if (game.getSquad() == null) {
            errors.add("Squad is required");
        } else if (game.getSquad().getSquadId() <= 0) {
            errors.add("Squad must be a valid squad");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Game validation failed", errors);
        }
    }

    // Helper method to validate game stats data
    public void validateGameStats(GameStats stats) {
        List<String> errors = new ArrayList<>();

        // Validate player ID
        if (stats.getPlayerId() <= 0) {
            errors.add("Player ID is required");
        }

        // Validate game ID
        if (stats.getGameId() <= 0) {
            errors.add("Game ID is required");
        }

        // Validate tackles
        if (stats.getTackles() < 0 || stats.getTackles() > 10) {
            errors.add("Tackles must be between 0 and 10");
        }

        // Validate passes
        if (stats.getPasses() < 0 || stats.getPasses() > 10) {
            errors.add("Passes must be between 0 and 10");
        }

        // Validate tries
        if (stats.getTries() < 0 || stats.getTries() > 10) {
            errors.add("Tries must be between 0 and 10");
        }

        // Validate kicks
        if (stats.getKicks() < 0 || stats.getKicks() > 10) {
            errors.add("Kicks must be between 0 and 10");
        }

        // Validate overall rating
        if (stats.getOverallRating() < 0 || stats.getOverallRating() > 10) {
            errors.add("Overall rating must be between 0 and 10");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Game statistics validation failed", errors);
        }
    }
}