package com.simplyrugby.service.impl;

import com.simplyrugby.domain.Coach;
import com.simplyrugby.domain.Squad;
import com.simplyrugby.repository.CoachRepository;
import com.simplyrugby.repository.SquadRepository;
import com.simplyrugby.service.CoachService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class CoachServiceImpl implements CoachService {
    private final CoachRepository coachRepository;
    private final SquadRepository squadRepository;

    public CoachServiceImpl(CoachRepository coachRepository, SquadRepository squadRepository) {
        this.coachRepository = coachRepository;
        this.squadRepository = squadRepository;
    }

    @Override
    public Coach getCoachById(int id) {
        Coach coach = coachRepository.findById(id);
        if (coach == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + id);
        }
        return coach;
    }

    @Override
    public Coach getCoachByMemberId(int memberId) {
        Coach coach = coachRepository.findByMemberId(memberId);
        if (coach == null) {
            throw new EntityNotFoundException("Coach not found with member ID: " + memberId);
        }
        return coach;
    }

    @Override
    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    @Override
    public List<Coach> getCoachesByName(String name) {
        return coachRepository.findByName(name);
    }

    @Override
    public List<Coach> getCoachesBySquad(int squadId) {
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }
        return coachRepository.findBySquad(squadId);
    }

    @Override
    public List<Coach> getCoachesByQualification(String qualification) {
        return coachRepository.findByQualification(qualification);
    }

    @Override
    public int addCoach(Coach coach) {
        validateCoach(coach);
        return coachRepository.save(coach);
    }

    @Override
    public boolean updateCoach(Coach coach) {
        if (coachRepository.findById(coach.getCoachId()) == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + coach.getCoachId());
        }
        validateCoach(coach);
        return coachRepository.update(coach);
    }

    @Override
    public boolean deleteCoach(int id) {
        if (coachRepository.findById(id) == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + id);
        }
        return coachRepository.delete(id);
    }

    @Override
    public boolean assignCoachToSquad(int coachId, int squadId) {
        // Check if both coach and squad exist
        if (coachRepository.findById(coachId) == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + coachId);
        }
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return coachRepository.assignToSquad(coachId, squadId);
    }

    @Override
    public boolean removeCoachFromSquad(int coachId, int squadId) {
        // Check if both coach and squad exist
        if (coachRepository.findById(coachId) == null) {
            throw new EntityNotFoundException("Coach not found with ID: " + coachId);
        }
        if (squadRepository.findById(squadId) == null) {
            throw new EntityNotFoundException("Squad not found with ID: " + squadId);
        }

        return coachRepository.removeFromSquad(coachId, squadId);
    }

    @Override
    public List<Squad> getAssignedSquads(int coachId) {
        Coach coach = getCoachById(coachId);
        return coach.getAssignedSquads();
    }

    @Override
    public void validateCoach(Coach coach) {
        List<String> errors = new ArrayList<>();

        // Validate member fields first
        validateMemberFields(coach, errors);

        // Validate coach fields
        if (coach.getQualifications() == null || coach.getQualifications().trim().isEmpty()) {
            errors.add("Qualifications are required");
        } else if (coach.getQualifications().length() > 100) {
            errors.add("Qualifications must be 100 characters or less");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Coach validation failed", errors);
        }
    }

    private void validateMemberFields(Coach coach, List<String> errors) {
        // Validate first name
        if (coach.getFirstName() == null || coach.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        } else if (!coach.getFirstName().matches("[a-zA-Z ]{1,20}")) {
            errors.add("First name must contain only letters and spaces, and be 20 characters or less");
        }

        // Validate last name
        if (coach.getLastName() == null || coach.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        } else if (!coach.getLastName().matches("[a-zA-Z ]{1,20}")) {
            errors.add("Last name must contain only letters and spaces, and be 20 characters or less");
        }

        // Validate date of birth
        if (coach.getDateOfBirth() == null) {
            errors.add("Date of birth is required");
        } else if (coach.getDateOfBirth().after(new java.util.Date())) {
            errors.add("Date of birth cannot be in the future");
        }

        // Validate email
        if (coach.getEmail() != null && !coach.getEmail().isEmpty()) {
            if (!coach.getEmail().contains("@") || !coach.getEmail().contains(".")) {
                errors.add("Email must be a valid email address");
            } else if (coach.getEmail().length() > 50) {
                errors.add("Email must be 50 characters or less");
            }
        }

        // Validate phone
        if (coach.getPhone() != null && !coach.getPhone().isEmpty()) {
            if (!coach.getPhone().matches("\\d{11}")) {
                errors.add("Phone number must be 11 digits");
            }
        }

        // Validate address
        if (coach.getAddress() != null && coach.getAddress().length() > 60) {
            errors.add("Address must be 60 characters or less");
        }
    }
}