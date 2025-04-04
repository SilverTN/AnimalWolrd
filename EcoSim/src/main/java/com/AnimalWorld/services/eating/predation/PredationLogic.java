package com.AnimalWorld.services.eating.predation;

import java.util.concurrent.ThreadLocalRandom;

public class PredationLogic {
    private final PredationProbabilityService probabilityService;

    public PredationLogic(PredationProbabilityService probabilityService) {
        this.probabilityService = probabilityService;
    }

    /**
     * Проверяет, съест ли хищник жертву
     * @param predatorType тип хищника (например, "WOLF")
     * @param preyType тип жертвы (например, "RABBIT")
     * @return true если хищник съедает жертву
     */
    public boolean checkPredation(String predatorType, String preyType) {
        Integer probability = probabilityService.getProbability(predatorType, preyType);

        // Если вероятность не указана или 0 - съедание невозможно
        if ((probability == null) || (probability == 0)) {
            return false;
        }
        // Вероятность съедания
        int randomValue = ThreadLocalRandom.current().nextInt(100);

        // Сравниваем с вероятностью
        return randomValue < probability;
    }
}