package com.simplyrugby.service.impl;

import com.simplyrugby.domain.Member;
import com.simplyrugby.repository.MemberRepository;
import com.simplyrugby.service.MemberService;
import com.simplyrugby.util.EntityNotFoundException;
import com.simplyrugby.util.ValidationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member getMemberById(int id) {
        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with ID: " + id);
        }
        return member;
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public List<Member> getMembersByName(String name) {
        return memberRepository.findByName(name);
    }

    @Override
    public int addMember(Member member) {
        validateMember(member);
        return memberRepository.save(member);
    }

    @Override
    public boolean updateMember(Member member) {
        if (memberRepository.findById(member.getMemberId()) == null) {
            throw new EntityNotFoundException("Member not found with ID: " + member.getMemberId());
        }
        validateMember(member);
        return memberRepository.update(member);
    }

    @Override
    public boolean deleteMember(int id) {
        if (memberRepository.findById(id) == null) {
            throw new EntityNotFoundException("Member not found with ID: " + id);
        }
        return memberRepository.delete(id);
    }

    @Override
    public Member getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with email: " + email);
        }
        return member;
    }

    @Override
    public Member getMemberByPhone(String phone) {
        Member member = memberRepository.findByPhone(phone);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with phone number: " + phone);
        }
        return member;
    }

    @Override
    public void validateMember(Member member) {
        List<String> errors = new ArrayList<>();

        // Validate first name
        if (member.getFirstName() == null || member.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        } else if (!member.getFirstName().matches("[a-zA-Z ]{1,20}")) {
            errors.add("First name must contain only letters and spaces, and be 20 characters or less");
        }

        // Validate last name
        if (member.getLastName() == null || member.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        } else if (!member.getLastName().matches("[a-zA-Z ]{1,20}")) {
            errors.add("Last name must contain only letters and spaces, and be 20 characters or less");
        }

        // Validate date of birth
        if (member.getDateOfBirth() == null) {
            errors.add("Date of birth is required");
        } else if (member.getDateOfBirth().after(new Date())) {
            errors.add("Date of birth cannot be in the future");
        }

        // Validate email
        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            if (!member.getEmail().contains("@") || !member.getEmail().contains(".")) {
                errors.add("Email must be a valid email address");
            } else if (member.getEmail().length() > 50) {
                errors.add("Email must be 50 characters or less");
            }
        }

        // Validate phone
        if (member.getPhone() != null && !member.getPhone().isEmpty()) {
            if (!member.getPhone().matches("\\d{11}")) {
                errors.add("Phone number must be 11 digits");
            }
        }

        // Validate address
        if (member.getAddress() != null && member.getAddress().length() > 60) {
            errors.add("Address must be 60 characters or less");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Member validation failed", errors);
        }
    }
}