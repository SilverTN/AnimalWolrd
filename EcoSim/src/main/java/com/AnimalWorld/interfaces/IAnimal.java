package com.AnimalWorld.interfaces;

import com.AnimalWorld.models.cells.Cell;

public interface IAnimal {
    void move(Cell currentCell);
    void eat(Cell cell);
}
