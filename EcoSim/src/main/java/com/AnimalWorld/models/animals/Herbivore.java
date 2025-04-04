package com.AnimalWorld.models.animals;

import com.AnimalWorld.interfaces.IHerbivore;
import com.AnimalWorld.models.cells.Cell;
import com.AnimalWorld.models.Enums.AnimalTypes;
import com.AnimalWorld.services.eating.EatingService;

public class Herbivore extends Animal implements IHerbivore {
    private final EatingService eatingService;

    public Herbivore(AnimalTypes type) {
        super(type);
        this.eatingService = new EatingService();
    }

    @Override
    public void eat(Cell cell) {
        if (this.getIsAlive()) {
            eatingService.eatGrass(cell, this, 0);
        }
    }
}

