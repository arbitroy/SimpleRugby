package com.simplyrugby.repository;

import com.simplyrugby.domain.Game;
import com.simplyrugby.domain.GameStats;
import java.util.Date;
import java.util.List;

/**
 * Repository interface for Game entity operations.
 */
public interface GameRepository {
    /**
     * Find a game by ID
     * 
     * @param id The game ID
     * @return The game or null if not found
     */
    Game findById(int id);
    
    /**
     * Find all games
     * 
     * @return List of all games
     */
    List<Game> findAll();
    
    /**
     * Find games for a specific squad
     * 
     * @param squadId The squad ID
     * @return List of games for the squad
     */
    List<Game> findBySquad(int squadId);
    
    /**
     * Find games by opponent (partial match)
     * 
     * @param opponent The opponent to search for
     * @return List of games against the opponent
     */
    List<Game> findByOpponent(String opponent);
    
    /**
     * Find games after a specific date
     * 
     * @param date The date
     * @return List of games after the date
     */
    List<Game> findGamesAfterDate(Date date);
    
    /**
     * Find games before a specific date
     * 
     * @param date The date
     * @return List of games before the date
     */
    List<Game> findGamesBeforeDate(Date date);
    
    /**
     * Find games between two dates
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of games between the dates
     */
    List<Game> findGamesBetweenDates(Date startDate, Date endDate);
    
    /**
     * Save a new game
     * 
     * @param game The game to save
     * @return The ID of the newly created game
     */
    int save(Game game);
    
    /**
     * Update an existing game
     * 
     * @param game The game to update
     * @return True if the update was successful
     */
    boolean update(Game game);
    
    /**
     * Delete a game by ID
     * 
     * @param id The game ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Add game statistics for a player
     * 
     * @param stats The game statistics to add
     * @return True if the addition was successful
     */
    boolean addGameStats(GameStats stats);
    
    /**
     * Update game statistics for a player
     * 
     * @param stats The game statistics to update
     * @return True if the update was successful
     */
    boolean updateGameStats(GameStats stats);
    
    /**
     * Get game statistics for a specific game
     * 
     * @param gameId The game ID
     * @return List of game statistics for all players
     */
    List<GameStats> getGameStats(int gameId);
    
    /**
     * Get game statistics for a specific player
     * 
     * @param playerId The player ID
     * @return List of game statistics for all games
     */
    List<GameStats> getStatsByPlayer(int playerId);
    
    /**
     * Get game statistics for a specific player in a specific game
     * 
     * @param gameId The game ID
     * @param playerId The player ID
     * @return The game statistics or null if not found
     */
    GameStats getPlayerGameStats(int gameId, int playerId);
    
    /**
     * Find upcoming games (games in the future)
     * 
     * @return List of upcoming games
     */
    List<Game> findUpcomingGames();
    
    /**
     * Find recent games (games in the past, ordered by date descending)
     * 
     * @param limit The maximum number of games to return
     * @return List of recent games
     */
    List<Game> findRecentGames(int limit);
    
    /**
     * Get the count of wins, losses, and draws for a squad
     * 
     * @param squadId The squad ID
     * @return Array with [wins, losses, draws]
     */
    int[] getSquadRecord(int squadId);
}