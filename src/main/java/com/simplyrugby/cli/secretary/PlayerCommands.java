package com.simplyrugby.cli.secretary;

import com.simplyrugby.domain.Player;
import com.simplyrugby.domain.Squad;
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
    name = "players",
    description = "Manage players",
    subcommands = {
        PlayerCommands.ListCommand.class,
        PlayerCommands.AddCommand.class,
        PlayerCommands.EditCommand.class,
        PlayerCommands.DeleteCommand.class,
        PlayerCommands.AssignCommand.class,
        CommandLine.HelpCommand.class
    },
    mixinStandardHelpOptions = true
)
public class PlayerCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("         PLAYERS PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  list     - List all players");
        System.out.println("  add      - Add a new player");
        System.out.println("  edit     - Edit a player");
        System.out.println("  delete   - Delete a player");
        System.out.println("  assign   - Assign player to squad");
        System.out.println("  help     - Show help\n");
    }

    @Command(name = "list", description = "List all players")
    static class ListCommand implements Callable<Integer> {
        @Option(names = {"-s", "--squad"}, description = "Filter by squad ID")
        Integer squadId;

        @Override
        public Integer call() {
            PlayerService playerService = DependencyManager.getPlayerService();
            SquadService squadService = DependencyManager.getSquadService();
            
            List<Player> players;
            
            try {
                if (squadId != null) {
                    // Get squad name for display
                    Squad squad = squadService.getSquadById(squadId);
                    System.out.printf("\n=== Players in %s (%s) ===\n\n", 
                                      squad.getSquadName(), squad.getAgeGrade());
                    players = playerService.getPlayersBySquad(squadId);
                } else {
                    System.out.println("\n=== All Players ===\n");
                    players = playerService.getAllPlayers();
                }
                
                // Display player table
                if (players.isEmpty()) {
                    System.out.println("No players found.");
                } else {
                    System.out.printf("%-4s %-20s %-15s %-15s %-30s\n", 
                                     "ID", "Name", "Position", "Squad", "Contact");
                    System.out.println("-".repeat(85));
                    
                    for (Player player : players) {
                        String squadName = player.getSquad() != null ? 
                                          player.getSquad().getSquadName() : "Unassigned";
                        
                        System.out.printf("%-4d %-20s %-15s %-15s %-30s\n",
                                         player.getPlayerId(),
                                         player.getFirstName() + " " + player.getLastName(),
                                         player.getPosition(),
                                         squadName,
                                         player.getEmail());
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

    @Command(name = "add", description = "Add a new player")
    static class AddCommand implements Callable<Integer> {
        @Option(names = {"-f", "--firstname"}, description = "First name", required = true)
        String firstName;
        
        @Option(names = {"-l", "--lastname"}, description = "Last name", required = true)
        String lastName;
        
        @Option(names = {"-d", "--dob"}, description = "Date of birth (DD/MM/YYYY)", required = true)
        String dob;
        
        @Option(names = {"-e", "--email"}, description = "Email address")
        String email;
        
        @Option(names = {"-p", "--phone"}, description = "Phone number")
        String phone;
        
        @Option(names = {"-a", "--address"}, description = "Address")
        String address;
        
        @Option(names = {"-po", "--position"}, description = "Rugby position", required = true)
        String position;
        
        @Option(names = {"-m", "--medical"}, description = "Medical conditions")
        String medicalConditions;
        
        @Option(names = {"-s", "--squad"}, description = "Squad ID to assign player to")
        Integer squadId;

        @Override
        public Integer call() {
            PlayerService playerService = DependencyManager.getPlayerService();
            SquadService squadService = DependencyManager.getSquadService();
            
            try {
                Player player = new Player();
                player.setFirstName(firstName);
                player.setLastName(lastName);
                
                // Parse date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    Date dateOfBirth = sdf.parse(dob);
                    player.setDateOfBirth(dateOfBirth);
                } catch (ParseException e) {
                    System.err.println("Error: Invalid date format. Please use DD/MM/YYYY.");
                    return 1;
                }
                
                player.setEmail(email);
                player.setPhone(phone);
                player.setAddress(address);
                player.setPosition(position);
                player.setMedicalConditions(medicalConditions);
                
                // Assign to squad if provided
                if (squadId != null) {
                    try {
                        Squad squad = squadService.getSquadById(squadId);
                        player.setSquad(squad);
                    } catch (Exception e) {
                        System.err.println("Warning: " + e.getMessage() + ". Player will be unassigned.");
                    }
                }
                
                // Save player
                int playerId = playerService.addPlayer(player);
                
                System.out.println("\nPlayer added successfully with ID: " + playerId);
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    // Edit player command
    @Command(name = "edit", description = "Edit a player")
    static class EditCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Player ID")
        int playerId;
        
        @Option(names = {"-f", "--firstname"}, description = "First name")
        String firstName;
        
        @Option(names = {"-l", "--lastname"}, description = "Last name")
        String lastName;
        
        @Option(names = {"-d", "--dob"}, description = "Date of birth (DD/MM/YYYY)")
        String dob;
        
        @Option(names = {"-e", "--email"}, description = "Email address")
        String email;
        
        @Option(names = {"-p", "--phone"}, description = "Phone number")
        String phone;
        
        @Option(names = {"-a", "--address"}, description = "Address")
        String address;
        
        @Option(names = {"-po", "--position"}, description = "Rugby position")
        String position;
        
        @Option(names = {"-m", "--medical"}, description = "Medical conditions")
        String medicalConditions;

        @Override
        public Integer call() {
            PlayerService playerService = DependencyManager.getPlayerService();
            
            try {
                // Get existing player
                Player player = playerService.getPlayerById(playerId);
                
                // Update fields if provided
                if (firstName != null) player.setFirstName(firstName);
                if (lastName != null) player.setLastName(lastName);
                if (dob != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        Date dateOfBirth = sdf.parse(dob);
                        player.setDateOfBirth(dateOfBirth);
                    } catch (ParseException e) {
                        System.err.println("Error: Invalid date format. Please use DD/MM/YYYY.");
                        return 1;
                    }
                }
                if (email != null) player.setEmail(email);
                if (phone != null) player.setPhone(phone);
                if (address != null) player.setAddress(address);
                if (position != null) player.setPosition(position);
                if (medicalConditions != null) player.setMedicalConditions(medicalConditions);
                
                // Update player
                boolean result = playerService.updatePlayer(player);
                
                if (result) {
                    System.out.println("\nPlayer updated successfully.");
                    return 0;
                } else {
                    System.err.println("Failed to update player.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    // Delete player command
    @Command(name = "delete", description = "Delete a player")
    static class DeleteCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Player ID")
        int playerId;
        
        @Option(names = {"-f", "--force"}, description = "Force deletion without confirmation")
        boolean force;

        @Override
        public Integer call() {
            PlayerService playerService = DependencyManager.getPlayerService();
            
            try {
                // Get player for confirmation
                Player player = playerService.getPlayerById(playerId);
                
                // Confirm deletion
                if (!force) {
                    System.out.printf("\nAre you sure you want to delete player: %s %s (ID: %d)? (y/n) ",
                                     player.getFirstName(), player.getLastName(), player.getPlayerId());
                    String confirmation = System.console().readLine().trim().toLowerCase();
                    
                    if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                        System.out.println("\nDeletion cancelled.");
                        return 0;
                    }
                }
                
                // Delete player
                boolean result = playerService.deletePlayer(playerId);
                
                if (result) {
                    System.out.println("\nPlayer deleted successfully.");
                    return 0;
                } else {
                    System.err.println("Failed to delete player.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    // Assign player to squad command
    @Command(name = "assign", description = "Assign player to squad")
    static class AssignCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Player ID")
        int playerId;
        
        @Parameters(index = "1", description = "Squad ID (or 0 to remove from squad)")
        int squadId;

        @Override
        public Integer call() {
            PlayerService playerService = DependencyManager.getPlayerService();
            SquadService squadService = DependencyManager.getSquadService();
            
            try {
                // Get player for confirmation
                Player player = playerService.getPlayerById(playerId);
                
                if (squadId == 0) {
                    // Remove player from squad
                    boolean result = playerService.removePlayerFromSquad(playerId);
                    
                    if (result) {
                        System.out.printf("\nPlayer %s %s removed from squad successfully.\n",
                                         player.getFirstName(), player.getLastName());
                        return 0;
                    } else {
                        System.err.println("Failed to remove player from squad.");
                        return 1;
                    }
                } else {
                    // Get squad for confirmation
                    Squad squad = squadService.getSquadById(squadId);
                    
                    // Assign player to squad
                    boolean result = playerService.assignPlayerToSquad(playerId, squadId);
                    
                    if (result) {
                        System.out.printf("\nPlayer %s %s assigned to squad %s (%s) successfully.\n",
                                         player.getFirstName(), player.getLastName(),
                                         squad.getSquadName(), squad.getAgeGrade());
                        return 0;
                    } else {
                        System.err.println("Failed to assign player to squad.");
                        return 1;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }
}