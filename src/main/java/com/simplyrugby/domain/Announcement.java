package com.simplyrugby.domain;

import java.util.Date;

/**
 * Represents an announcement sent to club members.
 */
public class Announcement {
    private int announcementId;
    private String title;
    private String content;
    private Date sentDate;
    private String sentBy;
    private String recipient;
    private boolean isImportant;

    /**
     * Default constructor
     */
    public Announcement() {
        this.sentDate = new Date();
        this.isImportant = false;
    }

    /**
     * Constructor with all fields
     */
    public Announcement(int announcementId, String title, String content, Date sentDate,
            String sentBy, String recipient, boolean isImportant) {
        this.announcementId = announcementId;
        this.title = title;
        this.content = content;
        this.sentDate = sentDate != null ? sentDate : new Date();
        this.sentBy = sentBy;
        this.recipient = recipient;
        this.isImportant = isImportant;
    }

    // Getters and Setters

    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    /**
     * Gets a formatted version of the sent date
     * 
     * @return The formatted date
     */
    public String getFormattedSentDate() {
        if (sentDate == null) {
            return "Unknown";
        }

        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(sentDate);
    }

    /**
     * Gets an excerpt of the content (first 50 characters)
     * 
     * @return The content excerpt
     */
    public String getContentExcerpt() {
        if (content == null || content.isEmpty()) {
            return "";
        }

        if (content.length() <= 50) {
            return content;
        }

        return content.substring(0, 47) + "...";
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "announcementId=" + announcementId +
                ", title='" + title + '\'' +
                ", sentDate=" + getFormattedSentDate() +
                ", sentBy='" + sentBy + '\'' +
                ", recipient='" + recipient + '\'' +
                ", isImportant=" + isImportant +
                '}';
    }
}