package com.simplyrugby.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import com.simplyrugby.cli.coach.CoachCommands;
import com.simplyrugby.cli.secretary.SecretaryCommands;
import com.simplyrugby.service.UserService;
import com.simplyrugby.util.DependencyManager;

import java.util.concurrent.Callable;

@Command(
    name = "login",
    description = "Login to the system",
    mixinStandardHelpOptions = true
)
public class LoginCommand implements Callable<Integer> {
    @Option(names = {"-u", "--username"}, description = "Username", interactive = true)
    private String username;
    
    @Option(names = {"-p", "--password"}, description = "Password", interactive = true, arity = "0..1")
    private String password;
    
    @Override
    public Integer call() throws Exception {
        UserService userService = DependencyManager.getUserService();
        
        System.out.println("\n=== Login ===\n");
        
        // If not provided in command line, prompt for credentials
        if (username == null) {
            username = System.console().readLine("Username: ");
        }
        
        if (password == null) {
            password = new String(System.console().readPassword("Password: "));
        }
        
        // Authenticate user
        String role = userService.authenticate(username, password);
        
        if (role != null) {
            System.out.println("\nLogin successful!\n");
            
            // Launch appropriate menu based on role
            switch (role) {
                case "Secretary":
                    return new CommandLine(new SecretaryCommands()).execute();
                case "Coach":
                    return new CommandLine(new CoachCommands()).execute();
                default:
                    System.out.println("Unknown role: " + role);
                    return 1;
            }
        } else {
            System.out.println("\nInvalid username or password. Please try again.\n");
            return 1;
        }
    }
}