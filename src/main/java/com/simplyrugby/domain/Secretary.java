package com.simplyrugby.domain;

import java.util.Date;

/**
 * Represents a secretary in the rugby club.
 * Extends the Member class to include secretary-specific information.
 */
public class Secretary extends Member {
    private int secretaryId;

    /**
     * Default constructor
     */
    public Secretary() {
        super();
    }

    /**
     * Constructor with member fields
     */
    public Secretary(int memberId, String firstName, String lastName, Date dateOfBirth,
            String email, String phone, String address) {
        super(memberId, firstName, lastName, dateOfBirth, email, phone, address);
    }

    /**
     * Constructor with all fields
     */
    public Secretary(int memberId, String firstName, String lastName, Date dateOfBirth,
            String email, String phone, String address, int secretaryId) {
        super(memberId, firstName, lastName, dateOfBirth, email, phone, address);
        this.secretaryId = secretaryId;
    }

    // Getters and Setters

    public int getSecretaryId() {
        return secretaryId;
    }

    public void setSecretaryId(int secretaryId) {
        this.secretaryId = secretaryId;
    }

    @Override
    public String toString() {
        return "Secretary{" +
                "secretaryId=" + secretaryId +
                ", memberInfo=" + super.toString() +
                '}';
    }
}