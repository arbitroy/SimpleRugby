package com.simplyrugby.service;

import com.simplyrugby.domain.User;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.List;

/**
 * Service interface for User entity operations.
 */
public interface UserService {
    /**
     * Get a user by ID
     *
     * @param id The user ID
     * @return The user
     * @throws EntityNotFoundException If the user doesn't exist
     */
    User getUserById(int id);

    /**
     * Get all users
     *
     * @return List of all users
     */
    List<User> getAllUsers();

    /**
     * Get a user by username
     *
     * @param username The username
     * @return The user
     * @throws EntityNotFoundException If the user doesn't exist
     */
    User getUserByUsername(String username);

    /**
     * Get users by role
     *
     * @param role The role
     * @return List of users with the given role
     */
    List<User> getUsersByRole(String role);

    /**
     * Get a user by member ID
     *
     * @param memberId The member ID
     * @return The user
     * @throws EntityNotFoundException If the user doesn't exist
     */
    User getUserByMemberId(int memberId);

    /**
     * Add a new user
     *
     * @param user The user to add
     * @return The ID of the newly created user
     * @throws ValidationException If the user data is invalid
     */
    int addUser(User user);

    /**
     * Update an existing user
     *
     * @param user The user to update
     * @return True if the update was successful
     * @throws ValidationException If the user data is invalid
     * @throws EntityNotFoundException If the user doesn't exist
     */
    boolean updateUser(User user);

    /**
     * Delete a user by ID
     *
     * @param id The user ID to delete
     * @return True if the deletion was successful
     * @throws EntityNotFoundException If the user doesn't exist
     */
    boolean deleteUser(int id);

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
     * @throws EntityNotFoundException If the user doesn't exist
     * @throws ValidationException If the new password is invalid
     */
    boolean changePassword(int userId, String newPassword);

    /**
     * Check if a username is already taken
     *
     * @param username The username to check
     * @return True if the username is taken, false otherwise
     */
    boolean isUsernameTaken(String username);

    /**
     * Validate user data
     *
     * @param user The user to validate
     * @throws ValidationException If the user data is invalid
     */
    void validateUser(User user);

    /**
     * Validate a password
     *
     * @param password The password to validate
     * @throws ValidationException If the password is invalid
     */
    void validatePassword(String password);
}