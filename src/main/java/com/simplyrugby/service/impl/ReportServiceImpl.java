package com.simplyrugby.service.impl;

import com.simplyrugby.domain.Game;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Report;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.domain.Training;
import com.simplyrugby.repository.ReportRepository;
import com.simplyrugby.service.GameService;
import com.simplyrugby.service.PlayerService;
import com.simplyrugby.service.ReportService;
import com.simplyrugby.service.SquadService;
import com.simplyrugby.service.TrainingService;
import com.simplyrugby.util.DateUtil;
import com.simplyrugby.util.DependencyManager;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report getReportById(int id) {
        Report report = reportRepository.findById(id);
        if (report == null) {
            throw new EntityNotFoundException("Report not found with ID: " + id);
        }
        return report;
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public List<Report> getReportsByTitle(String title) {
        return reportRepository.findByTitle(title);
    }

    @Override
    public List<Report> getReportsByType(String reportType) {
        return reportRepository.findByType(reportType);
    }

    @Override
    public List<Report> getReportsByGenerator(String generatedBy) {
        return reportRepository.findByGeneratedBy(generatedBy);
    }

    @Override
    public List<Report> getReportsAfterDate(Date date) {
        return reportRepository.findReportsAfterDate(date);
    }

    @Override
    public List<Report> getRecentReports(int limit) {
        return reportRepository.findRecentReports(limit);
    }

    @Override
    public int addReport(Report report) {
        validateReport(report);
        return reportRepository.save(report);
    }

    @Override
    public boolean updateReport(Report report) {
        if (reportRepository.findById(report.getReportId()) == null) {
            throw new EntityNotFoundException("Report not found with ID: " + report.getReportId());
        }
        validateReport(report);
        return reportRepository.update(report);
    }

    @Override
    public boolean deleteReport(int id) {
        if (reportRepository.findById(id) == null) {
            throw new EntityNotFoundException("Report not found with ID: " + id);
        }
        return reportRepository.delete(id);
    }

    @Override
    public String generateAttendanceReport(Integer squadId, String dateRange) {
        StringBuilder report = new StringBuilder();
        TrainingService trainingService = DependencyManager.getTrainingService();
        SquadService squadService = DependencyManager.getSquadService();
        PlayerService playerService = DependencyManager.getPlayerService();

        try {
            // Report title and parameters
            report.append("ATTENDANCE REPORT\n");
            report.append("=================\n\n");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date();
            report.append("Generated on: ").append(sdf.format(currentDate)).append("\n\n");

            // If squadId is provided, include squad details
            if (squadId != null) {
                Squad squad = squadService.getSquadById(squadId);
                report.append("Squad: ").append(squad.getSquadName()).append(" (").append(squad.getAgeGrade()).append(")\n\n");

                // Get all players in the squad
                List<Player> players = playerService.getPlayersBySquad(squadId);

                // Get all training sessions for the squad
                List<Training> trainings = trainingService.getTrainingSessionsBySquad(squadId);

                // If date range is provided, filter training sessions
                if (dateRange != null) {
                    // Parse date range
                    String[] dates = dateRange.split("-");
                    if (dates.length == 2) {
                        try {
                            Date startDate = sdf.parse(dates[0]);
                            Date endDate = sdf.parse(dates[1]);
                            trainings.removeIf(t -> t.getDate().before(startDate) || t.getDate().after(endDate));

                            report.append("Date Range: ").append(sdf.format(startDate)).append(" to ").append(sdf.format(endDate)).append("\n\n");
                        } catch (Exception e) {
                            report.append("Invalid date range format. Should be DD/MM/YYYY-DD/MM/YYYY\n\n");
                        }
                    }
                }

                // Training session summary
                report.append("Training Sessions: ").append(trainings.size()).append("\n\n");

                if (!trainings.isEmpty()) {
                    // Table header for training sessions
                    report.append(String.format("%-4s %-12s %-30s %-10s\n", "ID", "Date", "Focus Areas", "Attendance"));
                    report.append("-".repeat(60)).append("\n");

                    for (Training training : trainings) {
                        report.append(String.format("%-4d %-12s %-30s %.1f%%\n",
                                training.getTrainingId(),
                                sdf.format(training.getDate()),
                                truncate(training.getFocusAreas(), 30),
                                training.getAttendanceRate()));
                    }
                    report.append("\n");

                    // Player attendance summary
                    report.append("Player Attendance Summary:\n");
                    report.append(String.format("%-20s %-15s %-15s\n", "Player", "Attendance", "Rate"));
                    report.append("-".repeat(55)).append("\n");

                    for (Player player : players) {
                        double attendanceRate = trainingService.getPlayerAttendanceRate(player.getPlayerId());
                        List<Training> attendedTrainings = new ArrayList<>();

                        for (Training training : trainings) {
                            if (training.getPlayerAttendance(player.getPlayerId()) != null &&
                                    training.getPlayerAttendance(player.getPlayerId()).isPresent()) {
                                attendedTrainings.add(training);
                            }
                        }

                        report.append(String.format("%-20s %-15s %.1f%%\n",
                                player.getFirstName() + " " + player.getLastName(),
                                attendedTrainings.size() + "/" + trainings.size(),
                                attendanceRate));
                    }
                } else {
                    report.append("No training sessions found for the specified criteria.\n");
                }
            } else {
                // Overall attendance report
                report.append("Overall Attendance Report\n\n");

                // Get all squads
                List<Squad> squads = squadService.getAllSquads();

                for (Squad squad : squads) {
                    report.append("Squad: ").append(squad.getSquadName()).append(" (").append(squad.getAgeGrade()).append(")\n");

                    // Get training sessions for this squad
                    List<Training> squadTrainings = trainingService.getTrainingSessionsBySquad(squad.getSquadId());

                    if (!squadTrainings.isEmpty()) {
                        // Calculate average attendance rate
                        double totalRate = 0;
                        for (Training training : squadTrainings) {
                            totalRate += training.getAttendanceRate();
                        }
                        double averageRate = totalRate / squadTrainings.size();

                        report.append("Training Sessions: ").append(squadTrainings.size()).append("\n");
                        report.append("Average Attendance Rate: ").append(String.format("%.1f%%", averageRate)).append("\n\n");
                    } else {
                        report.append("No training sessions recorded.\n\n");
                    }
                }
            }

            return report.toString();
        } catch (Exception e) {
            return "Error generating attendance report: " + e.getMessage();
        }
    }

    @Override
    public String generateSkillsReport(Integer playerId, Integer squadId) {
        StringBuilder report = new StringBuilder();
        PlayerService playerService = DependencyManager.getPlayerService();
        SquadService squadService = DependencyManager.getSquadService();
        GameService gameService = DependencyManager.getGameService();

        try {
            // Report title and parameters
            report.append("PLAYER SKILLS REPORT\n");
            report.append("====================\n\n");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date();
            report.append("Generated on: ").append(sdf.format(currentDate)).append("\n\n");

            if (playerId != null) {
                // Individual player report
                Player player = playerService.getPlayerById(playerId);
                report.append("Player: ").append(player.getFirstName()).append(" ").append(player.getLastName()).append("\n");
                report.append("Position: ").append(player.getPosition()).append("\n");
                if (player.getSquad() != null) {
                    report.append("Squad: ").append(player.getSquad().getSquadName()).append(" (").append(player.getSquad().getAgeGrade()).append(")\n");
                }
                report.append("\n");

                // Get player game stats
                List<com.simplyrugby.domain.GameStats> gameStats = playerService.getPlayerGameStats(playerId);

                if (!gameStats.isEmpty()) {
                    report.append("Game Statistics:\n");
                    report.append(String.format("%-4s %-12s %-10s %-10s %-10s %-10s %-10s\n",
                            "Game", "Date", "Tackles", "Passes", "Tries", "Kicks", "Overall"));
                    report.append("-".repeat(75)).append("\n");

                    for (com.simplyrugby.domain.GameStats stats : gameStats) {
                        if (!stats.isAttended()) {
                            continue; // Skip games not attended
                        }

                        Game game = gameService.getGameById(stats.getGameId());

                        report.append(String.format("%-4d %-12s %-10d %-10d %-10d %-10d %-10d\n",
                                game.getGameId(),
                                sdf.format(game.getDate()),
                                stats.getTackles(),
                                stats.getPasses(),
                                stats.getTries(),
                                stats.getKicks(),
                                stats.getOverallRating()));
                    }

                    // Calculate averages
                    double avgTackles = gameStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getTackles).average().orElse(0);
                    double avgPasses = gameStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getPasses).average().orElse(0);
                    double avgTries = gameStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getTries).average().orElse(0);
                    double avgKicks = gameStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getKicks).average().orElse(0);
                    double overallRating = player.calculateOverallSkillRating();

                    report.append("\nAverage Ratings:\n");
                    report.append(String.format("Tackles: %.1f\n", avgTackles));
                    report.append(String.format("Passes: %.1f\n", avgPasses));
                    report.append(String.format("Tries: %.1f\n", avgTries));
                    report.append(String.format("Kicks: %.1f\n", avgKicks));
                    report.append(String.format("Overall: %.1f\n", overallRating));

                    // Training attendance
                    double attendanceRate = playerService.calculateTrainingAttendanceRate(playerId);
                    report.append("\nTraining Attendance Rate: ").append(String.format("%.1f%%", attendanceRate)).append("\n");
                } else {
                    report.append("No game statistics recorded for this player.\n");
                }
            } else if (squadId != null) {
                // Squad skills report
                Squad squad = squadService.getSquadById(squadId);
                report.append("Squad: ").append(squad.getSquadName()).append(" (").append(squad.getAgeGrade()).append(")\n\n");

                // Get all players in the squad
                List<Player> players = playerService.getPlayersBySquad(squadId);

                if (!players.isEmpty()) {
                    report.append("Player Skills Summary:\n");
                    report.append(String.format("%-20s %-10s %-10s %-10s %-10s %-10s %-15s\n",
                            "Player", "Tackles", "Passes", "Tries", "Kicks", "Overall", "Attendance"));
                    report.append("-".repeat(90)).append("\n");

                    for (Player player : players) {
                        double overallRating = player.calculateOverallSkillRating();
                        double attendanceRate = playerService.calculateTrainingAttendanceRate(player.getPlayerId());

                        // Calculate skill averages
                        List<com.simplyrugby.domain.GameStats> playerStats = playerService.getPlayerGameStats(player.getPlayerId());
                        double avgTackles = playerStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getTackles).average().orElse(0);
                        double avgPasses = playerStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getPasses).average().orElse(0);
                        double avgTries = playerStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getTries).average().orElse(0);
                        double avgKicks = playerStats.stream().filter(com.simplyrugby.domain.GameStats::isAttended).mapToInt(com.simplyrugby.domain.GameStats::getKicks).average().orElse(0);

                        report.append(String.format("%-20s %-10.1f %-10.1f %-10.1f %-10.1f %-10.1f %-15.1f%%\n",
                                player.getFirstName() + " " + player.getLastName(),
                                avgTackles,
                                avgPasses,
                                avgTries,
                                avgKicks,
                                overallRating,
                                attendanceRate));
                    }

                    // Squad averages
                    double squadAvgRating = players.stream().mapToDouble(Player::calculateOverallSkillRating).average().orElse(0);
                    double squadAvgAttendance = players.stream().mapToDouble(p -> playerService.calculateTrainingAttendanceRate(p.getPlayerId())).average().orElse(0);

                    report.append("\nSquad Averages:\n");
                    report.append(String.format("Overall Rating: %.1f\n", squadAvgRating));
                    report.append(String.format("Attendance Rate: %.1f%%\n", squadAvgAttendance));
                } else {
                    report.append("No players found in this squad.\n");
                }
            } else {
                // General skills report across all squads
                report.append("Overall Skills Report\n\n");

                // Get all squads
                List<Squad> squads = squadService.getAllSquads();

                for (Squad squad : squads) {
                    report.append("Squad: ").append(squad.getSquadName()).append(" (").append(squad.getAgeGrade()).append(")\n");

                    // Get players in this squad
                    List<Player> squadPlayers = playerService.getPlayersBySquad(squad.getSquadId());

                    if (!squadPlayers.isEmpty()) {
                        // Calculate squad averages
                        double squadAvgRating = squadPlayers.stream().mapToDouble(Player::calculateOverallSkillRating).average().orElse(0);
                        double squadAvgAttendance = squadPlayers.stream().mapToDouble(p -> playerService.calculateTrainingAttendanceRate(p.getPlayerId())).average().orElse(0);

                        report.append(String.format("Players: %d\n", squadPlayers.size()));
                        report.append(String.format("Average Rating: %.1f\n", squadAvgRating));
                        report.append(String.format("Average Attendance: %.1f%%\n\n", squadAvgAttendance));
                    } else {
                        report.append("No players found in this squad.\n\n");
                    }
                }
            }

            return report.toString();
        } catch (Exception e) {
            return "Error generating skills report: " + e.getMessage();
        }
    }

    @Override
    public String generateGamesReport(Integer squadId, String dateRange) {
        StringBuilder report = new StringBuilder();
        GameService gameService = DependencyManager.getGameService();
        SquadService squadService = DependencyManager.getSquadService();

        try {
            // Report title and parameters
            report.append("GAMES REPORT\n");
            report.append("=============\n\n");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date();
            report.append("Generated on: ").append(sdf.format(currentDate)).append("\n\n");

            List<Game> games;

            // If squadId is provided, include squad details
            if (squadId != null) {
                Squad squad = squadService.getSquadById(squadId);
                report.append("Squad: ").append(squad.getSquadName()).append(" (").append(squad.getAgeGrade()).append(")\n\n");

                // Get games for this squad
                games = gameService.getGamesBySquad(squadId);
            } else {
                report.append("All Games\n\n");
                games = gameService.getAllGames();
            }

            // If date range is provided, filter games
            if (dateRange != null) {
                // Parse date range
                String[] dates = dateRange.split("-");
                if (dates.length == 2) {
                    try {
                        Date startDate = sdf.parse(dates[0]);
                        Date endDate = sdf.parse(dates[1]);
                        games.removeIf(g -> g.getDate().before(startDate) || g.getDate().after(endDate));

                        report.append("Date Range: ").append(sdf.format(startDate)).append(" to ").append(sdf.format(endDate)).append("\n\n");
                    } catch (Exception e) {
                        report.append("Invalid date range format. Should be DD/MM/YYYY-DD/MM/YYYY\n\n");
                    }
                }
            }

            // Game summary
            report.append("Games: ").append(games.size()).append("\n\n");

            if (!games.isEmpty()) {
                // Table header for games
                report.append(String.format("%-4s %-12s %-20s %-10s %-20s %-15s\n",
                        "ID", "Date", "Opponent", "Score", "Venue", "Attendance"));
                report.append("-".repeat(85)).append("\n");

                for (Game game : games) {
                    String squadName = game.getSquad() != null ? game.getSquad().getSquadName() : "Unassigned";

                    report.append(String.format("%-4d %-12s %-20s %-10s %-20s %.1f%%\n",
                            game.getGameId(),
                            sdf.format(game.getDate()),
                            game.getOpponent(),
                            game.getFinalScore() != null ? game.getFinalScore() : "N/A",
                            game.getVenue(),
                            game.getAttendanceRate()));
                }

                // Calculate win/loss/draw statistics
                if (squadId != null) {
                    int[] record = gameService.getSquadRecord(squadId);
                    int wins = record[0];
                    int losses = record[1];
                    int draws = record[2];
                    int total = games.size();

                    report.append("\nResults Summary:\n");
                    report.append(String.format("Wins: %d (%.1f%%)\n", wins, (double) wins / total * 100));
                    report.append(String.format("Losses: %d (%.1f%%)\n", losses, (double) losses / total * 100));
                    report.append(String.format("Draws: %d (%.1f%%)\n", draws, (double) draws / total * 100));

                    // Show performance trend (simplified)
                    report.append("\nPerformance Trend (last 5 games):\n");

                    // Sort games by date (most recent first)
                    games.sort((g1, g2) -> g2.getDate().compareTo(g1.getDate()));

                    // Take the last 5 games
                    List<Game> recentGames = games.stream().limit(5).toList();

                    for (Game game : recentGames) {
                        String result;
                        if (game.isWin()) {
                            result = "W";
                        } else if (game.isLoss()) {
                            result = "L";
                        } else if (game.isDraw()) {
                            result = "D";
                        } else {
                            result = "N/A";
                        }

                        report.append(String.format("%s vs. %s: %s (%s)\n",
                                squadId != null ? squadService.getSquadById(squadId).getSquadName() : "Our Team",
                                game.getOpponent(),
                                game.getFinalScore() != null ? game.getFinalScore() : "N/A",
                                result));
                    }
                } else {
                    // Summary for all squads
                    report.append("\nResults by Squad:\n");

                    List<Squad> squads = squadService.getAllSquads();
                    for (Squad squad : squads) {
                        List<Game> squadGames = gameService.getGamesBySquad(squad.getSquadId());

                        if (!squadGames.isEmpty()) {
                            int[] record = gameService.getSquadRecord(squad.getSquadId());
                            int wins = record[0];
                            int losses = record[1];
                            int draws = record[2];

                            report.append(String.format("%s (%s): %d games, %d wins, %d losses, %d draws\n",
                                    squad.getSquadName(),
                                    squad.getAgeGrade(),
                                    squadGames.size(),
                                    wins,
                                    losses,
                                    draws));
                        }
                    }
                }
            } else {
                report.append("No games found for the specified criteria.\n");
            }

            return report.toString();
        } catch (Exception e) {
            return "Error generating games report: " + e.getMessage();
        }
    }

    @Override
    public void validateReport(Report report) {
        List<String> errors = new ArrayList<>();

        // Validate title
        if (report.getTitle() == null || report.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        } else if (report.getTitle().length() > 100) {
            errors.add("Title must be 100 characters or less");
        }

        // Validate content
        if (report.getContent() == null || report.getContent().trim().isEmpty()) {
            errors.add("Content is required");
        }

        // Validate generated by
        if (report.getGeneratedBy() == null || report.getGeneratedBy().trim().isEmpty()) {
            errors.add("Generator is required");
        }

        // Validate report type
        if (report.getReportType() == null || report.getReportType().trim().isEmpty()) {
            errors.add("Report type is required");
        }

        // Validate generated date
        if (report.getGeneratedDate() == null) {
            errors.add("Generated date is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Report validation failed", errors);
        }
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}