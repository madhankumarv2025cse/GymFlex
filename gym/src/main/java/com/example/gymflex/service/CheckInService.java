package com.example.gymflex.service;

import com.example.gymflex.dto.MonthlyAttendanceResponse;
import com.example.gymflex.exception.BusinessException;
import com.example.gymflex.exception.ResourceNotFoundException;
import com.example.gymflex.model.CheckIn;
import com.example.gymflex.model.Member;
import com.example.gymflex.model.Membership;
import com.example.gymflex.repository.CheckInRepository;
import com.example.gymflex.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final MemberRepository memberRepository;
    private final MembershipService membershipService;

    public CheckInService(CheckInRepository checkInRepository,
                           MemberRepository memberRepository,
                           MembershipService membershipService) {
        this.checkInRepository = checkInRepository;
        this.memberRepository = memberRepository;
        this.membershipService = membershipService;
    }

    public CheckIn checkInMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        Optional<Membership> activeMembershipOpt = membershipService.getActiveMembership(memberId);

        if (activeMembershipOpt.isEmpty()) {
            throw new BusinessException("Check-in rejected. Membership has expired.");
        }

        Membership activeMembership = activeMembershipOpt.get();
        if (activeMembership.getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Check-in rejected. Membership has expired.");
        }

        CheckIn checkIn = new CheckIn(LocalDate.now(), member);
        return checkInRepository.save(checkIn);
    }

    public List<CheckIn> getMemberCheckIns(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        }
        return checkInRepository.findByMemberIdOrderByCheckInDateDesc(memberId);
    }

    public MonthlyAttendanceResponse getMonthlyAttendanceCount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        long count = checkInRepository.countByMemberIdAndCheckInDateBetween(memberId, startOfMonth, endOfMonth);

        return new MonthlyAttendanceResponse(
                member.getId(),
                member.getName(),
                now.getMonth().name(),
                now.getYear(),
                count
        );
    }
}
