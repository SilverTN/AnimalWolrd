package com.AnimalWorld.utils.config;

import com.AnimalWorld.models.AnimalCharacteristics;
import com.AnimalWorld.models.Enums.AnimalTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.io.InputStream;
import java.util.stream.Collectors;

public class AnimalConfigLoader {
    private static final String FILE_PATH = "AnimalCharacteristics.yaml";
    private static Map<String, AnimalCharacteristics> animals;

    public static void initAnimalCharacteristics() {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream inputStream = AnimalConfigLoader.class.getClassLoader().getResourceAsStream(FILE_PATH)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Файл не найден: " + FILE_PATH);
            }
            Map<String, Object> yamlData = mapper.readValue(inputStream, new TypeReference<>() {});
            animals = ((Map<String, Map<String, Object>>) yamlData.get("animalCharacteristics"))
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> createCharacteristics(entry.getValue())
                    ));
            AnimalTypes.initCharacteristics(animals);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Файл не найден: " + FILE_PATH, e);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка при десериализации YAML-файла", e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка ввода/вывода при загрузке конфигурации", e);
        }
    }

    private static AnimalCharacteristics createCharacteristics(Map<String, Object> data) {
        return new AnimalCharacteristics(
                (String) data.get("name"),
                (String) data.get("symbol"),
                convertToDouble(data.get("weightKg")),
                ((Number) data.get("maxCountCell")).intValue(),
                ((Number) data.get("movementSpeed")).intValue(),
                convertToDouble(data.get("foodRequirementKg"))
        );
    }

    private static double convertToDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        return (Double) value;
    }

    public static Map<String, AnimalCharacteristics> getAnimals() {
        return animals;
    }
}