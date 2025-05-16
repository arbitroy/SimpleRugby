package com.simplyrugby.cli.coach;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "coach",
        description = "Coach functions",
        subcommands = {
                GameCommands.class,
                TrainingCommands.class,
                PlayerSkillCommands.class,
                SquadInfoCommands.class,
                CommandLine.HelpCommand.class
        },
        mixinStandardHelpOptions = true
)
public class CoachCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("           COACH PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  games     - Manage games");
        System.out.println("  training  - Manage training sessions");
        System.out.println("  skills    - View and track player skills");
        System.out.println("  squad     - View squad information");
        System.out.println("  help      - Show help");
        System.out.println("  logout    - Logout from the system\n");
    }
}