package com.example.gymflex.service;

import com.example.gymflex.dto.MembershipRequest;
import com.example.gymflex.dto.RenewRequest;
import com.example.gymflex.exception.ResourceNotFoundException;
import com.example.gymflex.model.Member;
import com.example.gymflex.model.Membership;
import com.example.gymflex.model.Plan;
import com.example.gymflex.repository.MemberRepository;
import com.example.gymflex.repository.MembershipRepository;
import com.example.gymflex.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public MembershipService(MembershipRepository membershipRepository,
                             MemberRepository memberRepository,
                             PlanRepository planRepository) {
        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
        this.planRepository = planRepository;
    }

    public Membership createMembership(MembershipRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        LocalDate endDate = startDate.plusMonths(plan.getDurationInMonths());

        String status = endDate.isBefore(LocalDate.now()) ? "EXPIRED" : "ACTIVE";

        Membership membership = new Membership(startDate, endDate, status, member, plan);
        return membershipRepository.save(membership);
    }

    public Membership renewMembership(Long membershipId, RenewRequest request) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + membershipId));

        Plan plan = membership.getPlan();
        if (request != null && request.getPlanId() != null) {
            plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));
        }

        LocalDate today = LocalDate.now();
        LocalDate renewalStartDate;

        if (membership.getEndDate().isBefore(today)) {
            renewalStartDate = today;
        } else {
            renewalStartDate = membership.getEndDate();
        }

        LocalDate newEndDate = renewalStartDate.plusMonths(plan.getDurationInMonths());

        membership.setStartDate(renewalStartDate);
        membership.setEndDate(newEndDate);
        membership.setStatus("ACTIVE");
        membership.setPlan(plan);

        return membershipRepository.save(membership);
    }

    public Membership getMembershipById(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));
        updateMembershipStatusIfExpired(membership);
        return membership;
    }

    public List<Membership> getAllMemberships() {
        List<Membership> memberships = membershipRepository.findAll();
        for (Membership membership : memberships) {
            updateMembershipStatusIfExpired(membership);
        }
        return memberships;
    }

    public List<Membership> getMembershipsByMemberId(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        }
        List<Membership> memberships = membershipRepository.findByMemberId(memberId);
        for (Membership membership : memberships) {
            updateMembershipStatusIfExpired(membership);
        }
        return memberships;
    }

    public Optional<Membership> getActiveMembership(Long memberId) {
        List<Membership> memberships = membershipRepository.findByMemberIdOrderByEndDateDesc(memberId);
        LocalDate today = LocalDate.now();

        for (Membership membership : memberships) {
            updateMembershipStatusIfExpired(membership);
            if (!membership.getEndDate().isBefore(today) && "ACTIVE".equalsIgnoreCase(membership.getStatus())) {
                return Optional.of(membership);
            }
        }
        return Optional.empty();
    }

    public List<Membership> getExpiringMemberships() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        List<Membership> allMemberships = membershipRepository.findAll();
        for (Membership membership : allMemberships) {
            updateMembershipStatusIfExpired(membership);
        }

        return membershipRepository.findByEndDateBetweenAndStatus(today, sevenDaysLater, "ACTIVE");
    }

    private void updateMembershipStatusIfExpired(Membership membership) {
        if (membership.getEndDate().isBefore(LocalDate.now()) && "ACTIVE".equals(membership.getStatus())) {
            membership.setStatus("EXPIRED");
            membershipRepository.save(membership);
        }
    }
}
