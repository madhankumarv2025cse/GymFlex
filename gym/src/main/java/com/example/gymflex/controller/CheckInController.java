package com.example.gymflex.controller;

import com.example.gymflex.dto.MonthlyAttendanceResponse;
import com.example.gymflex.model.CheckIn;
import com.example.gymflex.service.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/member/{memberId}")
    public ResponseEntity<CheckIn> checkInMember(@PathVariable Long memberId) {
        CheckIn checkIn = checkInService.checkInMember(memberId);
        return new ResponseEntity<>(checkIn, HttpStatus.CREATED);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<CheckIn>> getMemberCheckIns(@PathVariable Long memberId) {
        List<CheckIn> checkIns = checkInService.getMemberCheckIns(memberId);
        return ResponseEntity.ok(checkIns);
    }

    @GetMapping("/member/{memberId}/monthly-count")
    public ResponseEntity<MonthlyAttendanceResponse> getMonthlyAttendanceCount(@PathVariable Long memberId) {
        MonthlyAttendanceResponse response = checkInService.getMonthlyAttendanceCount(memberId);
        return ResponseEntity.ok(response);
    }
}
