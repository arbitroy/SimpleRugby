package com.simplyrugby.repository;

import com.simplyrugby.domain.Announcement;
import java.util.Date;
import java.util.List;

/**
 * Repository interface for Announcement entity operations.
 */
public interface AnnouncementRepository {
    /**
     * Find an announcement by ID
     * 
     * @param id The announcement ID
     * @return The announcement or null if not found
     */
    Announcement findById(int id);
    
    /**
     * Find all announcements
     * 
     * @return List of all announcements
     */
    List<Announcement> findAll();
    
    /**
     * Find announcements by title (partial match)
     * 
     * @param title The title to search for
     * @return List of matching announcements
     */
    List<Announcement> findByTitle(String title);
    
    /**
     * Find announcements sent by a specific user
     * 
     * @param sentBy The username of the user who sent the announcements
     * @return List of announcements sent by the user
     */
    List<Announcement> findBySentBy(String sentBy);
    
    /**
     * Find announcements for a specific recipient
     * 
     * @param recipient The recipient
     * @return List of announcements for the recipient
     */
    List<Announcement> findByRecipient(String recipient);
    
    /**
     * Find important announcements
     * 
     * @return List of important announcements
     */
    List<Announcement> findImportantAnnouncements();
    
    /**
     * Find announcements sent after a specific date
     * 
     * @param date The date
     * @return List of announcements sent after the date
     */
    List<Announcement> findAnnouncementsAfterDate(Date date);
    
    /**
     * Find announcements sent before a specific date
     * 
     * @param date The date
     * @return List of announcements sent before the date
     */
    List<Announcement> findAnnouncementsBeforeDate(Date date);
    
    /**
     * Find announcements sent between two dates
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of announcements sent between the dates
     */
    List<Announcement> findAnnouncementsBetweenDates(Date startDate, Date endDate);
    
    /**
     * Save a new announcement
     * 
     * @param announcement The announcement to save
     * @return The ID of the newly created announcement
     */
    int save(Announcement announcement);
    
    /**
     * Update an existing announcement
     * 
     * @param announcement The announcement to update
     * @return True if the update was successful
     */
    boolean update(Announcement announcement);
    
    /**
     * Delete an announcement by ID
     * 
     * @param id The announcement ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Find the most recent announcements
     * 
     * @param limit The maximum number of announcements to return
     * @return List of recent announcements
     */
    List<Announcement> findRecentAnnouncements(int limit);
}