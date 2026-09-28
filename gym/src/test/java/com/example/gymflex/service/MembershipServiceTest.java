package com.example.gymflex.service;

import com.example.gymflex.dto.MembershipRequest;
import com.example.gymflex.dto.RenewRequest;
import com.example.gymflex.model.Member;
import com.example.gymflex.model.Membership;
import com.example.gymflex.model.Plan;
import com.example.gymflex.repository.MemberRepository;
import com.example.gymflex.repository.MembershipRepository;
import com.example.gymflex.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private MembershipService membershipService;

    private Member member;
    private Plan plan;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "Madhan Kumar", "madhan@example.com", "9876543210", LocalDate.now());
        plan = new Plan(1L, "MONTHLY", 1, 1000.0);
    }

    @Test
    void testCreateMembership_Success() {
        MembershipRequest request = new MembershipRequest(1L, 1L, LocalDate.of(2026, 9, 1));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        Membership mockSaved = new Membership(1L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 10, 1), "ACTIVE", member, plan);
        when(membershipRepository.save(any(Membership.class))).thenReturn(mockSaved);

        Membership result = membershipService.createMembership(request);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(membershipRepository).save(any(Membership.class));
    }

    @Test
    void testRenewMembership_ExtendFromCurrentEndDate_Active() {
        LocalDate currentEnd = LocalDate.now().plusDays(10);
        Membership existing = new Membership(1L, LocalDate.now().minusDays(20), currentEnd, "ACTIVE", member, plan);

        when(membershipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArguments()[0]);

        Membership renewed = membershipService.renewMembership(1L, new RenewRequest(1L));

        assertNotNull(renewed);
        assertEquals(currentEnd, renewed.getStartDate());
        assertEquals(currentEnd.plusMonths(1), renewed.getEndDate());
        assertEquals("ACTIVE", renewed.getStatus());
    }

    @Test
    void testRenewMembership_StartFromToday_WhenExpired() {
        LocalDate expiredEnd = LocalDate.now().minusDays(10);
        Membership existing = new Membership(1L, LocalDate.now().minusDays(40), expiredEnd, "EXPIRED", member, plan);

        when(membershipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArguments()[0]);

        Membership renewed = membershipService.renewMembership(1L, new RenewRequest(1L));

        assertNotNull(renewed);
        assertEquals(LocalDate.now(), renewed.getStartDate());
        assertEquals(LocalDate.now().plusMonths(1), renewed.getEndDate());
        assertEquals("ACTIVE", renewed.getStatus());
    }
}
