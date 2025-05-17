package com.simplyrugby.cli.secretary;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.service.CoachService;
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
        name = "coaches",
        description = "Manage coaches",
        subcommands = {
                CoachCommands.ListCommand.class,
                CoachCommands.AddCommand.class,
                CoachCommands.EditCommand.class,
                CoachCommands.DeleteCommand.class,
                CoachCommands.ViewCommand.class,
                CoachCommands.AssignCommand.class,
                CommandLine.HelpCommand.class
        },
        mixinStandardHelpOptions = true
)
public class CoachCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("          COACHES PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  list     - List all coaches");
        System.out.println("  add      - Add a new coach");
        System.out.println("  edit     - Edit a coach");
        System.out.println("  delete   - Delete a coach");
        System.out.println("  view     - View coach details");
        System.out.println("  assign   - Assign coach to squad");
        System.out.println("  help     - Show help\n");
    }

    @Command(name = "list", description = "List all coaches")
    static class ListCommand implements Callable<Integer> {
        @Option(names = {"-s", "--squad"}, description = "Filter by squad ID")
        Integer squadId;

        @Option(names = {"-q", "--qualification"}, description = "Filter by qualification")
        String qualification;

        @Override
        public Integer call() {
            CoachService coachService = DependencyManager.getCoachService();
            SquadService squadService = DependencyManager.getSquadService();

            try {
                List<Coach> coaches;

                if (squadId != null) {
                    // Get squad name for display
                    Squad squad = squadService.getSquadById(squadId);
                    System.out.printf("\n=== Coaches for %s (%s) ===\n\n",
                            squad.getSquadName(), squad.getAgeGrade());
                    coaches = coachService.getCoachesBySquad(squadId);
                } else if (qualification != null) {
                    System.out.printf("\n=== Coaches with Qualification: %s ===\n\n", qualification);
                    coaches = coachService.getCoachesByQualification(qualification);
                } else {
                    System.out.println("\n=== All Coaches ===\n");
                    coaches = coachService.getAllCoaches();
                }

                // Display coaches table
                if (coaches.isEmpty()) {
                    System.out.println("No coaches found.");
                } else {
                    System.out.printf("%-4s %-20s %-30s %-20s %-20s\n",
                            "ID", "Name", "Email", "Phone", "Qualifications");
                    System.out.println("-".repeat(95));

                    for (Coach coach : coaches) {
                        System.out.printf("%-4d %-20s %-30s %-20s %-20s\n",
                                coach.getCoachId(),
                                coach.getFirstName() + " " + coach.getLastName(),
                                truncate(coach.getEmail(), 30),
                                coach.getPhone(),
                                truncate(coach.getQualifications(), 20));
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

    @Command(name = "add", description = "Add a new coach")
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

        @Option(names = {"-q", "--qualifications"}, description = "Coaching qualifications", required = true)
        String qualifications;

        @Option(names = {"-s", "--squad"}, description = "Squad ID to assign coach to")
        Integer squadId;

        @Override
        public Integer call() {
            CoachService coachService = DependencyManager.getCoachService();
            SquadService squadService = DependencyManager.getSquadService();

            try {
                Coach coach = new Coach();
                coach.setFirstName(firstName);
                coach.setLastName(lastName);

                // Parse date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false);
                    Date dateOfBirth = sdf.parse(dob);
                    coach.setDateOfBirth(dateOfBirth);
                } catch (ParseException e) {
                    System.err.println("Error: Invalid date format. Please use DD/MM/YYYY.");
                    return 1;
                }

                coach.setEmail(email);
                coach.setPhone(phone);
                coach.setAddress(address);
                coach.setQualifications(qualifications);

                // Save coach
                int coachId = coachService.addCoach(coach);

                System.out.println("\nCoach added successfully with ID: " + coachId);

                // Assign to squad if provided
                if (squadId != null) {
                    try {
                        Squad squad = squadService.getSquadById(squadId);
                        boolean result = coachService.assignCoachToSquad(coachId, squadId);

                        if (result) {
                            System.out.printf("Coach assigned to squad %s (%s) successfully.\n",
                                    squad.getSquadName(), squad.getAgeGrade());
                        } else {
                            System.err.println("Failed to assign coach to squad.");
                        }
                    } catch (Exception e) {
                        System.err.println("Warning: " + e.getMessage() + ". Coach was not assigned to a squad.");
                    }
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "edit", description = "Edit a coach")
    static class EditCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Coach ID")
        int coachId;

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

        @Option(names = {"-q", "--qualifications"}, description = "Coaching qualifications")
        String qualifications;

        @Override
        public Integer call() {
            CoachService coachService = DependencyManager.getCoachService();

            try {
                // Get existing coach
                Coach coach = coachService.getCoachById(coachId);

                // Update fields if provided
                if (firstName != null) coach.setFirstName(firstName);
                if (lastName != null) coach.setLastName(lastName);
                if (dob != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setLenient(false);
                        Date dateOfBirth = sdf.parse(dob);
                        coach.setDateOfBirth(dateOfBirth);
                    } catch (ParseException e) {
                        System.err.println("Error: Invalid date format. Please use DD/MM/YYYY.");
                        return 1;
                    }
                }
                if (email != null) coach.setEmail(email);
                if (phone != null) coach.setPhone(phone);
                if (address != null) coach.setAddress(address);
                if (qualifications != null) coach.setQualifications(qualifications);

                // Update coach
                boolean result = coachService.updateCoach(coach);

                if (result) {
                    System.out.println("\nCoach updated successfully.");
                    return 0;
                } else {
                    System.err.println("Failed to update coach.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "delete", description = "Delete a coach")
    static class DeleteCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Coach ID")
        int coachId;

        @Option(names = {"-f", "--force"}, description = "Force deletion without confirmation")
        boolean force;

        @Override
        public Integer call() {
            CoachService coachService = DependencyManager.getCoachService();

            try {
                // Get coach for confirmation
                Coach coach = coachService.getCoachById(coachId);

                // Confirm deletion
                if (!force) {
                    System.out.printf("\nAre you sure you want to delete coach: %s %s (ID: %d)? (y/n) ",
                            coach.getFirstName(), coach.getLastName(), coach.getCoachId());
                    String confirmation = System.console().readLine().trim().toLowerCase();

                    if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                        System.out.println("\nDeletion cancelled.");
                        return 0;
                    }
                }

                // Delete coach
                boolean result = coachService.deleteCoach(coachId);

                if (result) {
                    System.out.println("\nCoach deleted successfully.");
                    return 0;
                } else {
                    System.err.println("Failed to delete coach.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "view", description = "View coach details")
    static class ViewCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Coach ID")
        int coachId;

        @Override
        public Integer call() {
            CoachService coachService = DependencyManager.getCoachService();

            try {
                Coach coach = coachService.getCoachById(coachId);

                System.out.println("\n=== Coach Details ===\n");

                System.out.println("ID:             " + coach.getCoachId());
                System.out.println("Name:           " + coach.getFirstName() + " " + coach.getLastName());
                System.out.println("Date of Birth:  " + new SimpleDateFormat("dd/MM/yyyy").format(coach.getDateOfBirth()));
                System.out.println("Age:            " + coach.getAge());
                System.out.println("Email:          " + coach.getEmail());
                System.out.println("Phone:          " + coach.getPhone());
                System.out.println("Address:        " + coach.getAddress());
                System.out.println("Qualifications: " + coach.getQualifications());

                // Display assigned squads
                List<Squad> assignedSquads = coachService.getAssignedSquads(coachId);

                if (!assignedSquads.isEmpty()) {
                    System.out.println("\n=== Assigned Squads ===\n");

                    System.out.printf("%-4s %-20s %-15s\n",
                            "ID", "Name", "Age Grade");
                    System.out.println("-".repeat(40));

                    for (Squad squad : assignedSquads) {
                        System.out.printf("%-4d %-20s %-15s\n",
                                squad.getSquadId(),
                                squad.getSquadName(),
                                squad.getAgeGrade());
                    }
                } else {
                    System.out.println("\nThis coach is not assigned to any squads.");
                }

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "assign", description = "Assign coach to squad")
    static class AssignCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Coach ID")
        int coachId;

        @Parameters(index = "1", description = "Squad ID")
        int squadId;

        @Override
        public Integer call() {
            CoachService coachService = DependencyManager.getCoachService();
            SquadService squadService = DependencyManager.getSquadService();

            try {
                // Get coach and squad for confirmation
                Coach coach = coachService.getCoachById(coachId);
                Squad squad = squadService.getSquadById(squadId);

                // Assign coach to squad
                boolean result = coachService.assignCoachToSquad(coachId, squadId);

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
}