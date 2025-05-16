package com.simplyrugby.domain;

/**
 * Represents a user of the system with authentication credentials.
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private int memberId;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with all fields
     */
    public User(int userId, String username, String password, String role, int memberId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.memberId = memberId;
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    /**
     * Checks if this user is a Secretary
     * 
     * @return true if the user is a Secretary, false otherwise
     */
    public boolean isSecretary() {
        return "Secretary".equals(role);
    }

    /**
     * Checks if this user is a Coach
     * 
     * @return true if the user is a Coach, false otherwise
     */
    public boolean isCoach() {
        return "Coach".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", memberId=" + memberId +
                '}';
    }
}