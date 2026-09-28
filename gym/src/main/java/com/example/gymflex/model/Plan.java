package com.example.gymflex.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plan name must not be blank")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Duration in months is required")
    @Positive(message = "Duration in months must be positive")
    @Column(nullable = false)
    private Integer durationInMonths;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    public Plan() {
    }

    public Plan(String name, Integer durationInMonths, Double price) {
        this.name = name;
        this.durationInMonths = durationInMonths;
        this.price = price;
    }

    public Plan(Long id, String name, Integer durationInMonths, Double price) {
        this.id = id;
        this.name = name;
        this.durationInMonths = durationInMonths;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Integer durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
