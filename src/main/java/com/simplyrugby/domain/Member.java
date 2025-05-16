package com.simplyrugby.domain;

import java.util.Date;

/**
 * Represents a basic member of the rugby club.
 * This class serves as the base class for specialized member types like Player and Coach.
 */
public class Member {
    private int memberId;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;
    private String phone;
    private String address;
    
    /**
     * Default constructor
     */
    public Member() {
    }
    
    /**
     * Constructor with all fields
     */
    public Member(int memberId, String firstName, String lastName, Date dateOfBirth, 
                 String email, String phone, String address) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    // Getters and Setters
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Returns the full name of the member.
     * 
     * @return First name and last name combined
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Calculates the age of the member based on their date of birth.
     * 
     * @return Age in years
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        
        Date now = new Date();
        long diffInMillis = now.getTime() - dateOfBirth.getTime();
        long ageInMillis = 31556952000L; // Approximate milliseconds in a year
        
        return (int) (diffInMillis / ageInMillis);
    }
    
    @Override
    public String toString() {
        return "Member{" + 
               "memberId=" + memberId + 
               ", firstName='" + firstName + '\'' + 
               ", lastName='" + lastName + '\'' + 
               ", dateOfBirth=" + dateOfBirth + 
               ", email='" + email + '\'' + 
               ", phone='" + phone + '\'' + 
               ", address='" + address + '\'' + 
               '}';
    }
}