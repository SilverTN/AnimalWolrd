package com.AnimalWorld.core;

import com.AnimalWorld.models.Enums.AnimalTypes;
import com.AnimalWorld.utils.config.AnimalConfigLoader;
import com.AnimalWorld.models.animals.Animal;
import com.AnimalWorld.models.animals.AnimalFactory;
import com.AnimalWorld.models.cells.Cell;
import com.AnimalWorld.interfaces.Observer;
import com.AnimalWorld.services.movement.MovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class GameEngine {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<Cell> cells; // Все ячейки мира
    private final List<Observer> observers = new ArrayList<>(); // Список подписчиков
    private final Object lock = new Object();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService executor;
    private final CyclicBarrier barrier;
    private final AtomicBoolean hasError = new AtomicBoolean(false);
    private final int COUNT_THREADS = 100;
    private AtomicInteger threadComplit = new AtomicInteger(0);


    public GameEngine(List<Cell> cells) {
        this.cells = cells;
        loadCharacteristicsAnimalMir();
        this.executor = Executors.newFixedThreadPool(Math.min(cells.size(), COUNT_THREADS));
        this.barrier = new CyclicBarrier(COUNT_THREADS);
    }

    // Добавления подписчика
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Уведомления всех подписчиков
    private void notifyObservers() {
        synchronized (lock) {
            for (Observer observer : observers) {
                observer.update(cells);
            }
        }
    }

    private void loadCharacteristicsAnimalMir() {
        AnimalConfigLoader.initAnimalCharacteristics();
        AnimalTypes.initCharacteristics(AnimalConfigLoader.getAnimals()); // Инициализируем характеристики для Enum AnimalTypes
        populateCells(); // Заполнение ячеек животными
    }

    private void populateCells() {
        for (AnimalTypes type : AnimalTypes.values()) {
            int maxPerCell = type.getCharacteristics().maxCountCell();
            // Распределяем животных по ВСЕМ ячейкам
            for (Cell cell : cells) {
                for (int i = 0; i < maxPerCell; i++) {
                    Animal animal = AnimalFactory.createAnimal(type);
                    animal.setMovementService(new MovementService(animal, cells)); // Модель и карта передвижения
                    cell.addAnimal(animal);
                }
            }
            // Перемешиваем животных в локациях
            for (Cell cell : cells) {
                cell.shuftAnimals();
            }
        }
    }

    // Запуска игрового цикла
    public void startGameLoop() {
        scheduler.scheduleWithFixedDelay(this::gameTick, 0, 200, TimeUnit.MILLISECONDS);
    }

    // Основной игровой цикл
    private void gameTick() {

        if (!areAnimalsAlive()) {
            logger.info("Все животные погибли!");
            endGame();
            return;
        }

        if (threadComplit.get() != 0) {
            return;
        }

        threadComplit.set(COUNT_THREADS);
        growGrass();
        notifyObservers();

        int batchSize = (int) Math.ceil((double) cells.size() / COUNT_THREADS); // Размер группы ячеек

        for (int i = 0; i < COUNT_THREADS; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, cells.size());

            List<Cell> cellBatch = cells.subList(start, end);

            executor.submit(() -> {
                try {
                    if (hasError.get()) {
                        return;
                    }
                    barrier.await(); // CyclicBarrier
                    for (Cell cell : cellBatch) {
                        for (Animal animal : cell.getAnimals()) {
                            animal.passDay();
                            animal.eat(cell);
                            animal.reproduce(cell);
                            animal.move(cell);
                            checkAndRemoveDeadAnimal(animal, cell);
                        }
                        notifyObservers(); // Уведомляем наблюдателей
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Ошибка при работе с барьером: {}", e.getMessage());
                } catch (Exception e) {
                    hasError.set(true);
                    logger.error("Критическая ошибка: {}", e.getMessage());
                } finally {
                    threadComplit.decrementAndGet();
                }
            });
        }

    }

    // Метод для остановки игрового цикла
    public void stopGameLoop() {
        logger.info("Остановка игрового цикла...");

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Принудительная остановка, если не завершился за 5 секунд
            }
        } catch (InterruptedException e) {
            executor.shutdownNow(); // Принудительная остановка при прерывании
            Thread.currentThread().interrupt(); // Восстанавливаем флаг прерывания
        }

        logger.info("Игровой цикл успешно остановлен.");
    }

    private void growGrass() {
        for (Cell cell : cells) {
            cell.growGrass();
        }
    }

    private boolean isAnimalLive(Animal animal) {
        if (!animal.getIsAlive())
            return false;
        if (animal.getCurrentHunger() >= 100)
            return false;
        if (animal.getCurrentWeightKg() <= 0)
            return false;

        return true;
    }

    private void checkAndRemoveDeadAnimal(Animal animal, Cell cell) {
        if (!isAnimalLive(animal)) {
            logger.info("Умерло животное в ячейке {} - {}", cell.getCellNumber(), animal.toString());
            cell.getAnimals().remove(animal);
        }
    }

    // Проверки наличия животных
    private boolean areAnimalsAlive() {
        for (Cell cell : cells) {
            if (!cell.getAnimals().isEmpty()) {
                return true; // Есть
            }
        }
        return false; // Животных нет
    }

    private void endGame() {
        notifyObservers();
        System.exit(0);
    }

}
