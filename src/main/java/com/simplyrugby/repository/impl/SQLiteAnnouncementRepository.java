package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Announcement;
import com.simplyrugby.repository.AnnouncementRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteAnnouncementRepository implements AnnouncementRepository {
    private final ConnectionManager connectionManager;

    public SQLiteAnnouncementRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Announcement findById(int id) {
        String sql = "SELECT * FROM Announcement WHERE announcementID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAnnouncement(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding announcement with ID: " + id, e);
        }
    }

    @Override
    public List<Announcement> findAll() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all announcements", e);
        }
    }

    @Override
    public List<Announcement> findByTitle(String title) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE title LIKE ? ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding announcements by title", e);
        }
    }

    @Override
    public List<Announcement> findBySentBy(String sentBy) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE sentBy = ? ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sentBy);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding announcements by sender", e);
        }
    }

    @Override
    public List<Announcement> findByRecipient(String recipient) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE recipient = ? ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recipient);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding announcements by recipient", e);
        }
    }

    @Override
    public List<Announcement> findImportantAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE isImportant = 1 ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding important announcements", e);
        }
    }

    @Override
    public List<Announcement> findAnnouncementsAfterDate(Date date) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE sentDate > ? ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(1, sdf.format(date));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding announcements after date", e);
        }
    }

    @Override
    public List<Announcement> findAnnouncementsBeforeDate(Date date) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE sentDate < ? ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(1, sdf.format(date));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding announcements before date", e);
        }
    }

    @Override
    public List<Announcement> findAnnouncementsBetweenDates(Date startDate, Date endDate) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE sentDate BETWEEN ? AND ? ORDER BY sentDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(1, sdf.format(startDate));
            pstmt.setString(2, sdf.format(endDate));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding announcements between dates", e);
        }
    }

    @Override
    public int save(Announcement announcement) {
        String sql = "INSERT INTO Announcement (title, content, sentDate, sentBy, recipient, isImportant) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(3, sdf.format(announcement.getSentDate()));

            pstmt.setString(4, announcement.getSentBy());
            pstmt.setString(5, announcement.getRecipient());
            pstmt.setBoolean(6, announcement.isImportant());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        announcement.setAnnouncementId(id);
                        return id;
                    }
                }
            }

            throw new RepositoryException("Creating announcement failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RepositoryException("Error saving announcement", e);
        }
    }

    @Override
    public boolean update(Announcement announcement) {
        String sql = "UPDATE Announcement SET title = ?, content = ?, sentDate = ?, " +
                "sentBy = ?, recipient = ?, isImportant = ? WHERE announcementID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(3, sdf.format(announcement.getSentDate()));

            pstmt.setString(4, announcement.getSentBy());
            pstmt.setString(5, announcement.getRecipient());
            pstmt.setBoolean(6, announcement.isImportant());
            pstmt.setInt(7, announcement.getAnnouncementId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating announcement", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Announcement WHERE announcementID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting announcement", e);
        }
    }

    @Override
    public List<Announcement> findRecentAnnouncements(int limit) {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement ORDER BY sentDate DESC LIMIT ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
            return announcements;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding recent announcements", e);
        }
    }

    // Helper method to map ResultSet to Announcement object
    private Announcement mapResultSetToAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcementID"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));

        // Parse date from string
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date sentDate = sdf.parse(rs.getString("sentDate"));
            announcement.setSentDate(sentDate);
        } catch (ParseException e) {
            throw new SQLException("Error parsing sent date", e);
        }

        announcement.setSentBy(rs.getString("sentBy"));
        announcement.setRecipient(rs.getString("recipient"));
        announcement.setImportant(rs.getBoolean("isImportant"));

        return announcement;
    }
}