package com.example.gymflex.repository;

import com.example.gymflex.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findByMemberId(Long memberId);
    List<CheckIn> findByMemberIdOrderByCheckInDateDesc(Long memberId);
    long countByMemberIdAndCheckInDateBetween(Long memberId, LocalDate startDate, LocalDate endDate);
    long countByCheckInDateBetween(LocalDate startDate, LocalDate endDate);
}