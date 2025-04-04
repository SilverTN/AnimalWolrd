package com.AnimalWorld.services.eating;

import com.AnimalWorld.models.animals.Animal;
import com.AnimalWorld.services.eating.predation.PredationLogic;
import com.AnimalWorld.models.cells.Cell;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EatingService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Логика поедания животных.
     *
     * @param predator       Хищник, который ест.
     * @param cell           Ячейка, где происходит поедание.
     * @param predationLogic Логика проверки вероятности поедания.
     * @return Количество съеденной пищи (в кг).
     */
    public synchronized double eatAnimals(Animal predator, Cell cell, PredationLogic predationLogic) {
        if (cell == null) {
            throw new IllegalArgumentException("Ячейка не может быть null");
        }

        List<Animal> animals = cell.getAnimals();
        double foodRequirementKg = predator.getFoodRequirementKg(); // Потребность в пище
        double foodConsumed = 0; // Сколько еды уже съедено

        for (Animal animal : animals) {

            boolean isEatAnimal = predationLogic.checkPredation(predator.getType().name(), animal.getType().name());
            if (!isEatAnimal) {
                continue;
            }

            double animalWeight = animal.getCurrentWeightKg(); // Вес жертвы
            double foodToEat = Math.min(animalWeight, foodRequirementKg - foodConsumed);

            if (animal.kill()) { // Попытка съесть животное
                logger.info("{} {} съел {} {}", predator.getName(), predator.getId(), animal.getName(), animal.getId());
                foodConsumed += foodToEat;
            }

            if (foodConsumed >= foodRequirementKg) {
                logger.info("{} {} наелся", predator.getName(), predator.getId());
                break;
            }
        }

        predator.eat(foodConsumed);

        if (foodConsumed > 0) {
            logger.info("{} {} съел {} кг животных. В ячейке {}", predator.getName(), predator.getId(), foodConsumed, cell.getCellNumber());
        } else {
            logger.info("В ячейке {} нет животных для {}", cell.getCellNumber(), predator.getName());
        }

        return foodConsumed;
    }

    /**
     * Логика поедания травы.
     *
     * @param cell         Ячейка, где происходит поедание.
     * @param animal       Животное, которое ест траву.
     * @param foodConsumed Количество уже съеденной пищи.
     */
    public void eatGrass(Cell cell, Animal animal, double foodConsumed) {
        if (cell == null) {
            throw new IllegalArgumentException("Ячейка не может быть null");
        }

        double remainingFoodRequirement = animal.getFoodRequirementKg() - foodConsumed;
        int currentGrass = cell.getGrassAmount();
        if (currentGrass > 0) {
            int grassToEat = (int) Math.min(currentGrass, remainingFoodRequirement);
            cell.reduceGrass(grassToEat);
            animal.eat(grassToEat);
            logger.info("{} {} съел {} единиц травы. В ячейке {}", animal.getName(), animal.getId(), grassToEat, cell.getCellNumber());
        } else {
            logger.info("В ячейке {} нет травы для {} {}", cell.getCellNumber(), animal.getName(), animal.getId());
        }
    }
}
