package com.example.gymflex.service;

import com.example.gymflex.exception.BusinessException;
import com.example.gymflex.model.Member;
import com.example.gymflex.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member sampleMember;

    @BeforeEach
    void setUp() {
        sampleMember = new Member(1L, "Madhan Kumar", "madhan@example.com", "9876543210", LocalDate.now());
    }

    @Test
    void testCreateMember_Success() {
        when(memberRepository.existsByEmail(sampleMember.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(sampleMember);

        Member created = memberService.createMember(sampleMember);

        assertNotNull(created);
        assertEquals("Madhan Kumar", created.getName());
        verify(memberRepository).save(sampleMember);
    }

    @Test
    void testCreateMember_DuplicateEmail_ThrowsException() {
        when(memberRepository.existsByEmail(sampleMember.getEmail())).thenReturn(true);

        assertThrows(BusinessException.class, () -> memberService.createMember(sampleMember));
    }
}
