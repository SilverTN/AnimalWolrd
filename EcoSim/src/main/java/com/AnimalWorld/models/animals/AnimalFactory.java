package com.AnimalWorld.models.animals;
import com.AnimalWorld.models.Enums.AnimalTypes;


public class AnimalFactory {
    public static Animal createAnimal(AnimalTypes animalTypes) {
        switch (animalTypes) {
            case BISON:
            case HORSE:
            case DEER:
            case SHEEP:
            case GOAT:
            case RABBIT:
            case BUG:
                return new Herbivore(animalTypes);
            case BEAR:
            case WOLF:
            case SNAKE:
            case FOX:
            case EAGLE:
                return new Predator(animalTypes);
            case BOAR:
            case DUCK:
            case MOUSE:
                return new Omnivore(animalTypes);
            default:
                throw new IllegalArgumentException("Неизвестный тип животного: " + animalTypes);
        }
    }
}
