package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Game;
import com.simplyrugby.domain.GameStats;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.repository.GameRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteGameRepository implements GameRepository {
    private final ConnectionManager connectionManager;
    
    public SQLiteGameRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Game findById(int id) {
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                "WHERE g.gameID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Game game = mapResultSetToGame(rs);

                // Load game stats using the public method
                game.setGameStats(getGameStats(id));

                return game;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding game with ID: " + id, e);
        }
    }

    @Override
    public List<Game> findAll() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                "LEFT JOIN Squad s ON g.squadID = s.squadID";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }

            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }

            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all games", e);
        }
    }
    
    @Override
    public List<Game> findBySquad(int squadId) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                     "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                     "WHERE g.squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
            
            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }
            
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding games by squad: " + squadId, e);
        }
    }
    
    @Override
    public List<Game> findByOpponent(String opponent) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                     "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                     "WHERE g.opponent LIKE ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + opponent + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
            
            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }
            
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding games by opponent: " + opponent, e);
        }
    }
    
    @Override
    public List<Game> findGamesAfterDate(Date date) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                     "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                     "WHERE g.date > ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(date));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
            
            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }
            
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding games after date", e);
        }
    }
    
    @Override
    public List<Game> findGamesBeforeDate(Date date) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                     "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                     "WHERE g.date < ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(date));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
            
            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }
            
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding games before date", e);
        }
    }
    
    @Override
    public List<Game> findGamesBetweenDates(Date startDate, Date endDate) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                     "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                     "WHERE g.date BETWEEN ? AND ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(startDate));
            pstmt.setString(2, sdf.format(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
            
            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }
            
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding games between dates", e);
        }
    }
    
    @Override
    public int save(Game game) {
        String sql = "INSERT INTO Game (date, opponent, finalScore, venue, squadID) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(game.getDate()));
            
            pstmt.setString(2, game.getOpponent());
            pstmt.setString(3, game.getFinalScore());
            pstmt.setString(4, game.getVenue());
            
            if (game.getSquad() != null) {
                pstmt.setInt(5, game.getSquad().getSquadId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int gameId = generatedKeys.getInt(1);
                        game.setGameId(gameId);
                        return gameId;
                    }
                }
            }
            
            throw new RepositoryException("Creating game failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RepositoryException("Error saving game", e);
        }
    }
    
    @Override
    public boolean update(Game game) {
        String sql = "UPDATE Game SET date = ?, opponent = ?, finalScore = ?, venue = ?, " +
                     "squadID = ? WHERE gameID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(1, sdf.format(game.getDate()));
            
            pstmt.setString(2, game.getOpponent());
            pstmt.setString(3, game.getFinalScore());
            pstmt.setString(4, game.getVenue());
            
            if (game.getSquad() != null) {
                pstmt.setInt(5, game.getSquad().getSquadId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            
            pstmt.setInt(6, game.getGameId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating game", e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            
            // Delete game stats
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM GameStats WHERE gameID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // Delete game
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Game WHERE gameID = ?")) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            throw new RepositoryException("Error deleting game", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    // Log autocommit reset error
                }
            }
        }
    }
    
    @Override
    public boolean addGameStats(GameStats stats) {
        String sql = "INSERT INTO GameStats (playerID, gameID, tackles, passes, tries, kicks, " +
                     "overallRating, attended) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, stats.getPlayerId());
            pstmt.setInt(2, stats.getGameId());
            pstmt.setInt(3, stats.getTackles());
            pstmt.setInt(4, stats.getPasses());
            pstmt.setInt(5, stats.getTries());
            pstmt.setInt(6, stats.getKicks());
            pstmt.setInt(7, stats.getOverallRating());
            pstmt.setBoolean(8, stats.isAttended());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        stats.setGameStatsId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            throw new RepositoryException("Error adding game stats", e);
        }
    }
    
    @Override
    public boolean updateGameStats(GameStats stats) {
        String sql = "UPDATE GameStats SET tackles = ?, passes = ?, tries = ?, kicks = ?, " +
                     "overallRating = ?, attended = ? WHERE gameStatsID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, stats.getTackles());
            pstmt.setInt(2, stats.getPasses());
            pstmt.setInt(3, stats.getTries());
            pstmt.setInt(4, stats.getKicks());
            pstmt.setInt(5, stats.getOverallRating());
            pstmt.setBoolean(6, stats.isAttended());
            pstmt.setInt(7, stats.getGameStatsId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating game stats", e);
        }
    }

    // Private helper method that accepts a Connection
    private List<GameStats> getGameStatsInternal(Connection conn, int gameId) throws SQLException {
        List<GameStats> statsList = new ArrayList<>();
        String sql = "SELECT * FROM GameStats WHERE gameID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                GameStats stats = new GameStats();
                stats.setGameStatsId(rs.getInt("gameStatsID"));
                stats.setPlayerId(rs.getInt("playerID"));
                stats.setGameId(rs.getInt("gameID"));
                stats.setTackles(rs.getInt("tackles"));
                stats.setPasses(rs.getInt("passes"));
                stats.setTries(rs.getInt("tries"));
                stats.setKicks(rs.getInt("kicks"));
                stats.setOverallRating(rs.getInt("overallRating"));
                stats.setAttended(rs.getBoolean("attended"));

                statsList.add(stats);
            }
        }

        return statsList;
    }

    // Public method implementation required by the interface
    @Override
    public List<GameStats> getGameStats(int gameId) {
        try (Connection conn = connectionManager.getConnection()) {
            return getGameStatsInternal(conn, gameId);
        } catch (SQLException e) {
            throw new RepositoryException("Error getting game stats for game: " + gameId, e);
        }
    }

    @Override
    public List<GameStats> getStatsByPlayer(int playerId) {
        List<GameStats> statsList = new ArrayList<>();
        String sql = "SELECT * FROM GameStats WHERE playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GameStats stats = new GameStats();
                stats.setGameStatsId(rs.getInt("gameStatsID"));
                stats.setPlayerId(rs.getInt("playerID"));
                stats.setGameId(rs.getInt("gameID"));
                stats.setTackles(rs.getInt("tackles"));
                stats.setPasses(rs.getInt("passes"));
                stats.setTries(rs.getInt("tries"));
                stats.setKicks(rs.getInt("kicks"));
                stats.setOverallRating(rs.getInt("overallRating"));
                stats.setAttended(rs.getBoolean("attended"));
                
                statsList.add(stats);
            }
            
            return statsList;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting player stats", e);
        }
    }
    
    @Override
    public GameStats getPlayerGameStats(int gameId, int playerId) {
        String sql = "SELECT * FROM GameStats WHERE gameID = ? AND playerID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, playerId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                GameStats stats = new GameStats();
                stats.setGameStatsId(rs.getInt("gameStatsID"));
                stats.setPlayerId(rs.getInt("playerID"));
                stats.setGameId(rs.getInt("gameID"));
                stats.setTackles(rs.getInt("tackles"));
                stats.setPasses(rs.getInt("passes"));
                stats.setTries(rs.getInt("tries"));
                stats.setKicks(rs.getInt("kicks"));
                stats.setOverallRating(rs.getInt("overallRating"));
                stats.setAttended(rs.getBoolean("attended"));
                
                return stats;
            }
            
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting player game stats", e);
        }
    }
    
    @Override
    public List<Game> findUpcomingGames() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                     "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                     "WHERE g.date >= date('now') " +
                     "ORDER BY g.date ASC";
        
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
            
            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }
            
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding upcoming games", e);
        }
    }
    
    @Override
    public List<Game> findRecentGames(int limit) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.*, s.squadName, s.ageGrade FROM Game g " +
                     "LEFT JOIN Squad s ON g.squadID = s.squadID " +
                     "WHERE g.date < date('now') " +
                     "ORDER BY g.date DESC " +
                     "LIMIT ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
            
            // Load game stats for each game
            for (Game game : games) {
                game.setGameStats(getGameStats(game.getGameId()));
            }
            
            return games;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding recent games", e);
        }
    }
    
    @Override
    public int[] getSquadRecord(int squadId) {
        int[] record = new int[3]; // [wins, losses, draws]
        
        String sql = "SELECT finalScore FROM Game WHERE squadID = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, squadId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String finalScore = rs.getString("finalScore");
                
                if (finalScore != null && !finalScore.isEmpty()) {
                    try {
                        String[] scores = finalScore.split(" - ");
                        int ourScore = Integer.parseInt(scores[0]);
                        int theirScore = Integer.parseInt(scores[1]);
                        
                        if (ourScore > theirScore) {
                            record[0]++; // Win
                        } else if (ourScore < theirScore) {
                            record[1]++; // Loss
                        } else {
                            record[2]++; // Draw
                        }
                    } catch (Exception e) {
                        // Skip games with invalid scores
                    }
                }
            }
            
            return record;
        } catch (SQLException e) {
            throw new RepositoryException("Error getting squad record", e);
        }
    }
    
    // Helper method to map ResultSet to Game object
    private Game mapResultSetToGame(ResultSet rs) throws SQLException {
        Game game = new Game();
        game.setGameId(rs.getInt("gameID"));
        
        // Parse date from string
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date gameDate = sdf.parse(rs.getString("date"));
            game.setDate(gameDate);
        } catch (ParseException e) {
            throw new SQLException("Error parsing game date", e);
        }
        
        game.setOpponent(rs.getString("opponent"));
        game.setFinalScore(rs.getString("finalScore"));
        game.setVenue(rs.getString("venue"));
        
        // Set squad if available
        int squadId = rs.getInt("squadID");
        if (!rs.wasNull()) {
            Squad squad = new Squad();
            squad.setSquadId(squadId);
            squad.setSquadName(rs.getString("squadName"));
            squad.setAgeGrade(rs.getString("ageGrade"));
            game.setSquad(squad);
        }
        
        return game;
    }
}