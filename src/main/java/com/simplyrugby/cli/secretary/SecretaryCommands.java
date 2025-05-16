package com.simplyrugby.cli.secretary;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "secretary",
    description = "Secretary functions",
    subcommands = {
        PlayerCommands.class,
        CoachCommands.class,
        SquadCommands.class,
        ReportCommands.class,
        AnnouncementCommands.class,
        CommandLine.HelpCommand.class
    },
    mixinStandardHelpOptions = true
)
public class SecretaryCommands implements Runnable {
    
    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("         SECRETARY PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  players   - Manage players");
        System.out.println("  coaches   - Manage coaches");
        System.out.println("  squads    - Manage squads");
        System.out.println("  reports   - Generate reports");
        System.out.println("  announce  - Send announcements");
        System.out.println("  help      - Show help");
        System.out.println("  logout    - Logout from the system\n");
    }
}