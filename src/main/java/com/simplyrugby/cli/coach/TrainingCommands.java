package com.simplyrugby.cli.coach;

import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.domain.Training;
import com.simplyrugby.domain.TrainingAttendance;
import com.simplyrugby.service.PlayerService;
import com.simplyrugby.service.SquadService;
import com.simplyrugby.service.TrainingService;
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
        name = "training",
        description = "Manage training sessions",
        subcommands = {
                TrainingCommands.ListCommand.class,
                TrainingCommands.AddCommand.class,
                TrainingCommands.ViewCommand.class,
                TrainingCommands.RecordAttendanceCommand.class,
                CommandLine.HelpCommand.class
        },
        mixinStandardHelpOptions = true
)
public class TrainingCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("         TRAINING PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  list           - List all training sessions");
        System.out.println("  add            - Add a new training session");
        System.out.println("  view           - View training details");
        System.out.println("  attendance     - Record attendance for a training session");
        System.out.println("  help           - Show help\n");
    }

    @Command(name = "list", description = "List all training sessions")
    static class ListCommand implements Callable<Integer> {
        @Option(names = {"-s", "--squad"}, description = "Filter by squad ID")
        Integer squadId;

        @Option(names = {"-u", "--upcoming"}, description = "Show only upcoming training sessions")
        boolean upcomingOnly;

        @Option(names = {"-r", "--recent"}, description = "Show only recent training sessions")
        boolean recentOnly;

        @Override
        public Integer call() {
            TrainingService trainingService = DependencyManager.getTrainingService();
            SquadService squadService = DependencyManager.getSquadService();

            try {
                List<Training> trainings;

                if (squadId != null) {
                    // Get squad name for display
                    Squad squad = squadService.getSquadById(squadId);
                    System.out.printf("\n=== Training Sessions for %s (%s) ===\n\n",
                            squad.getSquadName(), squad.getAgeGrade());
                    trainings = trainingService.getTrainingSessionsBySquad(squadId);
                } else {
                    System.out.println("\n=== All Training Sessions ===\n");
                    trainings = trainingService.getAllTrainingSessions();
                }

                // Filter by date if requested
                Date today = new Date();
                if (upcomingOnly) {
                    trainings.removeIf(training -> training.getDate().before(today));
                } else if (recentOnly) {
                    trainings.removeIf(training -> training.getDate().after(today));
                }

                // Display trainings table
                if (trainings.isEmpty()) {
                    System.out.println("No training sessions found.");
                } else {
                    System.out.printf("%-4s %-12s %-20s %-30s %-10s\n",
                            "ID", "Date", "Squad", "Focus Areas", "Attendance");
                    System.out.println("-".repeat(85));

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    for (Training training : trainings) {
                        String squadName = training.getSquad() != null ?
                                training.getSquad().getSquadName() : "Unassigned";

                        double attendanceRate = training.getAttendanceRate();

                        System.out.printf("%-4d %-12s %-20s %-30s %.1f%%\n",
                                training.getTrainingId(),
                                sdf.format(training.getDate()),
                                squadName,
                                truncate(training.getFocusAreas(), 30),
                                attendanceRate);
                    }
                    System.out.println();
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }

        private String truncate(String text, int length) {
            if (text == null) return "";
            if (text.length() <= length) return text;
            return text.substring(0, length - 3) + "...";
        }
    }

    @Command(name = "add", description = "Add a new training session")
    static class AddCommand implements Callable<Integer> {
        @Option(names = {"-d", "--date"}, description = "Training date (DD/MM/YYYY)", required = true)
        String date;

        @Option(names = {"-s", "--squad"}, description = "Squad ID", required = true)
        int squadId;

        @Option(names = {"-f", "--focus"}, description = "Focus areas", required = true)
        String focusAreas;

        @Option(names = {"-n", "--notes"}, description = "Coach notes")
        String coachNotes;

        @Override
        public Integer call() {
            TrainingService trainingService = DependencyManager.getTrainingService();
            SquadService squadService = DependencyManager.getSquadService();

            try {
                Training training = new Training();

                // Parse date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    Date trainingDate = sdf.parse(date);
                    training.setDate(trainingDate);
                } catch (ParseException e) {
                    System.err.println("Error: Invalid date format. Please use DD/MM/YYYY.");
                    return 1;
                }

                // Get squad
                Squad squad = squadService.getSquadById(squadId);
                training.setSquad(squad);

                training.setFocusAreas(focusAreas);
                training.setCoachNotes(coachNotes);

                // Save training
                int trainingId = trainingService.addTraining(training);

                System.out.println("\nTraining session added successfully with ID: " + trainingId);
                System.out.println("You can now record attendance with the command:");
                System.out.println("  training attendance " + trainingId);
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "view", description = "View training details")
    static class ViewCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Training ID")
        int trainingId;

        @Override
        public Integer call() {
            TrainingService trainingService = DependencyManager.getTrainingService();

            try {
                Training training = trainingService.getTrainingById(trainingId);

                System.out.println("\n=== Training Session Details ===\n");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                System.out.println("ID:          " + training.getTrainingId());
                System.out.println("Date:        " + sdf.format(training.getDate()));

                if (training.getSquad() != null) {
                    System.out.println("Squad:       " + training.getSquad().getSquadName() +
                            " (" + training.getSquad().getAgeGrade() + ")");
                } else {
                    System.out.println("Squad:       Unassigned");
                }

                System.out.println("Focus Areas: " + training.getFocusAreas());

                if (training.getCoachNotes() != null && !training.getCoachNotes().isEmpty()) {
                    System.out.println("Coach Notes: " + training.getCoachNotes());
                }

                // Display attendance records if available
                List<TrainingAttendance> records = training.getAttendanceRecords();

                if (records != null && !records.isEmpty()) {
                    System.out.println("\n=== Attendance Records ===\n");

                    System.out.printf("%-20s %-10s %-50s\n",
                            "Player", "Present", "Notes");
                    System.out.println("-".repeat(80));

                    PlayerService playerService = DependencyManager.getPlayerService();

                    for (TrainingAttendance record : records) {
                        Player player = playerService.getPlayerById(record.getPlayerId());

                        System.out.printf("%-20s %-10s %-50s\n",
                                player.getFirstName() + " " + player.getLastName(),
                                record.isPresent() ? "Yes" : "No",
                                truncate(record.getPlayerNotes(), 50));
                    }

                    // Display attendance summary
                    int present = (int) records.stream().filter(TrainingAttendance::isPresent).count();
                    int total = records.size();
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
                    System.out.println("\nNo attendance records for this training session.");
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }

        private String truncate(String text, int length) {
            if (text == null) return "";
            if (text.length() <= length) return text;
            return text.substring(0, length - 3) + "...";
        }
    }

    @Command(name = "attendance", description = "Record attendance for a training session")
    static class RecordAttendanceCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Training ID")
        int trainingId;

        @Option(names = {"-i", "--interactive"}, description = "Interactive mode")
        boolean interactive = true;

        @Override
        public Integer call() {
            TrainingService trainingService = DependencyManager.getTrainingService();
            PlayerService playerService = DependencyManager.getPlayerService();

            try {
                Training training = trainingService.getTrainingById(trainingId);

                if (training.getSquad() == null) {
                    System.err.println("Error: Training session has no assigned squad.");
                    return 1;
                }

                System.out.printf("\n=== Recording Attendance for Training on %s ===\n\n",
                        new SimpleDateFormat("dd/MM/yyyy").format(training.getDate()));

                // Get players in the squad
                List<Player> players = playerService.getPlayersBySquad(training.getSquad().getSquadId());

                if (players.isEmpty()) {
                    System.out.println("No players found in this squad.");
                    return 0;
                }

                if (interactive) {
                    // Interactive mode - prompt for each player
                    for (Player player : players) {
                        System.out.printf("\nRecording attendance for %s %s:\n",
                                player.getFirstName(), player.getLastName());

                        System.out.print("Was the player present? (y/n): ");
                        String present = System.console().readLine().trim().toLowerCase();

                        TrainingAttendance attendance = new TrainingAttendance();
                        attendance.setPlayerId(player.getPlayerId());
                        attendance.setTrainingId(trainingId);
                        attendance.setTrainingDate(training.getDate());
                        attendance.setPresent(present.equals("y") || present.equals("yes"));

                        System.out.print("Notes (optional): ");
                        String notes = System.console().readLine().trim();
                        attendance.setPlayerNotes(notes);

                        // Save attendance to database
                        boolean result = trainingService.addAttendance(attendance);

                        if (result) {
                            System.out.printf("Attendance recorded successfully for %s %s.\n",
                                    player.getFirstName(), player.getLastName());
                        } else {
                            System.out.printf("Failed to record attendance for %s %s.\n",
                                    player.getFirstName(), player.getLastName());
                        }
                    }
                } else {
                    // Batch mode - not implemented in this example
                    System.out.println("Batch mode not implemented. Please use interactive mode.");
                    return 1;
                }

                System.out.println("\nAll attendance records recorded successfully.");
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }
}