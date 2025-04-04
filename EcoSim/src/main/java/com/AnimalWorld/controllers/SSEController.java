package com.AnimalWorld.controllers;

import com.AnimalWorld.models.cells.Cell;
import com.AnimalWorld.utils.CellConverter;
import com.AnimalWorld.interfaces.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Component
@RestController
public class SSEController implements Observer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicReference<Instant> lastUpdateTimestamp = new AtomicReference<>(Instant.MIN); // Время последней отправки
    private static final Duration MIN_UPDATE_INTERVAL = Duration.ofMillis(100); // Минимальный интервал между отправками


    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
    private volatile List<Cell> cells = new CopyOnWriteArrayList<>();

    // SSE-эндпоинт
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents() {
        return sink.asFlux()
                .doOnSubscribe(sub -> logger.info("Клиент подключился"))
                .doOnCancel(() -> logger.info("Клиент отключился"));
    }

    @Override
    public void update(List<Cell> cells) {
        Instant now = Instant.now();
        // Проверяем, прошло ли достаточно времени с момента последней отправки
        if (Duration.between(lastUpdateTimestamp.get(), now).compareTo(MIN_UPDATE_INTERVAL) < 0) {
           return;
        }
        this.cells = new CopyOnWriteArrayList<>(cells);
        String jsonData = CellConverter.convertToJson(cells);
        sink.tryEmitNext(jsonData); // Отправляем данные только при изменениях
        this.lastUpdateTimestamp.set(now); // Обновляем время последней отправки
        logger.info("Отправлены данные через SSE: {}", jsonData);
    }

    public void closeAllConnections() {
        logger.info("Закрытие всех активных SSE-соединений...");
        sink.tryEmitComplete(); // Завершаем поток данных для всех подписчиков
        logger.info("Все SSE-соединения закрыты.");
    }

}


