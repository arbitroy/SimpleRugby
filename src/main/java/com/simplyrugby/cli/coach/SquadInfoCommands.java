package com.simplyrugby.cli.coach;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.service.CoachService;
import com.simplyrugby.service.PlayerService;
import com.simplyrugby.service.SquadService;
import com.simplyrugby.service.UserService;
import com.simplyrugby.util.DependencyManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "squad",
        description = "View squad information",
        subcommands = {
                SquadInfoCommands.ListCommand.class,
                SquadInfoCommands.ViewCommand.class,
                SquadInfoCommands.PlayersCommand.class,
                CommandLine.HelpCommand.class
        },
        mixinStandardHelpOptions = true
)
public class SquadInfoCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("         SQUAD INFO PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  list    - List all squads you coach");
        System.out.println("  view    - View detailed squad information");
        System.out.println("  players - View players in a squad");
        System.out.println("  help    - Show help\n");
    }

    @Command(name = "list", description = "List all squads you coach")
    static class ListCommand implements Callable<Integer> {

        @Override
        public Integer call() {
            CoachService coachService = DependencyManager.getCoachService();
            UserService userService = DependencyManager.getUserService();

            try {
                // Get current user
                String username = System.getProperty("user.name");

                // Find coach by username
                int memberId = userService.getUserByUsername(username).getMemberId();
                Coach coach = coachService.getCoachByMemberId(memberId);

                // Get assigned squads
                List<Squad> squads = coach.getAssignedSquads();

                System.out.println("\n=== Your Assigned Squads ===\n");

                if (squads.isEmpty()) {
                    System.out.println("You are not currently assigned to any squads.");
                } else {
                    System.out.printf("%-4s %-20s %-15s %-15s\n",
                            "ID", "Name", "Age Grade", "Players");
                    System.out.println("-".repeat(60));

                    SquadService squadService = DependencyManager.getSquadService();

                    for (Squad squad : squads) {
                        int playerCount = squadService.getPlayerCount(squad.getSquadId());

                        System.out.printf("%-4d %-20s %-15s %-15d\n",
                                squad.getSquadId(),
                                squad.getSquadName(),
                                squad.getAgeGrade(),
                                playerCount);
                    }
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "view", description = "View detailed squad information")
    static class ViewCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Squad ID")
        int squadId;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();

            try {
                Squad squad = squadService.getSquadById(squadId);

                System.out.println("\n=== Squad Details ===\n");

                System.out.println("ID:        " + squad.getSquadId());
                System.out.println("Name:      " + squad.getSquadName());
                System.out.println("Age Grade: " + squad.getAgeGrade());

                // Player count
                int playerCount = squadService.getPlayerCount(squadId);
                System.out.println("Players:   " + playerCount);

                // Coach count
                int coachCount = squadService.getCoachCount(squadId);
                System.out.println("Coaches:   " + coachCount);

                // Check if has minimum coaches
                boolean hasMinimumCoaches = squadService.hasMinimumCoaches(squadId);
                System.out.println("Has Minimum Coaches: " + (hasMinimumCoaches ? "Yes" : "No"));

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "players", description = "View players in a squad")
    static class PlayersCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Squad ID")
        int squadId;

        @Override
        public Integer call() {
            SquadService squadService = DependencyManager.getSquadService();
            PlayerService playerService = DependencyManager.getPlayerService();

            try {
                Squad squad = squadService.getSquadById(squadId);

                System.out.printf("\n=== Players in %s (%s) ===\n\n",
                        squad.getSquadName(), squad.getAgeGrade());

                List<Player> players = playerService.getPlayersBySquad(squadId);

                if (players.isEmpty()) {
                    System.out.println("No players in this squad.");
                } else {
                    System.out.printf("%-4s %-20s %-15s %-30s %-15s\n",
                            "ID", "Name", "Position", "Contact", "Emergency Contact");
                    System.out.println("-".repeat(90));

                    for (Player player : players) {
                        String emergencyContact = player.getEmergencyContact() != null ?
                                player.getEmergencyContact().getFullName() : "Not set";

                        System.out.printf("%-4d %-20s %-15s %-30s %-15s\n",
                                player.getPlayerId(),
                                player.getFirstName() + " " + player.getLastName(),
                                player.getPosition(),
                                player.getEmail() != null ? player.getEmail() : player.getPhone(),
                                emergencyContact);
                    }
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }
}