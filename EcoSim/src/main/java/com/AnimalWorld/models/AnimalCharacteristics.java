package com.AnimalWorld.models;

public record AnimalCharacteristics(
        String name,
        String symbol,
        double weightKg,
        int maxCountCell,
        int movementSpeed,
        double foodRequirementKg
) {}
