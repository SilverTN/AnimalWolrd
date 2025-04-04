package com.AnimalWorld.services.reproduction;

import com.AnimalWorld.models.animals.Animal;
import com.AnimalWorld.models.cells.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public class ReproductionService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public synchronized void reproduce(Animal animal, Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Ячейка не может быть null");
        }

        if (!isAnimalReadyToReproduce(animal))
            return;

        if (isPopulationLimitReached(cell, animal))
            return;

        Optional<Animal> partner = findReproductionPartner(cell, animal);

        if (partner.isPresent()) {
            // Обновляем время размножения для обоих животных
            synchronized (partner.get()) {
                if (!partner.get().updateReproductionTime()) {
                    return;
                }
                if (!animal.updateReproductionTime()) {
                    return;
                }
            }
            // Создаем новое животное
            cell.addAnimal(animal.giveBirth());

            logger.info("Размножение: {} добавлен в ячейку {}", animal.getName(), cell.getCellNumber());
        } else {
            logger.info("Размножение невозможно: нет пары для {} в ячейке {}", animal.getName(), cell.getCellNumber());
        }
    }

    // Проверяем, готово ли животное к размножению
    private boolean isAnimalReadyToReproduce(Animal animal) {
        if (!animal.getIsAlive() || !animal.canReproduce()) {
            logger.info("Животное {} не готово к размножению", animal.getName());
            return false;
        }
        return true;
    }

    // Проверяем лимит популяции
    private boolean isPopulationLimitReached(Cell cell, Animal animal) {
        long currentPopulation = cell.getAnimals().stream()
                .filter(a -> a.getType() == animal.getType())
                .count();
        if (currentPopulation >= animal.getType().getMaxCountCell()) {
            logger.info("Размножение невозможно: достигнут лимит популяции в ячейке {} для {}", cell.getCellNumber(), animal.getName());
            return true;
        }
        return false;
    }

    private Optional<Animal> findReproductionPartner(Cell cell, Animal animal) {
        return cell.getAnimals().stream()
                .filter(a -> a.getType() == animal.getType())
                .filter(Animal::canReproduce) // Проверяем, готово ли оно к размножению
                .findFirst();
    }
}
