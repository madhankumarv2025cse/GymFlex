package com.example.gymflex.repository;

import com.example.gymflex.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByMemberId(Long memberId);
    List<Membership> findByMemberIdOrderByEndDateDesc(Long memberId);
    List<Membership> findByEndDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);
    List<Membership> findByStatus(String status);
    long countByStatus(String status);
}
