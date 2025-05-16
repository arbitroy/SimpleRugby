package com.simplyrugby.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a rugby game/match.
 */
public class Game {
    private int gameId;
    private Date date;
    private String opponent;
    private String finalScore;
    private String venue;
    private Squad squad;
    private List<GameStats> gameStats = new ArrayList<>();

    /**
     * Default constructor
     */
    public Game() {
    }

    /**
     * Constructor with all fields
     */
    public Game(int gameId, Date date, String opponent, String finalScore, String venue, Squad squad) {
        this.gameId = gameId;
        this.date = date;
        this.opponent = opponent;
        this.finalScore = finalScore;
        this.venue = venue;
        this.squad = squad;
    }

    // Getters and Setters

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Squad getSquad() {
        return squad;
    }

    public void setSquad(Squad squad) {
        this.squad = squad;
    }

    public List<GameStats> getGameStats() {
        return gameStats;
    }

    public void setGameStats(List<GameStats> gameStats) {
        this.gameStats = gameStats != null ? gameStats : new ArrayList<>();
    }

    /**
     * Adds player statistics for this game
     * 
     * @param stats The player statistics to add
     */
    public void addPlayerStats(GameStats stats) {
        if (this.gameStats == null) {
            this.gameStats = new ArrayList<>();
        }

        // Check if player already has stats for this game
        for (int i = 0; i < gameStats.size(); i++) {
            if (gameStats.get(i).getPlayerId() == stats.getPlayerId()) {
                gameStats.set(i, stats); // Replace existing stats
                return;
            }
        }

        gameStats.add(stats);
    }

    /**
     * Gets the statistics for a specific player
     * 
     * @param playerId The ID of the player
     * @return The player's statistics, or null if not found
     */
    public GameStats getPlayerStats(int playerId) {
        if (gameStats == null) {
            return null;
        }

        for (GameStats stats : gameStats) {
            if (stats.getPlayerId() == playerId) {
                return stats;
            }
        }

        return null;
    }

    /**
     * Gets the list of players who attended the game
     * 
     * @return List of player IDs who attended
     */
    public List<Integer> getAttendingPlayerIds() {
        List<Integer> attendingPlayers = new ArrayList<>();

        if (gameStats == null) {
            return attendingPlayers;
        }

        for (GameStats stats : gameStats) {
            if (stats.isAttended()) {
                attendingPlayers.add(stats.getPlayerId());
            }
        }

        return attendingPlayers;
    }

    /**
     * Gets the attendance rate for this game
     * 
     * @return The attendance rate as a percentage
     */
    public double getAttendanceRate() {
        if (gameStats == null || gameStats.isEmpty()) {
            return 0.0;
        }

        int attended = 0;
        for (GameStats stats : gameStats) {
            if (stats.isAttended()) {
                attended++;
            }
        }

        return (double) attended / gameStats.size() * 100.0;
    }

    /**
     * Determines if this game is in the future
     * 
     * @return true if the game is upcoming, false otherwise
     */
    public boolean isUpcoming() {
        if (date == null) {
            return false;
        }

        return date.after(new Date());
    }

    /**
     * Parses the final score and returns the home team's score
     * 
     * @return The home team's score, or -1 if not available
     */
    public int getHomeScore() {
        if (finalScore == null || finalScore.isEmpty()) {
            return -1;
        }

        try {
            String[] scores = finalScore.split(" - ");
            return Integer.parseInt(scores[0]);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Parses the final score and returns the away team's score
     * 
     * @return The away team's score, or -1 if not available
     */
    public int getAwayScore() {
        if (finalScore == null || finalScore.isEmpty()) {
            return -1;
        }

        try {
            String[] scores = finalScore.split(" - ");
            return Integer.parseInt(scores[1]);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Determines if the game was won
     * 
     * @return true if the game was won, false if lost or drawn
     */
    public boolean isWin() {
        int home = getHomeScore();
        int away = getAwayScore();

        return home > away && home != -1 && away != -1;
    }

    /**
     * Determines if the game was lost
     * 
     * @return true if the game was lost, false if won or drawn
     */
    public boolean isLoss() {
        int home = getHomeScore();
        int away = getAwayScore();

        return home < away && home != -1 && away != -1;
    }

    /**
     * Determines if the game was drawn
     * 
     * @return true if the game was drawn, false otherwise
     */
    public boolean isDraw() {
        int home = getHomeScore();
        int away = getAwayScore();

        return home == away && home != -1 && away != -1;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", date=" + date +
                ", opponent='" + opponent + '\'' +
                ", finalScore='" + finalScore + '\'' +
                ", venue='" + venue + '\'' +
                ", squad=" + (squad != null ? squad.getSquadName() : "None") +
                '}';
    }
}