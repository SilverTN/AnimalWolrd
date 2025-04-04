package com.AnimalWorld.models.cells;

import com.AnimalWorld.models.animals.Animal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс ячейка, в которой хранятся животные
 */

public class Cell {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int cellNumber; // Номер ячейки
    private List<Animal> animals = new CopyOnWriteArrayList<>();
    private AtomicInteger grassAmount; // Количестов травы


    public Cell(int cellNumber, int grassAmount) {
        this.cellNumber = cellNumber;
        this.grassAmount = new AtomicInteger(grassAmount);
    }

    public int getCellNumber() {
        return cellNumber;
    }

    public void growGrass() {
        grassAmount.updateAndGet(current -> {
            if (current >= 50_000) {
                return current;
            }
            return current + ThreadLocalRandom.current().nextInt(1000, 10000);
        });
    }

    public int getGrassAmount() {
        return grassAmount.get();
    }

    public void reduceGrass(int amount) {
        grassAmount.updateAndGet(current -> Math.max(current - amount, 0));
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public void shuftAnimals() {
        Collections.shuffle(animals);
    }


    public boolean canAddAnimal(Animal animal) {
        if (animal == null) {
            logger.error("Животное не может быть null");
            throw new IllegalArgumentException("Животное не может быть null");
        }

        int maxCount = animal.getType().getCharacteristics().maxCountCell();

        // Получаем текущее количество животных данного типа в ячейке
        long currentCount = animals.stream()
                .filter(a -> a.getName().equals(animal.getName()))
                .count();

        return currentCount < maxCount;
    }

    public List<Animal> getAnimals() {
        return animals;
    }
}