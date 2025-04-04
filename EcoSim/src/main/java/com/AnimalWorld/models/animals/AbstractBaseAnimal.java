package com.AnimalWorld.models.animals;

import com.AnimalWorld.interfaces.IAnimal;
import com.AnimalWorld.models.cells.Cell;
import com.AnimalWorld.models.Enums.AnimalTypes;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractBaseAnimal implements IAnimal {

    private final String id = UUID.randomUUID().toString();
    private final AnimalTypes type;
    private final long REPRODUCTION_INTERVAL_SECOND = 3; // Минимальный интервал между размножениями
    private final AtomicReference<Instant> lastReproductionTime = new AtomicReference<>(Instant.MIN);// Время последнего размножения
    protected final AtomicBoolean isAlive = new AtomicBoolean(true);

    public AbstractBaseAnimal(AnimalTypes type) {
        this.type = type;
        this.lastReproductionTime.set(Instant.now());
    }

    public String getName() {
        return type.getName();
    }

    public boolean getIsAlive() {
        return isAlive.get();
    }

    public String getId() {
        return id;
    }

    public AnimalTypes getType() {
        return type;
    }

    public double getFoodRequirementKg() {
        return getType().getCharacteristics().foodRequirementKg();
    }

    public boolean kill() {
        return isAlive.getAndSet(false);
    }

    public boolean canReproduce() {
        Instant now = Instant.now();
        return now.isAfter(lastReproductionTime.get().plusSeconds(REPRODUCTION_INTERVAL_SECOND));
    }

    public boolean updateReproductionTime() {
        Instant lastTime = lastReproductionTime.get();
        if (canReproduce()) {
            return lastReproductionTime.compareAndSet(lastTime, Instant.now());
        }
        return false;
    }

    public abstract void eat(Cell cell);

    public abstract void move(Cell currentCell);

    public abstract void reproduce(Cell cell);
}
