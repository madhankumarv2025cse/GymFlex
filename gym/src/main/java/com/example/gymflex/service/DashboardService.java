package com.example.gymflex.service;

import com.example.gymflex.dto.DashboardResponse;
import com.example.gymflex.model.Membership;
import com.example.gymflex.repository.CheckInRepository;
import com.example.gymflex.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final MemberRepository memberRepository;
    private final CheckInRepository checkInRepository;
    private final MembershipService membershipService;

    public DashboardService(MemberRepository memberRepository,
                            CheckInRepository checkInRepository,
                            MembershipService membershipService) {
        this.memberRepository = memberRepository;
        this.checkInRepository = checkInRepository;
        this.membershipService = membershipService;
    }

    public DashboardResponse getDashboardData() {
        long totalMembers = memberRepository.count();

        List<Membership> allMemberships = membershipService.getAllMemberships();
        long activeMemberships = allMemberships.stream()
                .filter(m -> "ACTIVE".equalsIgnoreCase(m.getStatus()))
                .count();
        long expiredMemberships = allMemberships.stream()
                .filter(m -> "EXPIRED".equalsIgnoreCase(m.getStatus()))
                .count();

        long expiringWithin7Days = membershipService.getExpiringMemberships().size();

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        long totalCheckInsThisMonth = checkInRepository.countByCheckInDateBetween(startOfMonth, endOfMonth);

        return new DashboardResponse(
                totalMembers,
                activeMemberships,
                expiredMemberships,
                expiringWithin7Days,
                totalCheckInsThisMonth
        );
    }
}
