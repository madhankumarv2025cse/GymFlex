package com.example.gymflex.service;

import com.example.gymflex.exception.BusinessException;
import com.example.gymflex.exception.ResourceNotFoundException;
import com.example.gymflex.model.Plan;
import com.example.gymflex.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public Plan createPlan(Plan plan) {
        if (planRepository.existsByName(plan.getName())) {
            throw new BusinessException("Plan with name " + plan.getName() + " already exists.");
        }
        return planRepository.save(plan);
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public Plan getPlanById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
    }

    public Plan updatePlan(Long id, Plan planDetails) {
        Plan existingPlan = getPlanById(id);

        existingPlan.setName(planDetails.getName());
        existingPlan.setDurationInMonths(planDetails.getDurationInMonths());
        existingPlan.setPrice(planDetails.getPrice());

        return planRepository.save(existingPlan);
    }

    public void deletePlan(Long id) {
        Plan plan = getPlanById(id);
        planRepository.delete(plan);
    }
}
