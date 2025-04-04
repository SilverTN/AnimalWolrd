package com.AnimalWorld.interfaces;

import com.AnimalWorld.models.cells.Cell;

import java.util.List;

public interface Observer {
    void update(List<Cell> cells);
}