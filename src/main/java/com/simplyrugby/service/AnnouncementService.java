package com.simplyrugby.service;

import com.simplyrugby.domain.Announcement;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.Date;
import java.util.List;

/**
 * Service interface for Announcement entity operations.
 */
public interface AnnouncementService {
    /**
     * Get an announcement by ID
     *
     * @param id The announcement ID
     * @return The announcement
     * @throws EntityNotFoundException If the announcement doesn't exist
     */
    Announcement getAnnouncementById(int id);

    /**
     * Get all announcements
     *
     * @return List of all announcements
     */
    List<Announcement> getAllAnnouncements();

    /**
     * Get announcements by title (partial match)
     *
     * @param title The title to search for
     * @return List of matching announcements
     */
    List<Announcement> getAnnouncementsByTitle(String title);

    /**
     * Get announcements sent by a specific user
     *
     * @param sentBy The username of the user who sent the announcements
     * @return List of announcements sent by the user
     */
    List<Announcement> getAnnouncementsBySender(String sentBy);

    /**
     * Get announcements for a specific recipient
     *
     * @param recipient The recipient
     * @return List of announcements for the recipient
     */
    List<Announcement> getAnnouncementsByRecipient(String recipient);

    /**
     * Get important announcements
     *
     * @return List of important announcements
     */
    List<Announcement> getImportantAnnouncements();

    /**
     * Get announcements sent after a specific date
     *
     * @param date The date
     * @return List of announcements sent after the date
     */
    List<Announcement> getAnnouncementsAfterDate(Date date);

    /**
     * Get the most recent announcements
     *
     * @param limit The maximum number of announcements to return
     * @return List of recent announcements
     */
    List<Announcement> getRecentAnnouncements(int limit);

    /**
     * Add a new announcement
     *
     * @param announcement The announcement to add
     * @return The ID of the newly created announcement
     * @throws ValidationException If the announcement data is invalid
     */
    int addAnnouncement(Announcement announcement);

    /**
     * Update an existing announcement
     *
     * @param announcement The announcement to update
     * @return True if the update was successful
     * @throws ValidationException If the announcement data is invalid
     * @throws EntityNotFoundException If the announcement doesn't exist
     */
    boolean updateAnnouncement(Announcement announcement);

    /**
     * Delete an announcement by ID
     *
     * @param id The announcement ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the announcement doesn't exist
     */
    boolean deleteAnnouncement(int id);

    /**
     * Validate announcement data
     *
     * @param announcement The announcement to validate
     * @throws ValidationException If the announcement data is invalid
     */
    void validateAnnouncement(Announcement announcement);
}