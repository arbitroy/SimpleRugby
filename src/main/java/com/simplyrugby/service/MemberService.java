package com.simplyrugby.service;

import com.simplyrugby.domain.Member;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.List;

/**
 * Service interface for Member entity operations.
 */
public interface MemberService {
    /**
     * Get a member by ID
     *
     * @param id The member ID
     * @return The member
     * @throws EntityNotFoundException If the member doesn't exist
     */
    Member getMemberById(int id);

    /**
     * Get all members
     *
     * @return List of all members
     */
    List<Member> getAllMembers();

    /**
     * Get members by name (partial match)
     *
     * @param name The name to search for
     * @return List of matching members
     */
    List<Member> getMembersByName(String name);

    /**
     * Add a new member
     *
     * @param member The member to add
     * @return The ID of the newly created member
     * @throws ValidationException If the member data is invalid
     */
    int addMember(Member member);

    /**
     * Update an existing member
     *
     * @param member The member to update
     * @return True if the update was successful
     * @throws ValidationException If the member data is invalid
     * @throws EntityNotFoundException If the member doesn't exist
     */
    boolean updateMember(Member member);

    /**
     * Delete a member by ID
     *
     * @param id The member ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the member doesn't exist
     */
    boolean deleteMember(int id);

    /**
     * Get a member by email
     *
     * @param email The email to search for
     * @return The member
     * @throws EntityNotFoundException If the member doesn't exist
     */
    Member getMemberByEmail(String email);

    /**
     * Get a member by phone number
     *
     * @param phone The phone number to search for
     * @return The member
     * @throws EntityNotFoundException If the member doesn't exist
     */
    Member getMemberByPhone(String phone);

    /**
     * Validate member data
     *
     * @param member The member to validate
     * @throws ValidationException If the member data is invalid
     */
    void validateMember(Member member);
}