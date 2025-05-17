package com.simplyrugby.cli.secretary;

import com.simplyrugby.domain.Announcement;
import com.simplyrugby.service.AnnouncementService;
import com.simplyrugby.service.UserService;
import com.simplyrugby.util.DependencyManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "announce",
        description = "Manage club announcements",
        subcommands = {
                AnnouncementCommands.ListCommand.class,
                AnnouncementCommands.AddCommand.class,
                AnnouncementCommands.ViewCommand.class,
                AnnouncementCommands.DeleteCommand.class,
                CommandLine.HelpCommand.class
        },
        mixinStandardHelpOptions = true
)
public class AnnouncementCommands implements Runnable {

    @Override
    public void run() {
        System.out.println("\n==================================");
        System.out.println("        ANNOUNCEMENTS PANEL");
        System.out.println("==================================\n");
        System.out.println("Available commands:");
        System.out.println("  list    - List all announcements");
        System.out.println("  add     - Add a new announcement");
        System.out.println("  view    - View announcement details");
        System.out.println("  delete  - Delete an announcement");
        System.out.println("  help    - Show help\n");
    }

    @Command(name = "list", description = "List all announcements")
    static class ListCommand implements Callable<Integer> {
        @Option(names = {"-i", "--important"}, description = "Show only important announcements")
        boolean importantOnly;

        @Option(names = {"-r", "--recent"}, description = "Show only recent announcements")
        boolean recentOnly;

        @Override
        public Integer call() {
            // For this implementation, we'll assume there's an AnnouncementService
            // Similar to other services in the project
            AnnouncementService announcementService = DependencyManager.getAnnouncementService();

            try {
                List<Announcement> announcements;

                if (recentOnly) {
                    System.out.println("\n=== Recent Announcements ===\n");
                    // Assuming a method to get recent announcements
                    announcements = announcementService.getRecentAnnouncements(10);
                } else {
                    System.out.println("\n=== All Announcements ===\n");
                    announcements = announcementService.getAllAnnouncements();
                }

                // Filter important announcements if requested
                if (importantOnly) {
                    announcements.removeIf(a -> !a.isImportant());
                }

                // Display announcements table
                if (announcements.isEmpty()) {
                    System.out.println("No announcements found.");
                } else {
                    System.out.printf("%-4s %-12s %-20s %-30s %-10s\n",
                            "ID", "Date", "From", "Title", "Important");
                    System.out.println("-".repeat(85));

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    for (Announcement announcement : announcements) {
                        System.out.printf("%-4d %-12s %-20s %-30s %-10s\n",
                                announcement.getAnnouncementId(),
                                sdf.format(announcement.getSentDate()),
                                announcement.getSentBy(),
                                truncate(announcement.getTitle(), 30),
                                announcement.isImportant() ? "Yes" : "No");
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

    @Command(name = "add", description = "Add a new announcement")
    static class AddCommand implements Callable<Integer> {
        @Option(names = {"-t", "--title"}, description = "Announcement title", required = true)
        String title;

        @Option(names = {"-c", "--content"}, description = "Announcement content", required = true)
        String content;

        @Option(names = {"-r", "--recipient"}, description = "Recipient (e.g., 'All', 'Coaches', 'Parents')", defaultValue = "All")
        String recipient;

        @Option(names = {"-i", "--important"}, description = "Mark as important")
        boolean important;

        @Override
        public Integer call() {
            AnnouncementService announcementService = DependencyManager.getAnnouncementService();
            UserService userService = DependencyManager.getUserService();

            try {
                Announcement announcement = new Announcement();
                announcement.setTitle(title);
                announcement.setContent(content);
                announcement.setRecipient(recipient);
                announcement.setImportant(important);
                announcement.setSentDate(new Date());

                // Get current username (in a real app, this would be from the session)
                String username = System.getProperty("user.name");
                announcement.setSentBy(username);

                // Save announcement
                int announcementId = announcementService.addAnnouncement(announcement);

                System.out.println("\nAnnouncement published successfully with ID: " + announcementId);
                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "view", description = "View announcement details")
    static class ViewCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Announcement ID")
        int announcementId;

        @Override
        public Integer call() {
            AnnouncementService announcementService = DependencyManager.getAnnouncementService();

            try {
                Announcement announcement = announcementService.getAnnouncementById(announcementId);

                System.out.println("\n=== Announcement Details ===\n");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                System.out.println("ID:        " + announcement.getAnnouncementId());
                System.out.println("Title:     " + announcement.getTitle());
                System.out.println("Sent By:   " + announcement.getSentBy());
                System.out.println("Sent Date: " + sdf.format(announcement.getSentDate()));
                System.out.println("Recipient: " + announcement.getRecipient());
                System.out.println("Important: " + (announcement.isImportant() ? "Yes" : "No"));
                System.out.println("\nContent:");
                System.out.println("-".repeat(50));
                System.out.println(announcement.getContent());
                System.out.println("-".repeat(50));

                return 0;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "delete", description = "Delete an announcement")
    static class DeleteCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Announcement ID")
        int announcementId;

        @Option(names = {"-f", "--force"}, description = "Force deletion without confirmation")
        boolean force;

        @Override
        public Integer call() {
            AnnouncementService announcementService = DependencyManager.getAnnouncementService();

            try {
                // Get announcement for confirmation
                Announcement announcement = announcementService.getAnnouncementById(announcementId);

                // Confirm deletion
                if (!force) {
                    System.out.printf("\nAre you sure you want to delete announcement: %s (ID: %d)? (y/n) ",
                            announcement.getTitle(), announcement.getAnnouncementId());
                    String confirmation = System.console().readLine().trim().toLowerCase();

                    if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                        System.out.println("\nDeletion cancelled.");
                        return 0;
                    }
                }

                // Delete announcement
                boolean result = announcementService.deleteAnnouncement(announcementId);

                if (result) {
                    System.out.println("\nAnnouncement deleted successfully.");
                    return 0;
                } else {
                    System.err.println("Failed to delete announcement.");
                    return 1;
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
        }
    }
}