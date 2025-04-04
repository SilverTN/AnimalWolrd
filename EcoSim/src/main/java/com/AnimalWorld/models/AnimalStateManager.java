package com.AnimalWorld.models;

import java.util.concurrent.ThreadLocalRandom;

public class AnimalStateManager {
    /** Текущий уровень голода (0 = сытый, 100 = голодный) */
    private double currentHunger = 0;
    /** Текущий вес существа в кг */
    private double currentWeightKg;
    /** Текущая скорость передвижения */
    private int currentMovementSpeed;

    public AnimalStateManager(double baseWeight, int baseMovementSpeed) {
        this.currentWeightKg = baseWeight;
        this.currentMovementSpeed = baseMovementSpeed;
    }

    public void updateState(double foodRequirementKg) {
        currentHunger += ThreadLocalRandom.current().nextInt(1, 5);

        if (isHungry(foodRequirementKg)) {
            currentMovementSpeed = Math.max(1, currentMovementSpeed - 1); // Ограничение скорости при голоде
        } else {
            currentMovementSpeed = Math.max(1, currentMovementSpeed);     // Восстановление базовой скорости
        }

        if (currentHunger > 0) {
            currentWeightKg = Math.round(Math.max(0.1, currentWeightKg * 0.95) * 100.0) / 100.0; // Уменьшаем вес животного на 5%
        }

    }

    public boolean isHungry(double foodRequirementKg) {
        return currentHunger >= foodRequirementKg * 0.8;
    }

    public void eat(double foodAmount) {
        currentHunger = Math.max(0, currentHunger - foodAmount);
        currentWeightKg = Math.max(currentWeightKg, currentWeightKg + foodAmount / 2); // Восстановление веса
    }

    public double getCurrentHunger() {
        return currentHunger;
    }

    public double getCurrentWeightKg() {
        return currentWeightKg;
    }

    public int getCurrentMovementSpeed() {
        return Math.max(1, currentMovementSpeed);
    }
}