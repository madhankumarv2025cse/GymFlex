package com.example.gymflex.dto;

public class MonthlyAttendanceResponse {

    private Long memberId;
    private String memberName;
    private String month;
    private int year;
    private long attendanceCount;

    public MonthlyAttendanceResponse() {
    }

    public MonthlyAttendanceResponse(Long memberId, String memberName, String month, int year, long attendanceCount) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.month = month;
        this.year = year;
        this.attendanceCount = attendanceCount;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(long attendanceCount) {
        this.attendanceCount = attendanceCount;
    }
}
