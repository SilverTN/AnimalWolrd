package com.AnimalWorld.models.animals;

import com.AnimalWorld.interfaces.IPredator;
import com.AnimalWorld.models.cells.Cell;
import com.AnimalWorld.models.Enums.AnimalTypes;
import com.AnimalWorld.services.eating.predation.PredationLogic;
import com.AnimalWorld.services.eating.predation.PredationProbabilityService;
import com.AnimalWorld.services.eating.EatingService;


public class Predator extends Animal implements IPredator {
    private final PredationLogic predationLogic;
    private final EatingService eatingService;

    public Predator(AnimalTypes type) {
        super(type);
        PredationProbabilityService service = PredationProbabilityService.getInstance();
        this.predationLogic = new PredationLogic(service);
        this.eatingService = new EatingService();
    }

    @Override
    public void eat(Cell cell) {
        if (this.getIsAlive()) {
            eatingService.eatAnimals(this, cell, predationLogic);
        }
    }

}

