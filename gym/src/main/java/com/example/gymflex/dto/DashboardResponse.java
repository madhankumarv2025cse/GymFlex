package com.example.gymflex.dto;

public class DashboardResponse {

    private long totalMembers;
    private long activeMemberships;
    private long expiredMemberships;
    private long expiringWithin7Days;
    private long totalCheckInsThisMonth;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalMembers, long activeMemberships, long expiredMemberships, long expiringWithin7Days, long totalCheckInsThisMonth) {
        this.totalMembers = totalMembers;
        this.activeMemberships = activeMemberships;
        this.expiredMemberships = expiredMemberships;
        this.expiringWithin7Days = expiringWithin7Days;
        this.totalCheckInsThisMonth = totalCheckInsThisMonth;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getActiveMemberships() {
        return activeMemberships;
    }

    public void setActiveMemberships(long activeMemberships) {
        this.activeMemberships = activeMemberships;
    }

    public long getExpiredMemberships() {
        return expiredMemberships;
    }

    public void setExpiredMemberships(long expiredMemberships) {
        this.expiredMemberships = expiredMemberships;
    }

    public long getExpiringWithin7Days() {
        return expiringWithin7Days;
    }

    public void setExpiringWithin7Days(long expiringWithin7Days) {
        this.expiringWithin7Days = expiringWithin7Days;
    }

    public long getTotalCheckInsThisMonth() {
        return totalCheckInsThisMonth;
    }

    public void setTotalCheckInsThisMonth(long totalCheckInsThisMonth) {
        this.totalCheckInsThisMonth = totalCheckInsThisMonth;
    }
}
