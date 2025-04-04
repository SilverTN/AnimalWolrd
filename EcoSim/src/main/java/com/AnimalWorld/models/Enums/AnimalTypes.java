package com.AnimalWorld.models.Enums;

import com.AnimalWorld.models.AnimalCharacteristics;

import java.util.Map;

public enum AnimalTypes {

    WOLF, SNAKE, FOX, BEAR, EAGLE, HORSE, DEER, RABBIT, MOUSE, GOAT, SHEEP, BOAR, BISON, DUCK, BUG;

    private AnimalCharacteristics characteristics;

    public void setCharacteristics(AnimalCharacteristics characteristics) {
        this.characteristics = characteristics;
    }

    public AnimalCharacteristics getCharacteristics() {
        return characteristics;
    }

    public String getName() {
        return characteristics.name();
    }

    public String getSymbol() {
        return characteristics.symbol();
    }

    public int getMaxCountCell() {
        return characteristics.maxCountCell();
    }

    public static AnimalTypes getByTypeName(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            throw new IllegalArgumentException("Имя типа не может быть пустым");
        }

        try {
            return AnimalTypes.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Тип животного с именем "+ typeName +" не найден." );
        }
    }


    // Инициализация характеристик
    public static void initCharacteristics(Map<String, AnimalCharacteristics> animals) {
        for (AnimalTypes type : AnimalTypes.values()) {
            AnimalCharacteristics characteristics = animals.get(type.name());
            if (characteristics != null) {
                type.setCharacteristics(characteristics);
            }
        }
    }
}

