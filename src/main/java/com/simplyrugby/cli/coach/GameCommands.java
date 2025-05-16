package com.simplyrugby.cli.coach;

import com.simplyrugby.domain.Game;
import com.simplyrugby.domain.GameStats;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.service.GameService;
import com.simplyrugby.service.PlayerService;
import com.simplyrugby.service.SquadService;
import com.simplyrugby.util.DateUtil;
import com.simplyrugby.util.DependencyManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
    name = "games",
    description = "Manage games",
    subcommands = {
        GameCommands.ListCommand.class,
        GameCommands.AddCommand.class,
        GameCommands.ViewCommand.class,
        GameCommands.RecordStatsCommand.class,
        CommandLine.HelpCommand.class
    },
    mixinStandardHelpOptions = true
)
public class GameCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("           GAMES PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  list        - List all games");
        System.out.println("  add         - Add a new game");
        System.out.println("  view        - View game details");
        System.out.println("  recordstats - Record player statistics for a game");
        System.out.println("  help        - Show help\n");
    }

    @Command(name = "list", description = "List all games")
    static class ListCommand implements Callable<Integer> {
        @Option(names = {"-s", "--squad"}, description = "Filter by squad ID")
        Integer squadId;
        
        @Option(names = {"-u", "--upcoming"}, description = "Show only upcoming games")
        boolean upcomingOnly;
        
        @Option(names = {"-r", "--recent"}, description = "Show only recent games")
        boolean recentOnly;

        @Override
        public Integer call() {
            GameService gameService = DependencyManager.getGameService();
            SquadService squadService = DependencyManager.getSquadService();
            
            try {
                List<Game> games;
                
                if (squadId != null) {
                    // Get squad name for display
                    Squad squad = squadService.getSquadById(squadId);
                    System.out.printf("\n=== Games for %s (%s) ===\n\n", 
                                     squad.getSquadName(), squad.getAgeGrade());
                    games = gameService.getGamesBySquad(squadId);
                } else {
                    System.out.println("\n=== All Games ===\n");
                    games = gameService.getAllGames();
                }
                
                // Filter by date if requested
                Date today = new Date();
                if (upcomingOnly) {
                    games.removeIf(game -> game.getDate().before(today));
                } else if (recentOnly) {
                    games.removeIf(game -> game.getDate().after(today));
                }
                
                // Display games table
                if (games.isEmpty()) {
                    System.out.println("No games found.");
                } else {
                    System.out.printf("%-4s %-12s %-20s %-10s %-20s %-20s\n", 
                                     "ID", "Date", "Opponent", "Score", "Venue", "Squad");
                    System.out.println("-".repeat(90));
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    
                    for (Game game : games) {
                        String squadName = game.getSquad() != null ? 
                                         game.getSquad().getSquadName() : "Unassigned";
                        
                        System.out.printf("%-4d %-12s %-20s %-10s %-20s %-20s\n",
                                         game.getGameId(),
                                         sdf.format(game.getDate()),
                                         game.getOpponent(),
                                         game.getFinalScore() != null ? game.getFinalScore() : "N/A",
                                         game.getVenue(),
                                         squadName);
                    }
                    System.out.println();
                }
                
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "add", description = "Add a new game")
    static class AddCommand implements Callable<Integer> {
        @Option(names = {"-d", "--date"}, description = "Game date (DD/MM/YYYY)", required = true)
        String date;
        
        @Option(names = {"-o", "--opponent"}, description = "Opponent team", required = true)
        String opponent;
        
        @Option(names = {"-s", "--score"}, description = "Final score (e.g., '21 - 15')")
        String finalScore;
        
        @Option(names = {"-v", "--venue"}, description = "Game venue", required = true)
        String venue;
        
        @Option(names = {"-q", "--squad"}, description = "Squad ID", required = true)
        int squadId;

        @Override
        public Integer call() {
            GameService gameService = DependencyManager.getGameService();
            SquadService squadService = DependencyManager.getSquadService();
            
            try {
                Game game = new Game();
                
                // Parse date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    Date gameDate = sdf.parse(date);
                    game.setDate(gameDate);
                } catch (ParseException e) {
                    System.err.println("Error: Invalid date format. Please use DD/MM/YYYY.");
                    return 1;
                }
                
                game.setOpponent(opponent);
                game.setFinalScore(finalScore);
                game.setVenue(venue);
                
                // Get squad
                Squad squad = squadService.getSquadById(squadId);
                game.setSquad(squad);
                
                // Save game
                int gameId = gameService.addGame(game);
                
                System.out.println("\nGame added successfully with ID: " + gameId);
                System.out.println("You can now record player statistics with the command:");
                System.out.println("  games recordstats " + gameId);
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "view", description = "View game details")
    static class ViewCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Game ID")
        int gameId;

        @Override
        public Integer call() {
            GameService gameService = DependencyManager.getGameService();
            
            try {
                Game game = gameService.getGameById(gameId);
                
                System.out.println("\n=== Game Details ===\n");
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                
                System.out.println("ID:       " + game.getGameId());
                System.out.println("Date:     " + sdf.format(game.getDate()));
                System.out.println("Opponent: " + game.getOpponent());
                System.out.println("Score:    " + (game.getFinalScore() != null ? game.getFinalScore() : "N/A"));
                System.out.println("Venue:    " + game.getVenue());
                
                if (game.getSquad() != null) {
                    System.out.println("Squad:    " + game.getSquad().getSquadName() + 
                                       " (" + game.getSquad().getAgeGrade() + ")");
                } else {
                    System.out.println("Squad:    Unassigned");
                }
                
                // Display player stats if available
                List<GameStats> stats = game.getGameStats();
                
                if (stats != null && !stats.isEmpty()) {
                    System.out.println("\n=== Player Statistics ===\n");
                    
                    System.out.printf("%-20s %-10s %-10s %-10s %-10s %-10s %-10s\n", 
                                     "Player", "Attended", "Tackles", "Passes", "Tries", "Kicks", "Overall");
                    System.out.println("-".repeat(85));
                    
                    PlayerService playerService = DependencyManager.getPlayerService();
                    
                    for (GameStats stat : stats) {
                        Player player = playerService.getPlayerById(stat.getPlayerId());
                        
                        System.out.printf("%-20s %-10s %-10d %-10d %-10d %-10d %-10d\n",
                                         player.getFirstName() + " " + player.getLastName(),
                                         stat.isAttended() ? "Yes" : "No",
                                         stat.isAttended() ? stat.getTackles() : 0,
                                         stat.isAttended() ? stat.getPasses() : 0,
                                         stat.isAttended() ? stat.getTries() : 0,
                                         stat.isAttended() ? stat.getKicks() : 0,
                                         stat.isAttended() ? stat.getOverallRating() : 0);
                    }
                    
                    // Display attendance summary
                    int present = (int) stats.stream().filter(GameStats::isAttended).count();
                    int total = stats.size();
                    int absent = total - present;
                    
                    System.out.println("\nAttendance Summary:");
                    System.out.println("Present: " + present);
                    System.out.println("Absent:  " + absent);
                    System.out.println("Total:   " + total);
                    
                    if (total > 0) {
                        double rate = (double) present / total * 100;
                        System.out.printf("Attendance Rate: %.1f%%\n", rate);
                    }
                } else {
                    System.out.println("\nNo player statistics recorded for this game.");
                }
                
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "recordstats", description = "Record player statistics for a game")
    static class RecordStatsCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Game ID")
        int gameId;
        
        @Option(names = {"-i", "--interactive"}, description = "Interactive mode")
        boolean interactive = true;

        @Override
        public Integer call() {
            GameService gameService = DependencyManager.getGameService();
            PlayerService playerService = DependencyManager.getPlayerService();
            
            try {
                Game game = gameService.getGameById(gameId);
                
                if (game.getSquad() == null) {
                    System.err.println("Error: Game has no assigned squad.");
                    return 1;
                }
                
                System.out.printf("\n=== Recording Stats for Game vs %s on %s ===\n\n", 
                                game.getOpponent(), 
                                new SimpleDateFormat("dd/MM/yyyy").format(game.getDate()));
                
                // Get players in the squad
                List<Player> players = playerService.getPlayersBySquad(game.getSquad().getSquadId());
                
                if (players.isEmpty()) {
                    System.out.println("No players found in this squad.");
                    return 0;
                }
                
                if (interactive) {
                    // Interactive mode - prompt for each player
                    for (Player player : players) {
                        System.out.printf("\nRecording stats for %s %s:\n", 
                                         player.getFirstName(), player.getLastName());
                        
                        System.out.print("Did the player attend? (y/n): ");
                        String attended = System.console().readLine().trim().toLowerCase();
                        
                        GameStats stats = new GameStats();
                        stats.setPlayerId(player.getPlayerId());
                        stats.setGameId(gameId);
                        stats.setAttended(attended.equals("y") || attended.equals("yes"));
                        
                        if (stats.isAttended()) {
                            System.out.print("Tackles (0-10): ");
                            stats.setTackles(Integer.parseInt(System.console().readLine().trim()));
                            
                            System.out.print("Passes (0-10): ");
                            stats.setPasses(Integer.parseInt(System.console().readLine().trim()));
                            
                            System.out.print("Tries (0-10): ");
                            stats.setTries(Integer.parseInt(System.console().readLine().trim()));
                            
                           System.out.print("Kicks (0-10): ");
                            stats.setKicks(Integer.parseInt(System.console().readLine().trim()));
                            
                            // Calculate overall rating
                            stats.calculateOverallRating();
                            System.out.println("Overall Rating: " + stats.getOverallRating());
                        }
                        
                        // Save stats to database
                        boolean result = gameService.addGameStats(stats);
                        
                        if (result) {
                            System.out.printf("Stats recorded successfully for %s %s.\n",
                                             player.getFirstName(), player.getLastName());
                        } else {
                            System.out.printf("Failed to record stats for %s %s.\n",
                                             player.getFirstName(), player.getLastName());
                        }
                    }
                } else {
                    // Batch mode - not implemented in this example
                    System.out.println("Batch mode not implemented. Please use interactive mode.");
                    return 1;
                }
                
                System.out.println("\nAll player statistics recorded successfully.");
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }
}