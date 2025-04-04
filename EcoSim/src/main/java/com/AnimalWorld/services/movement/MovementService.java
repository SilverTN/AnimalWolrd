package com.AnimalWorld.services.movement;

import com.AnimalWorld.models.animals.Animal;
import com.AnimalWorld.interfaces.IMovementService;
import com.AnimalWorld.models.cells.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MovementService implements IMovementService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Animal animal;
    private final List<Cell> cells;

    public MovementService(Animal animal, List<Cell> cells) {
        this.animal = animal;
        this.cells = cells;
    }

    @Override
    public MovementService clone(Animal newAnimal) {
        try {
            return new MovementService(newAnimal, cells);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при клонировании Service", e);
        }
    }

    @Override
    public void move(Cell currentCell) {
        // Проверяем, живо ли животное
        if (!animal.getIsAlive()) {
            logger.info("Животное {} {} мертво и не может двигаться.", animal.getName(), animal.getId());
            return;
        }

        synchronized (animal) {
            int movementSpeed = animal.getCurrentMovementSpeed();

            if (movementSpeed > 0) {
                Cell targetCell = findTargetCell(currentCell, movementSpeed);
                if (targetCell != null && targetCell.canAddAnimal(animal)) {
                    currentCell.removeAnimal(animal);
                    targetCell.addAnimal(animal);
                    logger.info("{} {} переместилось в ячейку {}", animal.getName(), animal.getId(), targetCell.getCellNumber());
                } else
                    logger.info("{} {} НЕ переместилось в ячейку {}", animal.getName(), animal.getId(), targetCell.getCellNumber());
            }
        }
    }

    // Поиск целевой ячейки для перемещения
    private Cell findTargetCell(Cell currentCell, int movementSpeed) {
        int currentIndex = cells.indexOf(currentCell);
        if (currentIndex == -1) {
            throw new IllegalArgumentException("Текущая ячейка не найдена в списке.");
        }

        // Определяем случайное направление (вниз, вверх, влево, вправо)
        String direction = getRandomDirection();

        // Вычисляем целевой индекс
        int targetIndex = currentIndex;
        switch (direction) {
            case "DOWN":
                targetIndex = moveDown(currentIndex, movementSpeed);
                break;
            case "UP":
                targetIndex = moveUp(currentIndex, movementSpeed);
                break;
            case "LEFT":
                targetIndex = moveLeft(currentIndex, movementSpeed);
                break;
            case "RIGHT":
                targetIndex = moveRight(currentIndex, movementSpeed);
                break;
        }

        return cells.get(targetIndex);
    }

    // Метод для получения случайного направления
    private String getRandomDirection() {
        String[] directions = {"DOWN", "UP", "LEFT", "RIGHT"};
        int randomIndex = ThreadLocalRandom.current().nextInt(directions.length);
        return directions[randomIndex];
    }

    // Методы для вычисления нового индекса при движении
    private int moveDown(int currentIndex, int speed) {
        int column = currentIndex % 10;
        int newIndex = currentIndex + 10 * speed;
        int maxIndexInColumn = 90 + column;
        if (newIndex > maxIndexInColumn) {
            newIndex = maxIndexInColumn;
        }
        return newIndex;
    }

    private int moveUp(int currentIndex, int speed) {
        int column = currentIndex % 10;
        int newIndex = currentIndex - 10 * speed;
        int minIndexInColumn = column;
        if (newIndex < minIndexInColumn) {
            newIndex = minIndexInColumn;
        }
        return newIndex;
    }

    private int moveLeft(int currentIndex, int speed) {
        int row = currentIndex / 10;
        int newIndex = currentIndex - speed;
        int minIndexInRow = row * 10;
        if (newIndex < minIndexInRow) {
            newIndex = minIndexInRow;
        }
        return newIndex;
    }

    private int moveRight(int currentIndex, int speed) {
        int row = currentIndex / 10;
        int newIndex = currentIndex + speed;
        int maxIndexInRow = row * 10 + 9;
        if (newIndex > maxIndexInRow) {
            newIndex = maxIndexInRow;
        }
        return newIndex;
    }
}