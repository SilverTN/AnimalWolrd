package com.AnimalWorld.interfaces;

import com.AnimalWorld.models.animals.Animal;
import com.AnimalWorld.services.movement.MovementService;
import com.AnimalWorld.models.cells.Cell;

public interface IMovementService {
    MovementService clone(Animal animal);

    void move(Cell currentCell);
}