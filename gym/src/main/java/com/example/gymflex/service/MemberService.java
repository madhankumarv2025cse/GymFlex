package com.example.gymflex.service;

import com.example.gymflex.exception.BusinessException;
import com.example.gymflex.exception.ResourceNotFoundException;
import com.example.gymflex.model.Member;
import com.example.gymflex.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new BusinessException("Member with email " + member.getEmail() + " already exists.");
        }
        if (member.getJoinDate() == null) {
            member.setJoinDate(LocalDate.now());
        }
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    public Member updateMember(Long id, Member memberDetails) {
        Member existingMember = getMemberById(id);

        if (memberRepository.existsByEmailAndIdNot(memberDetails.getEmail(), id)) {
            throw new BusinessException("Member with email " + memberDetails.getEmail() + " already exists.");
        }

        existingMember.setName(memberDetails.getName());
        existingMember.setEmail(memberDetails.getEmail());
        existingMember.setPhone(memberDetails.getPhone());
        if (memberDetails.getJoinDate() != null) {
            existingMember.setJoinDate(memberDetails.getJoinDate());
        }

        return memberRepository.save(existingMember);
    }

    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        memberRepository.delete(member);
    }
}
