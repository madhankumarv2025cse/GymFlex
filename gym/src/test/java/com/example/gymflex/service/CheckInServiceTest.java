package com.example.gymflex.service;

import com.example.gymflex.dto.MonthlyAttendanceResponse;
import com.example.gymflex.exception.BusinessException;
import com.example.gymflex.model.CheckIn;
import com.example.gymflex.model.Member;
import com.example.gymflex.model.Membership;
import com.example.gymflex.model.Plan;
import com.example.gymflex.repository.CheckInRepository;
import com.example.gymflex.repository.MemberRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MembershipService membershipService;

    @InjectMocks
    private CheckInService checkInService;

    private Member member;
    private Plan plan;
    private Membership activeMembership;
    private Membership expiredMembership;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "Madhan Kumar", "madhan@example.com", "9876543210", LocalDate.now());
        plan = new Plan(1L, "MONTHLY", 1, 1000.0);
        activeMembership = new Membership(1L, LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), "ACTIVE", member, plan);
        expiredMembership = new Membership(2L, LocalDate.now().minusDays(40), LocalDate.now().minusDays(10), "EXPIRED", member, plan);
    }

    @Test
    void testCheckInMember_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(membershipService.getActiveMembership(1L)).thenReturn(Optional.of(activeMembership));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(i -> i.getArguments()[0]);

        CheckIn checkIn = checkInService.checkInMember(1L);

        assertNotNull(checkIn);
        assertEquals(member, checkIn.getMember());
        assertEquals(LocalDate.now(), checkIn.getCheckInDate());
    }

    @Test
    void testCheckInMember_ExpiredMembership_ThrowsBusinessException() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(membershipService.getActiveMembership(1L)).thenReturn(Optional.of(expiredMembership));

        BusinessException ex = assertThrows(BusinessException.class, () -> checkInService.checkInMember(1L));
        assertEquals("Check-in rejected. Membership has expired.", ex.getMessage());
    }

    @Test
    void testGetMonthlyAttendanceCount() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        when(checkInRepository.countByMemberIdAndCheckInDateBetween(eq(1L), eq(startOfMonth), eq(endOfMonth))).thenReturn(12L);

        MonthlyAttendanceResponse response = checkInService.getMonthlyAttendanceCount(1L);

        assertNotNull(response);
        assertEquals(1L, response.getMemberId());
        assertEquals("Madhan Kumar", response.getMemberName());
        assertEquals(12L, response.getAttendanceCount());
    }
}
