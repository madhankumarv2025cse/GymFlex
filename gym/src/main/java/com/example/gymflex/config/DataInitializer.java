package com.example.gymflex.config;

import com.example.gymflex.model.Plan;
import com.example.gymflex.repository.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;

    public DataInitializer(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (planRepository.count() == 0) {
            planRepository.save(new Plan("MONTHLY", 1, 1000.0));
            planRepository.save(new Plan("QUARTERLY", 3, 2500.0));
            planRepository.save(new Plan("YEARLY", 12, 9000.0));
            System.out.println("Default plans initialized: MONTHLY, QUARTERLY, YEARLY.");
        }
    }
}
