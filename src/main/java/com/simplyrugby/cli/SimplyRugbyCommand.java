package com.simplyrugby.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "simplyrugby",
    description = "Simply Rugby Club Management System",
    subcommands = {
        LoginCommand.class,
        CommandLine.HelpCommand.class
    },
    mixinStandardHelpOptions = true,
    version = "Simply Rugby v1.0"
)
public class SimplyRugbyCommand implements Runnable {
    @Option(names = {"-v", "--verbose"}, description = "Enable verbose mode")
    private boolean verbose;
    
    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("  SIMPLY RUGBY CLUB MANAGEMENT");
        System.out.println("==================================\n");
        System.out.println("Welcome to the Simply Rugby Club Management System.");
        System.out.println("Please login to access the system.\n");
        System.out.println("Type 'simplyrugby login' to proceed or 'simplyrugby --help' for more options.");
    }
}