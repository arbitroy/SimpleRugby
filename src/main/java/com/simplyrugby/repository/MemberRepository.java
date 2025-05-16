package com.simplyrugby.repository;

import com.simplyrugby.domain.Member;
import java.util.List;

/**
 * Repository interface for Member entity operations.
 */
public interface MemberRepository {
    /**
     * Find a member by ID
     * 
     * @param id The member ID
     * @return The member or null if not found
     */
    Member findById(int id);
    
    /**
     * Find all members
     * 
     * @return List of all members
     */
    List<Member> findAll();
    
    /**
     * Find members by name (partial match)
     * 
     * @param name The name to search for
     * @return List of matching members
     */
    List<Member> findByName(String name);
    
    /**
     * Save a new member
     * 
     * @param member The member to save
     * @return The ID of the newly created member
     */
    int save(Member member);
    
    /**
     * Update an existing member
     * 
     * @param member The member to update
     * @return True if the update was successful
     */
    boolean update(Member member);
    
    /**
     * Delete a member by ID
     * 
     * @param id The member ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Find members by email
     * 
     * @param email The email to search for
     * @return The member or null if not found
     */
    Member findByEmail(String email);
    
    /**
     * Find members by phone number
     * 
     * @param phone The phone number to search for
     * @return The member or null if not found
     */
    Member findByPhone(String phone);
}