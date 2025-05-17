package com.simplyrugby.util;

import com.simplyrugby.repository.*;
import com.simplyrugby.repository.impl.*;
import com.simplyrugby.service.*;
import com.simplyrugby.service.impl.*;

/**
 * Utility class for managing dependencies.
 */
public class DependencyManager {
    private static ConnectionManager connectionManager;

    /**
     * Initialize all dependencies.
     *
     * @param dbName The database name
     */
    public static void initialize(String dbName) {
        // Initialize connection manager
        connectionManager = new ConnectionManager(dbName);

        // Initialize repositories
        MemberRepository memberRepository = new SQLiteMemberRepository(connectionManager);
        PlayerRepository playerRepository = new SQLitePlayerRepository(connectionManager);
        CoachRepository coachRepository = new SQLiteCoachRepository(connectionManager);
        SquadRepository squadRepository = new SQLiteSquadRepository(connectionManager);
        GameRepository gameRepository = new SQLiteGameRepository(connectionManager);
        TrainingRepository trainingRepository = new SQLiteTrainingRepository(connectionManager);
        UserRepository userRepository = new SQLiteUserRepository(connectionManager);
        AnnouncementRepository announcementRepository = new SQLiteAnnouncementRepository(connectionManager);
        ReportRepository reportRepository = new SQLiteReportRepository(connectionManager);

        // Initialize services
        MemberService memberService = new MemberServiceImpl(memberRepository);
        PlayerService playerService = new PlayerServiceImpl(playerRepository, squadRepository,
                gameRepository, trainingRepository);
        CoachService coachService = new CoachServiceImpl(coachRepository, squadRepository);
        SquadService squadService = new SquadServiceImpl(squadRepository, playerRepository, coachRepository);
        GameService gameService = new GameServiceImpl(gameRepository, playerRepository, squadRepository);
        TrainingService trainingService = new TrainingServiceImpl(trainingRepository, playerRepository, squadRepository);
        UserService userService = new UserServiceImpl(userRepository, memberRepository);
        AnnouncementService announcementService = new AnnouncementServiceImpl(announcementRepository);
        ReportService reportService = new ReportServiceImpl(reportRepository);

        // Register services in service locator
        ServiceLocator.register(MemberService.class, memberService);
        ServiceLocator.register(PlayerService.class, playerService);
        ServiceLocator.register(CoachService.class, coachService);
        ServiceLocator.register(SquadService.class, squadService);
        ServiceLocator.register(GameService.class, gameService);
        ServiceLocator.register(TrainingService.class, trainingService);
        ServiceLocator.register(UserService.class, userService);
        ServiceLocator.register(AnnouncementService.class, announcementService);
        ServiceLocator.register(ReportService.class, reportService);
    }

    /**
     * Shutdown and clean up resources.
     */
    public static void shutdown() {
        if (connectionManager != null) {
            connectionManager.closeConnection();
        }
        ServiceLocator.clear();
    }

    /**
     * Get the member service.
     *
     * @return The member service
     */
    public static MemberService getMemberService() {
        return ServiceLocator.getService(MemberService.class);
    }

    /**
     * Get the player service.
     *
     * @return The player service
     */
    public static PlayerService getPlayerService() {
        return ServiceLocator.getService(PlayerService.class);
    }

    /**
     * Get the coach service.
     *
     * @return The coach service
     */
    public static CoachService getCoachService() {
        return ServiceLocator.getService(CoachService.class);
    }

    /**
     * Get the squad service.
     *
     * @return The squad service
     */
    public static SquadService getSquadService() {
        return ServiceLocator.getService(SquadService.class);
    }

    /**
     * Get the game service.
     *
     * @return The game service
     */
    public static GameService getGameService() {
        return ServiceLocator.getService(GameService.class);
    }

    /**
     * Get the training service.
     *
     * @return The training service
     */
    public static TrainingService getTrainingService() {
        return ServiceLocator.getService(TrainingService.class);
    }

    /**
     * Get the user service.
     *
     * @return The user service
     */
    public static UserService getUserService() {
        return ServiceLocator.getService(UserService.class);
    }

    public static AnnouncementService getAnnouncementService() {
        return ServiceLocator.getService(AnnouncementService.class);
    }

    public static ReportService getReportService() {
        return ServiceLocator.getService(ReportService.class);
    }
}