package com.simplyrugby.repository;

import com.simplyrugby.domain.User;
import java.util.List;

/**
 * Repository interface for User entity operations.
 */
public interface UserRepository {
    /**
     * Find a user by ID
     * 
     * @param id The user ID
     * @return The user or null if not found
     */
    User findById(int id);
    
    /**
     * Find all users
     * 
     * @return List of all users
     */
    List<User> findAll();
    
    /**
     * Find a user by username
     * 
     * @param username The username
     * @return The user or null if not found
     */
    User findByUsername(String username);
    
    /**
     * Find users by role
     * 
     * @param role The role
     * @return List of users with the given role
     */
    List<User> findByRole(String role);
    
    /**
     * Find a user by member ID
     * 
     * @param memberId The member ID
     * @return The user or null if not found
     */
    User findByMemberId(int memberId);
    
    /**
     * Save a new user
     * 
     * @param user The user to save
     * @return The ID of the newly created user
     */
    int save(User user);
    
    /**
     * Update an existing user
     * 
     * @param user The user to update
     * @return True if the update was successful
     */
    boolean update(User user);
    
    /**
     * Delete a user by ID
     * 
     * @param id The user ID to delete
     * @return True if the deletion was successful
     */
    boolean delete(int id);
    
    /**
     * Authenticate a user with username and password
     * 
     * @param username The username
     * @param password The password
     * @return The user's role if authentication is successful, null otherwise
     */
    String authenticate(String username, String password);
    
    /**
     * Change a user's password
     * 
     * @param userId The user ID
     * @param newPassword The new password
     * @return True if the password change was successful
     */
    boolean changePassword(int userId, String newPassword);
    
    /**
     * Check if a username is already taken
     * 
     * @param username The username to check
     * @return True if the username is taken, false otherwise
     */
    boolean isUsernameTaken(String username);
}