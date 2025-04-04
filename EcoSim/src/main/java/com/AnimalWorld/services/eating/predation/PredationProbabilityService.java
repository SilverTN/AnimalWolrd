package com.AnimalWorld.services.eating.predation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;


public class PredationProbabilityService {
    private static PredationProbabilityService instance;
    private final Map<String, Integer> probabilities;

    private PredationProbabilityService() throws IOException {
        this.probabilities = loadProbabilitiesFromYaml("PredationProbabilities.yaml");
    }

    private static void init() throws IOException {
        if (instance == null) {
            instance = new PredationProbabilityService();
        }
    }

    public static synchronized PredationProbabilityService getInstance()  {
        if (instance == null) {
            try {
                init();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (instance == null) {
                throw new IllegalStateException(PredationProbabilityService.class + " не инициализирован. Сначала вызовите init().");
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> loadProbabilitiesFromYaml(String yamlFilePath) throws IOException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        Map<String, Object> root;

        try (InputStream inputStream = new ClassPathResource(yamlFilePath).getInputStream()) {
            root = yamlMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Файл не найден: " + yamlFilePath, e);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка при десериализации YAML-файла", e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка ввода/вывода при загрузке конфигурации", e);
        }

        // Получаем блок с вероятностями (теперь он Map<String, Integer>)
        Map<String, Integer> result = (Map<String, Integer>) root.get("predationProbabilities");
        if (result == null) {
            throw new IOException("Файл YAML не содержит раздела 'predationProbabilities'");
        }
        // Создаем новую карту для thread-safe доступа
        return Map.copyOf(result);
    }

    /**
     * Возвращает вероятность хищничества для пары "хищник-жертва".
     * @param animalFirst Хищник (например, "WOLF").
     * @param animalNext Жертва (например, "RABBIT").
     * @return Вероятность в виде int (например, 60), или null, если пара не найдена.
     */
    public Integer getProbability(String animalFirst , String animalNext) {
        final String key = animalFirst + "_" + animalNext;
        return probabilities.get(key);
    }

    public Map<String, Integer> getAllProbabilities() {
        return probabilities;
    }
}