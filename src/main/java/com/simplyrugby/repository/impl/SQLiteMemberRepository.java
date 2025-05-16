package com.simplyrugby.repository.impl;

import com.simplyrugby.domain.Member;
import com.simplyrugby.repository.MemberRepository;
import com.simplyrugby.util.RepositoryException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLiteMemberRepository implements MemberRepository {
    private final ConnectionManager connectionManager;

    public SQLiteMemberRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Member findById(int id) {
        String sql = "SELECT * FROM Member WHERE memberID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding member with ID: " + id, e);
        }
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Member";

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            return members;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding all members", e);
        }
    }

    @Override
    public List<Member> findByName(String name) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Member WHERE firstName LIKE ? OR lastName LIKE ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + name + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            return members;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding members by name: " + name, e);
        }
    }

    @Override
    public int save(Member member) {
        String sql = "INSERT INTO Member (firstName, lastName, dob, email, phone, address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());

            // Format date as string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(member.getDateOfBirth()));

            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getPhone());
            pstmt.setString(6, member.getAddress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        member.setMemberId(id);
                        return id;
                    }
                }
            }

            throw new RepositoryException("Creating member failed, no ID obtained.");
        } catch (SQLException e) {
            throw new RepositoryException("Error saving member", e);
        }
    }

    @Override
    public boolean update(Member member) {
        String sql = "UPDATE Member SET firstName = ?, lastName = ?, dob = ?, " +
                "email = ?, phone = ?, address = ? WHERE memberID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getFirstName());
            pstmt.setString(2, member.getLastName());

            // Format date as string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(3, sdf.format(member.getDateOfBirth()));

            pstmt.setString(4, member.getEmail());
            pstmt.setString(5, member.getPhone());
            pstmt.setString(6, member.getAddress());
            pstmt.setInt(7, member.getMemberId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating member", e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Member WHERE memberID = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting member", e);
        }
    }

    @Override
    public Member findByEmail(String email) {
        String sql = "SELECT * FROM Member WHERE email = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding member by email: " + email, e);
        }
    }

    @Override
    public Member findByPhone(String phone) {
        String sql = "SELECT * FROM Member WHERE phone = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error finding member by phone: " + phone, e);
        }
    }

    // Helper method to map ResultSet to Member object
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setMemberId(rs.getInt("memberID"));
        member.setFirstName(rs.getString("firstName"));
        member.setLastName(rs.getString("lastName"));

        // Parse date from string
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = sdf.parse(rs.getString("dob"));
            member.setDateOfBirth(dob);
        } catch (ParseException e) {
            throw new SQLException("Error parsing date of birth", e);
        }

        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setAddress(rs.getString("address"));

        return member;
    }
}