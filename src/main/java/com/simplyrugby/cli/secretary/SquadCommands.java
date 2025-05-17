package com.simplyrugby.cli.secretary;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.service.CoachService;
import com.simplyrugby.service.PlayerService;
import com.simplyrugby.service.SquadService;
import com.simplyrugby.util.DependencyManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "squads",
        description = "Manage squads",
        subcommands = {
                SquadCommands.ListCommand.class,
                SquadCommands.AddCommand.class,
                SquadCommands.EditCommand.class,
                SquadCommands.DeleteCommand.class,
                SquadCommands.ViewCommand.class,
                SquadCommands.AssignCoachCommand.class,
                SquadCommands.RemoveCoachCommand.class,
                CommandLine.HelpCommand.class
        },
        mixinStandardHelpOptions = true
)
public class SquadCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("          SQUADS PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  list        - List all squads");
        System.out.println("  add         - Add a new squad");
        System.out.println("  edit        - Edit a squad");
        System.out.println("  delete      - Delete a squad");
        System.out.println("  view        - View squad details");
        System.out.println("  assigncoach - Assign a coach to a squad");
        System.out.println("  removecoach - Remove a coach from a squad");
        System.out.println("  help        - Show help\n");
    }

    @Command(name = "list", description = "List all squads")
    static class ListCommand implements Callable<Integer> {
        @Option(names = {"-a", "--agegrade"}, description = "Filter by age grade")
        String ageGrade;

        @Option(names = {"-c", "--coach"}, description = "Filter by coach ID")
        Integer coachId;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();

            try {
                List<Squad> squads;

                if (ageGrade != null) {
                    System.out.printf("\n=== Squads with Age Grade: %s ===\n\n", ageGrade);
                    squads = squadService.getSquadsByAgeGrade(ageGrade);
                } else if (coachId != null) {
                    CoachService coachService = DependencyManager.getCoachService();
                    Coach coach = coachService.getCoachById(coachId);
                    System.out.printf("\n=== Squads Coached by %s %s ===\n\n",
                            coach.getFirstName(), coach.getLastName());
                    squads = squadService.getSquadsByCoach(coachId);
                } else {
                    System.out.println("\n=== All Squads ===\n");
                    squads = squadService.getAllSquads();
                }

                // Display squads table
                if (squads.isEmpty()) {
                    System.out.println("No squads found.");
                } else {
                    System.out.printf("%-4s %-20s %-15s %-12s %-12s\n",
                            "ID", "Name", "Age Grade", "Players", "Coaches");
                    System.out.println("-".repeat(70));

                    for (Squad squad : squads) {
                        int playerCount = squadService.getPlayerCount(squad.getSquadId());
                        int coachCount = squadService.getCoachCount(squad.getSquadId());

                        System.out.printf("%-4d %-20s %-15s %-12d %-12d\n",
                                squad.getSquadId(),
                                squad.getSquadName(),
                                squad.getAgeGrade(),
                                playerCount,
                                coachCount);
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

    @Command(name = "add", description = "Add a new squad")
    static class AddCommand implements Callable<Integer> {
        @Option(names = {"-n", "--name"}, description = "Squad name", required = true)
        String name;

        @Option(names = {"-a", "--agegrade"}, description = "Age grade", required = true)
        String ageGrade;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();

            try {
                Squad squad = new Squad();
                squad.setSquadName(name);
                squad.setAgeGrade(ageGrade);

                // Save squad
                int squadId = squadService.addSquad(squad);

                System.out.println("\nSquad added successfully with ID: " + squadId);
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "edit", description = "Edit a squad")
    static class EditCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Squad ID")
        int squadId;

        @Option(names = {"-n", "--name"}, description = "Squad name")
        String name;

        @Option(names = {"-a", "--agegrade"}, description = "Age grade")
        String ageGrade;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();

            try {
                // Get existing squad
                Squad squad = squadService.getSquadById(squadId);

                // Update fields if provided
                if (name != null) squad.setSquadName(name);
                if (ageGrade != null) squad.setAgeGrade(ageGrade);

                // Update squad
                boolean result = squadService.updateSquad(squad);

                if (result) {
                    System.out.println("\nSquad updated successfully.");
                    return 0;
                } else {
                    System.err.println("Failed to update squad.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "delete", description = "Delete a squad")
    static class DeleteCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Squad ID")
        int squadId;

        @Option(names = {"-f", "--force"}, description = "Force deletion without confirmation")
        boolean force;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();

            try {
                // Get squad for confirmation
                Squad squad = squadService.getSquadById(squadId);

                // Confirm deletion
                if (!force) {
                    System.out.printf("\nAre you sure you want to delete squad: %s (%s) (ID: %d)? (y/n) ",
                            squad.getSquadName(), squad.getAgeGrade(), squad.getSquadId());
                    String confirmation = System.console().readLine().trim().toLowerCase();

                    if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                        System.out.println("\nDeletion cancelled.");
                        return 0;
                    }
                }

                // Delete squad
                boolean result = squadService.deleteSquad(squadId);

                if (result) {
                    System.out.println("\nSquad deleted successfully.");
                    return 0;
                } else {
                    System.err.println("Failed to delete squad.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "view", description = "View squad details")
    static class ViewCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Squad ID")
        int squadId;

        @Option(names = {"-p", "--players"}, description = "Show players in squad")
        boolean showPlayers = true;

        @Option(names = {"-c", "--coaches"}, description = "Show coaches for squad")
        boolean showCoaches = true;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();
            PlayerService playerService = DependencyManager.getPlayerService();
            CoachService coachService = DependencyManager.getCoachService();

            try {
                Squad squad = squadService.getSquadById(squadId);

                System.out.println("\n=== Squad Details ===\n");

                System.out.println("ID:        " + squad.getSquadId());
                System.out.println("Name:      " + squad.getSquadName());
                System.out.println("Age Grade: " + squad.getAgeGrade());

                int playerCount = squadService.getPlayerCount(squadId);
                int coachCount = squadService.getCoachCount(squadId);

                System.out.println("Players:   " + playerCount);
                System.out.println("Coaches:   " + coachCount);

                boolean hasMinimumCoaches = squadService.hasMinimumCoaches(squadId);
                System.out.println("Has Minimum Coaches: " + (hasMinimumCoaches ? "Yes" : "No"));

                if (showPlayers && playerCount > 0) {
                    List<Player> players = squadService.getPlayers(squadId);

                    System.out.println("\n=== Players ===\n");

                    System.out.printf("%-4s %-20s %-15s %-30s\n",
                            "ID", "Name", "Position", "Contact");
                    System.out.println("-".repeat(70));

                    for (Player player : players) {
                        System.out.printf("%-4d %-20s %-15s %-30s\n",
                                player.getPlayerId(),
                                player.getFirstName() + " " + player.getLastName(),
                                player.getPosition(),
                                player.getEmail() != null ? player.getEmail() : player.getPhone());
                    }
                }

                if (showCoaches && coachCount > 0) {
                    List<Coach> coaches = squadService.getCoaches(squadId);

                    System.out.println("\n=== Coaches ===\n");

                    System.out.printf("%-4s %-20s %-50s\n",
                            "ID", "Name", "Qualifications");
                    System.out.println("-".repeat(75));

                    for (Coach coach : coaches) {
                        System.out.printf("%-4d %-20s %-50s\n",
                                coach.getCoachId(),
                                coach.getFirstName() + " " + coach.getLastName(),
                                truncate(coach.getQualifications(), 50));
                    }
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

    @Command(name = "assigncoach", description = "Assign a coach to a squad")
    static class AssignCoachCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Squad ID")
        int squadId;

        @Parameters(index = "1", description = "Coach ID")
        int coachId;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();
            CoachService coachService = DependencyManager.getCoachService();

            try {
                // Get entities for display
                Squad squad = squadService.getSquadById(squadId);
                Coach coach = coachService.getCoachById(coachId);

                // Assign coach to squad
                boolean result = squadService.addCoach(squadId, coachId);

                if (result) {
                    System.out.printf("\nCoach %s %s assigned to squad %s (%s) successfully.\n",
                            coach.getFirstName(), coach.getLastName(),
                            squad.getSquadName(), squad.getAgeGrade());
                    return 0;
                } else {
                    System.err.println("Failed to assign coach to squad.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "removecoach", description = "Remove a coach from a squad")
    static class RemoveCoachCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Squad ID")
        int squadId;

        @Parameters(index = "1", description = "Coach ID")
        int coachId;

        @Option(names = {"-f", "--force"}, description = "Force removal without confirmation")
        boolean force;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();
            CoachService coachService = DependencyManager.getCoachService();

            try {
                // Get entities for display
                Squad squad = squadService.getSquadById(squadId);
                Coach coach = coachService.getCoachById(coachId);

                // Confirm removal
                if (!force) {
                    System.out.printf("\nAre you sure you want to remove coach %s %s from squad %s (%s)? (y/n) ",
                            coach.getFirstName(), coach.getLastName(),
                            squad.getSquadName(), squad.getAgeGrade());
                    String confirmation = System.console().readLine().trim().toLowerCase();

                    if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                        System.out.println("\nRemoval cancelled.");
                        return 0;
                    }
                }

                // Remove coach from squad
                boolean result = squadService.removeCoach(squadId, coachId);

                if (result) {
                    System.out.printf("\nCoach %s %s removed from squad %s (%s) successfully.\n",
                            coach.getFirstName(), coach.getLastName(),
                            squad.getSquadName(), squad.getAgeGrade());

                    // Check if squad still has minimum coaches
                    boolean hasMinimumCoaches = squadService.hasMinimumCoaches(squadId);
                    if (!hasMinimumCoaches) {
                        System.out.printf("\nWarning: Squad %s (%s) now has fewer than the minimum required coaches.\n",
                                squad.getSquadName(), squad.getAgeGrade());
                    }

                    return 0;
                } else {
                    System.err.println("Failed to remove coach from squad.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }
}