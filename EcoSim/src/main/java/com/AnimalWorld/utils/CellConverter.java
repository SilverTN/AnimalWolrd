package com.AnimalWorld.utils;

import com.AnimalWorld.models.animals.Animal;
import com.AnimalWorld.models.cells.Cell;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CellConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertToJson(List<Cell> cells){
        CellConverter converter = new CellConverter();
        return converter.convertCellsToJson(cells);
    }

    private String convertCellsToJson(List<Cell> cells) {
        try {
            // Создаем структуру JSON
            Map<String, Object> jsonStructure = Map.of(
                    "cells", cells.stream()
                            .map(this::convertCellToMap)
                            .collect(Collectors.toList())
            );

            // Сериализуем данные в JSON
            return objectMapper.writeValueAsString(jsonStructure);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to serialize data\"}";
        }
    }

    private Map<String, Object> convertCellToMap(Cell cell) {
        if (cell.getAnimals().isEmpty()) {
            return Map.of(
                    "animals", List.of(),
                    "grassAmount", cell.getGrassAmount()
            );
        }

        // Группируем животных
        Map<String, AnimalGroup> groupedAnimals = cell.getAnimals().stream().
                filter(Animal::getIsAlive)
                .collect(Collectors.groupingBy(
                        animal -> animal.getType().getSymbol(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new AnimalGroup(list.get(0).getType().getName(), list.size())
                        )
                ));

        // Преобразуем группировку в список Map
        List<Map<String, ? extends Serializable>> animals = groupedAnimals.entrySet().stream()
                .map(entry -> Map.of(
                        "symbol", entry.getKey(),
                        "name", entry.getValue().getName(),
                        "count", entry.getValue().getCount()
                ))
                .collect(Collectors.toList());

        return Map.of(
                "animals", animals,
                "grassAmount", cell.getGrassAmount()
        );
    }

    // Вспомогательный класс для хранения данных о группе животных
    private static class AnimalGroup {
        private final String name;
        private final int count;

        public AnimalGroup(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }
    }
}
