package com.example.gymflex.dto;

public class RenewRequest {

    private Long planId;

    public RenewRequest() {
    }

    public RenewRequest(Long planId) {
        this.planId = planId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
