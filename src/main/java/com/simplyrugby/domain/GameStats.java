package com.simplyrugby.domain;

/**
 * Represents a player's statistics for a specific game.
 */
public class GameStats {
    private int gameStatsId;
    private int playerId;
    private int gameId;
    private int tackles;
    private int passes;
    private int tries;
    private int kicks;
    private int overallRating;
    private boolean attended;

    /**
     * Default constructor
     */
    public GameStats() {
        this.tackles = 0;
        this.passes = 0;
        this.tries = 0;
        this.kicks = 0;
        this.overallRating = 0;
        this.attended = false;
    }

    /**
     * Constructor with all fields
     */
    public GameStats(int gameStatsId, int playerId, int gameId, int tackles, int passes,
            int tries, int kicks, int overallRating, boolean attended) {
        this.gameStatsId = gameStatsId;
        this.playerId = playerId;
        this.gameId = gameId;
        this.tackles = tackles;
        this.passes = passes;
        this.tries = tries;
        this.kicks = kicks;
        this.overallRating = overallRating;
        this.attended = attended;
    }

    // Getters and Setters

    public int getGameStatsId() {
        return gameStatsId;
    }

    public void setGameStatsId(int gameStatsId) {
        this.gameStatsId = gameStatsId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getTackles() {
        return tackles;
    }

    public void setTackles(int tackles) {
        if (tackles >= 0 && tackles <= 10) {
            this.tackles = tackles;
        } else {
            throw new IllegalArgumentException("Tackles must be between 0 and 10");
        }
    }

    public int getPasses() {
        return passes;
    }

    public void setPasses(int passes) {
        if (passes >= 0 && passes <= 10) {
            this.passes = passes;
        } else {
            throw new IllegalArgumentException("Passes must be between 0 and 10");
        }
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        if (tries >= 0 && tries <= 10) {
            this.tries = tries;
        } else {
            throw new IllegalArgumentException("Tries must be between 0 and 10");
        }
    }

    public int getKicks() {
        return kicks;
    }

    public void setKicks(int kicks) {
        if (kicks >= 0 && kicks <= 10) {
            this.kicks = kicks;
        } else {
            throw new IllegalArgumentException("Kicks must be between 0 and 10");
        }
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        if (overallRating >= 0 && overallRating <= 10) {
            this.overallRating = overallRating;
        } else {
            throw new IllegalArgumentException("Overall rating must be between 0 and 10");
        }
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    /**
     * Calculates the overall rating based on individual skills
     */
    public void calculateOverallRating() {
        // Simple average of all skills (rounded to nearest integer)
        this.overallRating = Math.round((tackles + passes + tries + kicks) / 4.0f);
    }

    /**
     * Returns the highest rated skill
     * 
     * @return The name of the highest rated skill
     */
    public String getStrongestSkill() {
        int max = Math.max(Math.max(tackles, passes), Math.max(tries, kicks));

        if (max == tackles)
            return "Tackles";
        if (max == passes)
            return "Passes";
        if (max == tries)
            return "Tries";
        if (max == kicks)
            return "Kicks";

        return "None";
    }

    /**
     * Returns the lowest rated skill
     * 
     * @return The name of the lowest rated skill
     */
    public String getWeakestSkill() {
        int min = Math.min(Math.min(tackles, passes), Math.min(tries, kicks));

        if (min == tackles)
            return "Tackles";
        if (min == passes)
            return "Passes";
        if (min == tries)
            return "Tries";
        if (min == kicks)
            return "Kicks";

        return "None";
    }

    @Override
    public String toString() {
        return "GameStats{" +
                "gameStatsId=" + gameStatsId +
                ", playerId=" + playerId +
                ", gameId=" + gameId +
                ", tackles=" + tackles +
                ", passes=" + passes +
                ", tries=" + tries +
                ", kicks=" + kicks +
                ", overallRating=" + overallRating +
                ", attended=" + attended +
                '}';
    }
}