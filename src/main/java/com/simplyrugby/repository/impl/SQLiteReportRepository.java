package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Report;
import com.simplyrugby.repository.ReportRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteReportRepository implements ReportRepository {
    private final ConnectionManager connectionManager;

    public SQLiteReportRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Report findById(int id) {
        String sql = "SELECT * FROM Report WHERE reportID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToReport(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding report with ID: " + id, e);
        }
    }

    @Override
    public List<Report> findAll() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report ORDER BY generatedDate DESC";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all reports", e);
        }
    }

    @Override
    public List<Report> findByTitle(String title) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report WHERE title LIKE ? ORDER BY generatedDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reports by title", e);
        }
    }

    @Override
    public List<Report> findByType(String reportType) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report WHERE reportType = ? ORDER BY generatedDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reportType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reports by type", e);
        }
    }

    @Override
    public List<Report> findByGeneratedBy(String generatedBy) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report WHERE generatedBy = ? ORDER BY generatedDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, generatedBy);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reports by generator", e);
        }
    }

    @Override
    public List<Report> findReportsAfterDate(Date date) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report WHERE generatedDate > ? ORDER BY generatedDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(1, sdf.format(date));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reports after date", e);
        }
    }

    @Override
    public List<Report> findReportsBeforeDate(Date date) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report WHERE generatedDate < ? ORDER BY generatedDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(1, sdf.format(date));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reports before date", e);
        }
    }

    @Override
    public List<Report> findReportsBetweenDates(Date startDate, Date endDate) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report WHERE generatedDate BETWEEN ? AND ? ORDER BY generatedDate DESC";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(1, sdf.format(startDate));
            pstmt.setString(2, sdf.format(endDate));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding reports between dates", e);
        }
    }

    @Override
    public int save(Report report) {
        String sql = "INSERT INTO Report (title, content, generatedDate, generatedBy, reportType) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, report.getTitle());
            pstmt.setString(2, report.getContent());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(3, sdf.format(report.getGeneratedDate()));

            pstmt.setString(4, report.getGeneratedBy());
            pstmt.setString(5, report.getReportType());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        report.setReportId(id);
                        return id;
                    }
                }
            }

            throw new RepositoryException("Creating report failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RepositoryException("Error saving report", e);
        }
    }

    @Override
    public boolean update(Report report) {
        String sql = "UPDATE Report SET title = ?, content = ?, generatedDate = ?, " +
                "generatedBy = ?, reportType = ? WHERE reportID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, report.getTitle());
            pstmt.setString(2, report.getContent());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(3, sdf.format(report.getGeneratedDate()));

            pstmt.setString(4, report.getGeneratedBy());
            pstmt.setString(5, report.getReportType());
            pstmt.setInt(6, report.getReportId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating report", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Report WHERE reportID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting report", e);
        }
    }

    @Override
    public List<Report> findRecentReports(int limit) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM Report ORDER BY generatedDate DESC LIMIT ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding recent reports", e);
        }
    }

    // Helper method to map ResultSet to Report object
    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("reportID"));
        report.setTitle(rs.getString("title"));
        report.setContent(rs.getString("content"));

        // Parse date from string
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date generatedDate = sdf.parse(rs.getString("generatedDate"));
            report.setGeneratedDate(generatedDate);
        } catch (ParseException e) {
            throw new SQLException("Error parsing generated date", e);
        }

        report.setGeneratedBy(rs.getString("generatedBy"));
        report.setReportType(rs.getString("reportType"));

        return report;
    }
}