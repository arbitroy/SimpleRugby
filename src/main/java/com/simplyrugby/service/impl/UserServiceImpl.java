package com.simplyrugby.service.impl;

import com.simplyrugby.domain.User;
import com.simplyrugby.repository.MemberRepository;
import com.simplyrugby.repository.UserRepository;
import com.simplyrugby.service.UserService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public UserServiceImpl(UserRepository userRepository, MemberRepository memberRepository) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public User getUserById(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User getUserByMemberId(int memberId) {
        User user = userRepository.findByMemberId(memberId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with member ID: " + memberId);
        }
        return user;
    }

    @Override
    public int addUser(User user) {
        validateUser(user);

        // Check if username is already taken
        if (userRepository.isUsernameTaken(user.getUsername())) {
            throw new ValidationException("Username is already taken", (List<String>) null);
        }

        // Check if member exists if member ID is provided
        if (user.getMemberId() > 0 && memberRepository.findById(user.getMemberId()) == null) {
            throw new EntityNotFoundException("Member not found with ID: " + user.getMemberId());
        }

        return userRepository.save(user);
    }

    @Override
    public boolean updateUser(User user) {
        if (userRepository.findById(user.getUserId()) == null) {
            throw new EntityNotFoundException("User not found with ID: " + user.getUserId());
        }

        validateUser(user);

        // Check if username is already taken by another user
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null && existingUser.getUserId() != user.getUserId()) {
            throw new ValidationException("Username is already taken", (List<String>) null);
        }

        // Check if member exists if member ID is provided
        if (user.getMemberId() > 0 && memberRepository.findById(user.getMemberId()) == null) {
            throw new EntityNotFoundException("Member not found with ID: " + user.getMemberId());
        }

        return userRepository.update(user);
    }

    @Override
    public boolean deleteUser(int id) {
        if (userRepository.findById(id) == null) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }

        return userRepository.delete(id);
    }

    @Override
    public String authenticate(String username, String password) {
        return userRepository.authenticate(username, password);
    }

    @Override
    public boolean changePassword(int userId, String newPassword) {
        if (userRepository.findById(userId) == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        validatePassword(newPassword);

        return userRepository.changePassword(userId, newPassword);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.isUsernameTaken(username);
    }

    @Override
    public void validateUser(User user) {
        List<String> errors = new ArrayList<>();

        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.add("Username is required");
        } else if (!user.getUsername().matches("[a-zA-Z0-9]{3,20}")) {
            errors.add("Username must contain only letters and numbers, and be between 3 and 20 characters");
        }

        // Validate password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            errors.add("Password is required");
        } else {
            try {
                validatePassword(user.getPassword());
            } catch (ValidationException e) {
                if (e.getErrors() != null) {
                    errors.addAll(e.getErrors());
                } else {
                    errors.add(e.getMessage());
                }
            }
        }

        // Validate role
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            errors.add("Role is required");
        } else if (!user.getRole().equals("Secretary") && !user.getRole().equals("Coach")) {
            errors.add("Role must be either 'Secretary' or 'Coach'");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("User validation failed", errors);
        }
    }

    @Override
    public void validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.trim().isEmpty()) {
            errors.add("Password is required");
        } else if (password.length() < 8) {
            errors.add("Password must be at least 8 characters long");
        } else if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter");
        } else if (!password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least one lowercase letter");
        } else if (!password.matches(".*\\d.*")) {
            errors.add("Password must contain at least one digit");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Password validation failed", errors);
        }
    }
}