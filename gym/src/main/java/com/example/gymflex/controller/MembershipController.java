package com.example.gymflex.controller;

import com.example.gymflex.dto.MembershipRequest;
import com.example.gymflex.dto.RenewRequest;
import com.example.gymflex.model.Membership;
import com.example.gymflex.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping
    public ResponseEntity<Membership> createMembership(@Valid @RequestBody MembershipRequest request) {
        Membership membership = membershipService.createMembership(request);
        return new ResponseEntity<>(membership, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Membership>> getAllMemberships() {
        List<Membership> memberships = membershipService.getAllMemberships();
        return ResponseEntity.ok(memberships);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Membership> getMembershipById(@PathVariable Long id) {
        Membership membership = membershipService.getMembershipById(id);
        return ResponseEntity.ok(membership);
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<Membership> renewMembership(@PathVariable Long id, @RequestBody(required = false) RenewRequest request) {
        Membership membership = membershipService.renewMembership(id, request);
        return ResponseEntity.ok(membership);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Membership>> getMembershipsByMemberId(@PathVariable Long memberId) {
        List<Membership> memberships = membershipService.getMembershipsByMemberId(memberId);
        return ResponseEntity.ok(memberships);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Membership>> getExpiringMemberships() {
        List<Membership> memberships = membershipService.getExpiringMemberships();
        return ResponseEntity.ok(memberships);
    }
}
