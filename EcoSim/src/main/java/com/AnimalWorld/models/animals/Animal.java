package com.AnimalWorld.models.animals;

import com.AnimalWorld.models.AnimalStateManager;
import com.AnimalWorld.models.cells.Cell;
import com.AnimalWorld.models.Enums.AnimalTypes;
import com.AnimalWorld.services.movement.MovementService;
import com.AnimalWorld.services.reproduction.ReproductionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class Animal extends AbstractBaseAnimal {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AnimalStateManager stateManager; // Управления состоянием животного
    private MovementService movementService; // Управление передвижением животного
    private final ReproductionService reproductionService; // Управление размножением животного

    public Animal(AnimalTypes type) {
        super(type);
        this.stateManager = new AnimalStateManager(
                type.getCharacteristics().weightKg(),
                type.getCharacteristics().movementSpeed());
        this.reproductionService = new ReproductionService();
    }

    public MovementService getMovementService() {
        return movementService;
    }

    /**
     * Геттеры для базовых характеристик
     */
    public double getBaseWeightKg() {
        return getType().getCharacteristics().weightKg();
    }

    public int getBaseMovementSpeed() {
        return getType().getCharacteristics().movementSpeed();
    }

    public void passDay() {
        stateManager.updateState(getType().getCharacteristics().foodRequirementKg());
        if (this.getCurrentHunger() >= 100) {
            this.kill();
        }
    }

    public boolean isHungry() {
        return stateManager.isHungry(getType().getCharacteristics().foodRequirementKg());
    }

    public double getCurrentHunger() {
        return stateManager.getCurrentHunger();
    }

    public double getCurrentWeightKg() {
        return stateManager.getCurrentWeightKg();
    }

    public int getCurrentMovementSpeed() {
        return stateManager.getCurrentMovementSpeed();
    }

    public void eat(double foodAmount) {
        stateManager.eat(foodAmount);
    }

    public void setMovementService(MovementService movementService) {
        this.movementService = movementService;
    }

    @Override
    public void move(Cell currentCell) {
        if (movementService != null) {
            movementService.move(currentCell);
        } else {
            logger.warn("Не двигается. Не задана модель передвижения");
        }
    }

    public void reproduce(Cell cell) {
        reproductionService.reproduce(this, cell);
    }

    public Animal giveBirth() {
        Animal newAnimal = AnimalFactory.createAnimal(this.getType());
        newAnimal.setMovementService(this.getMovementService().clone(newAnimal));
        return newAnimal;
    }

    public void sleepAnimal(long sleep) {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(sleep); // Приостановка на N миллисекунды
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).join();
    }

    @Override
    public String toString() {
        return "Animal {" +
                "id='" + this.getId() + '\'' +
                ", тип=" + this.getType().getName() +
                ", symbol='" + this.getType().getSymbol() + '\'' +
                ", вес=" + stateManager.getCurrentWeightKg() +
                ", голод=" + stateManager.getCurrentHunger() +
                '}';
    }
}






