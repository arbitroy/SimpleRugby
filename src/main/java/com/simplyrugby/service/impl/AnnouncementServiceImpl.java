package com.simplyrugby.service.impl;

import com.simplyrugby.domain.Announcement;
import com.simplyrugby.repository.AnnouncementRepository;
import com.simplyrugby.service.AnnouncementService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public Announcement getAnnouncementById(int id) {
        Announcement announcement = announcementRepository.findById(id);
        if (announcement == null) {
            throw new EntityNotFoundException("Announcement not found with ID: " + id);
        }
        return announcement;
    }

    @Override
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    @Override
    public List<Announcement> getAnnouncementsByTitle(String title) {
        return announcementRepository.findByTitle(title);
    }

    @Override
    public List<Announcement> getAnnouncementsBySender(String sentBy) {
        return announcementRepository.findBySentBy(sentBy);
    }

    @Override
    public List<Announcement> getAnnouncementsByRecipient(String recipient) {
        return announcementRepository.findByRecipient(recipient);
    }

    @Override
    public List<Announcement> getImportantAnnouncements() {
        return announcementRepository.findImportantAnnouncements();
    }

    @Override
    public List<Announcement> getAnnouncementsAfterDate(Date date) {
        return announcementRepository.findAnnouncementsAfterDate(date);
    }

    @Override
    public List<Announcement> getRecentAnnouncements(int limit) {
        return announcementRepository.findRecentAnnouncements(limit);
    }

    @Override
    public int addAnnouncement(Announcement announcement) {
        validateAnnouncement(announcement);
        return announcementRepository.save(announcement);
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        if (announcementRepository.findById(announcement.getAnnouncementId()) == null) {
            throw new EntityNotFoundException("Announcement not found with ID: " + announcement.getAnnouncementId());
        }
        validateAnnouncement(announcement);
        return announcementRepository.update(announcement);
    }

    @Override
    public boolean deleteAnnouncement(int id) {
        if (announcementRepository.findById(id) == null) {
            throw new EntityNotFoundException("Announcement not found with ID: " + id);
        }
        return announcementRepository.delete(id);
    }

    @Override
    public void validateAnnouncement(Announcement announcement) {
        List<String> errors = new ArrayList<>();

        // Validate title
        if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        } else if (announcement.getTitle().length() > 100) {
            errors.add("Title must be 100 characters or less");
        }

        // Validate content
        if (announcement.getContent() == null || announcement.getContent().trim().isEmpty()) {
            errors.add("Content is required");
        }

        // Validate sent by
        if (announcement.getSentBy() == null || announcement.getSentBy().trim().isEmpty()) {
            errors.add("Sender is required");
        }

        // Validate recipient
        if (announcement.getRecipient() == null || announcement.getRecipient().trim().isEmpty()) {
            errors.add("Recipient is required");
        }

        // Validate sent date
        if (announcement.getSentDate() == null) {
            errors.add("Sent date is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Announcement validation failed", errors);
        }
    }
}