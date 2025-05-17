package com.simplyrugby.service;

import com.simplyrugby.domain.Game;
import com.simplyrugby.domain.GameStats;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.Date;
import java.util.List;

/**
 * Service interface for Game entity operations.
 */
public interface GameService {
    /**
     * Get a game by ID
     *
     * @param id The game ID
     * @return The game
     * @throws EntityNotFoundException If the game doesn't exist
     */
    Game getGameById(int id);

    /**
     * Get all games
     *
     * @return List of all games
     */
    List<Game> getAllGames();

    /**
     * Get games for a specific squad
     *
     * @param squadId The squad ID
     * @return List of games for the squad
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    List<Game> getGamesBySquad(int squadId);

    /**
     * Get games by opponent (partial match)
     *
     * @param opponent The opponent to search for
     * @return List of games against the opponent
     */
    List<Game> getGamesByOpponent(String opponent);

    /**
     * Get games after a specific date
     *
     * @param date The date
     * @return List of games after the date
     */
    List<Game> getGamesAfterDate(Date date);

    /**
     * Get games before a specific date
     *
     * @param date The date
     * @return List of games before the date
     */
    List<Game> getGamesBeforeDate(Date date);

    /**
     * Get games between two dates
     *
     * @param startDate The start date
     * @param endDate   The end date
     * @return List of games between the dates
     */
    List<Game> getGamesBetweenDates(Date startDate, Date endDate);

    /**
     * Add a new game
     *
     * @param game The game to add
     * @return The ID of the newly created game
     * @throws ValidationException If the game data is invalid
     */
    int addGame(Game game);

    /**
     * Update an existing game
     *
     * @param game The game to update
     * @return True if the update was successful
     * @throws ValidationException     If the game data is invalid
     * @throws EntityNotFoundException If the game doesn't exist
     */
    boolean updateGame(Game game);

    /**
     * Delete a game by ID
     *
     * @param id The game ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the game doesn't exist
     */
    boolean deleteGame(int id);

    /**
     * Add game statistics for a player
     *
     * @param stats The game statistics to add
     * @return True if the addition was successful
     * @throws ValidationException     If the statistics data is invalid
     * @throws EntityNotFoundException If the player or game doesn't exist
     */
    boolean addGameStats(GameStats stats);

    /**
     * Update game statistics for a player
     *
     * @param stats The game statistics to update
     * @return True if the update was successful
     * @throws ValidationException     If the statistics data is invalid
     * @throws EntityNotFoundException If the statistics record doesn't exist
     */
    boolean updateGameStats(GameStats stats);

    /**
     * Get game statistics for a specific game
     *
     * @param gameId The game ID
     * @return List of game statistics for all players
     * @throws EntityNotFoundException If the game doesn't exist
     */
    List<GameStats> getGameStats(int gameId);

    /**
     * Get game statistics for a specific player
     *
     * @param playerId The player ID
     * @return List of game statistics for all games
     * @throws EntityNotFoundException If the player doesn't exist
     */
    List<GameStats> getStatsByPlayer(int playerId);

    /**
     * Get game statistics for a specific player in a specific game
     *
     * @param gameId   The game ID
     * @param playerId The player ID
     * @return The game statistics or null if not found
     * @throws EntityNotFoundException If the game or player doesn't exist
     */
    GameStats getPlayerGameStats(int gameId, int playerId);

    List<Game> getUpcomingGames();

    /**
     * Get recent games (games in the past, ordered by date descending)
     *
     * @param limit The maximum number of games to return
     * @return List of recent games
     */
    List<Game> getRecentGames(int limit);

    /**
     * Get the win/loss/draw record for a squad
     *
     * @param squadId The squad ID
     * @return Array with [wins, losses, draws]
     * @throws EntityNotFoundException If the squad doesn't exist
     */
    int[] getSquadRecord(int squadId);

    /**
     * Validate game data
     *
     * @param game The game to validate
     * @throws ValidationException If the game data is invalid
     */
    void validateGame(Game game);

    /**
     * Validate game statistics data
     *
     * @param stats The game statistics to validate
     * @throws ValidationException If the statistics data is invalid
     */
    void validateGameStats(GameStats stats);
}