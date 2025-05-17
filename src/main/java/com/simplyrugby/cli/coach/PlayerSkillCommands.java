package com.simplyrugby.cli.coach;

import com.simplyrugby.domain.GameStats;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.service.GameService;
import com.simplyrugby.service.PlayerService;
import com.simplyrugby.service.SquadService;
import com.simplyrugby.service.TrainingService;
import com.simplyrugby.util.DependencyManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "skills",
        description = "View and track player skills",
        subcommands = {
                PlayerSkillCommands.ViewCommand.class,
                PlayerSkillCommands.ProgressCommand.class,
                CommandLine.HelpCommand.class
        },
        mixinStandardHelpOptions = true
)
public class PlayerSkillCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("        PLAYER SKILLS PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  view     - View player skills");
        System.out.println("  progress - View player progress over time");
        System.out.println("  help     - Show help\n");
    }

    @Command(name = "view", description = "View player skills")
    static class ViewCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Player ID")
        int playerId;

        @Override
        public Integer call() {
            PlayerService playerService = DependencyManager.getPlayerService();
            GameService gameService = DependencyManager.getGameService();

            try {
                Player player = playerService.getPlayerById(playerId);

                System.out.printf("\n=== Skills for %s %s ===\n\n",
                        player.getFirstName(), player.getLastName());

                List<GameStats> stats = playerService.getPlayerGameStats(playerId);

                if (stats.isEmpty()) {
                    System.out.println("No game statistics recorded for this player.");
                } else {
                    System.out.printf("%-4s %-12s %-10s %-10s %-10s %-10s %-10s\n",
                            "Game", "Date", "Tackles", "Passes", "Tries", "Kicks", "Overall");
                    System.out.println("-".repeat(75));

                    for (GameStats stat : stats) {
                        if (!stat.isAttended()) {
                            continue; // Skip games the player didn't attend
                        }

                        // Get game details
                        var game = gameService.getGameById(stat.getGameId());
                        String gameDate = new java.text.SimpleDateFormat("dd/MM/yyyy").format(game.getDate());

                        System.out.printf("%-4d %-12s %-10d %-10d %-10d %-10d %-10d\n",
                                game.getGameId(),
                                gameDate,
                                stat.getTackles(),
                                stat.getPasses(),
                                stat.getTries(),
                                stat.getKicks(),
                                stat.getOverallRating());
                    }

                    // Show average ratings
                    System.out.println("\nStrength areas:");
                    stats.stream()
                            .filter(GameStats::isAttended)
                            .map(GameStats::getStrongestSkill)
                            .distinct()
                            .forEach(skill -> System.out.println("- " + skill));

                    System.out.println("\nAreas for improvement:");
                    stats.stream()
                            .filter(GameStats::isAttended)
                            .map(GameStats::getWeakestSkill)
                            .distinct()
                            .forEach(skill -> System.out.println("- " + skill));

                    // Calculate overall skill rating
                    double overallRating = player.calculateOverallSkillRating();
                    System.out.printf("\nOverall Player Rating: %.1f/10\n", overallRating);
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "progress", description = "View player progress over time")
    static class ProgressCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Player ID")
        int playerId;

        @Option(names = {"-g", "--games"}, description = "Show game progress")
        boolean showGames = true;

        @Option(names = {"-t", "--training"}, description = "Show training attendance")
        boolean showTraining = true;

        @Override
        public Integer call() {
            PlayerService playerService = DependencyManager.getPlayerService();
            TrainingService trainingService = DependencyManager.getTrainingService();

            try {
                Player player = playerService.getPlayerById(playerId);

                System.out.printf("\n=== Progress Report for %s %s ===\n\n",
                        player.getFirstName(), player.getLastName());

                if (showGames) {
                    // Show game progress
                    List<GameStats> stats = playerService.getPlayerGameStats(playerId);

                    System.out.println("Game Performance Progress:");

                    if (stats.isEmpty()) {
                        System.out.println("No game statistics recorded for this player.");
                    } else {
                        // Calculate progress over time
                        int gamesPlayed = player.getGamesAttended();
                        double overallRating = player.calculateOverallSkillRating();

                        System.out.printf("Games Played: %d\n", gamesPlayed);
                        System.out.printf("Overall Rating: %.1f/10\n", overallRating);

                        // Show trend graph (simplified for CLI)
                        System.out.println("\nSkill Progress Trend:");
                        showTrendGraph(stats);
                    }
                }

                if (showTraining) {
                    // Show training attendance
                    List<TrainingAttendance> attendance = playerService.getPlayerTrainingAttendance(playerId);

                    System.out.println("\nTraining Attendance:");

                    if (attendance.isEmpty()) {
                        System.out.println("No training attendance recorded for this player.");
                    } else {
                        double attendanceRate = player.calculateTrainingAttendanceRate();
                        int sessionsAttended = (int) attendance.stream().filter(TrainingAttendance::isPresent).count();
                        int totalSessions = attendance.size();

                        System.out.printf("Sessions Attended: %d/%d (%.1f%%)\n",
                                sessionsAttended, totalSessions, attendanceRate);
                    }
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }

        // Simple ASCII chart to show progress trend
        private void showTrendGraph(List<GameStats> stats) {
            // Sort stats by game date
            stats.sort((a, b) -> {
                try {
                    var gameService = DependencyManager.getGameService();
                    var gameA = gameService.getGameById(a.getGameId());
                    var gameB = gameService.getGameById(b.getGameId());
                    return gameA.getDate().compareTo(gameB.getDate());
                } catch (Exception e) {
                    return 0;
                }
            });

            // Show only attended games
            var attendedStats = stats.stream()
                    .filter(GameStats::isAttended)
                    .toList();

            if (attendedStats.isEmpty()) {
                System.out.println("No games attended.");
                return;
            }

            // Create simple ASCII chart
            for (int i = 10; i >= 1; i--) {
                System.out.print(i + " |");
                for (var stat : attendedStats) {
                    if (stat.getOverallRating() >= i) {
                        System.out.print(" #");
                    } else {
                        System.out.print("  ");
                    }
                }
                System.out.println();
            }

            // Print x-axis
            System.out.print("   ");
            for (int i = 0; i < attendedStats.size(); i++) {
                System.out.print("--");
            }
            System.out.println();

            System.out.print("   ");
            for (int i = 0; i < attendedStats.size(); i++) {
                System.out.print(" " + (i+1));
            }
            System.out.println(" (Game #)");
        }
    }
}