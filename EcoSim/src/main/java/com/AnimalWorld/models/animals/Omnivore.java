package com.AnimalWorld.models.animals;

import com.AnimalWorld.interfaces.IOmnivore;
import com.AnimalWorld.models.cells.Cell;
import com.AnimalWorld.models.Enums.AnimalTypes;
import com.AnimalWorld.services.eating.predation.PredationLogic;
import com.AnimalWorld.services.eating.predation.PredationProbabilityService;
import com.AnimalWorld.services.eating.EatingService;

public class Omnivore extends Animal implements IOmnivore {
    private final PredationLogic predationLogic;
    private final EatingService eatingService;

    public Omnivore(AnimalTypes type) {
        super(type);
        PredationProbabilityService service = PredationProbabilityService.getInstance();
        this.predationLogic = new PredationLogic(service);
        this.eatingService = new EatingService();
    }

    @Override
    public void eat(Cell cell) {
        if (this.getIsAlive()) {
            double foodConsumed = eatingService.eatAnimals(this, cell, predationLogic);

            // Если хищно-травоядное животное не наелось, то пусть ест "салат" :)
            if (foodConsumed < this.getFoodRequirementKg()) {
                eatingService.eatGrass(cell, this, foodConsumed);
            }
        }
    }
}
