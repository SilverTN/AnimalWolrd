package com.AnimalWorld.core;

import com.AnimalWorld.controllers.SSEController;
import com.AnimalWorld.models.cells.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Main implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SSEController sseController;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    @Autowired
    public Main(SSEController sseController) {
        this.sseController = sseController;
    }

    @Override
    public void run(String... args) throws Exception {

        /* Визуальное представление локаций (клеток)
         0	1	2	3	4	5	6	7	8	9
         10	11	12	13	14	15	16	17	18	19
         20	21	22	23	24	25	26	27	28	29
         30	31	32	33	34	35	36	37	38	39
         40	41	42	43	44	45	46	47	48	49
         50	51	52	53	54	55	56	57	58	59
         60	61	62	63	64	65	66	67	68	69
         70	71	72	73	74	75	76	77	78	79
         80	81	82	83	84	85	86	87	88	89
         90	91	92	93	94	95	96	97	98	99
        */

        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            cells.add(new Cell(i,5000)); // Каждая ячейка с начальным количеством травы = 5000
        }
        // Создаем игровой мир
        GameEngine gameEngine = new GameEngine(cells);

        // Подписываем SSEController на обновления
        gameEngine.addObserver(sseController);

        // Запускаем игровой цикл в отдельном потоке
        executor.submit(() -> {
            try {
                logger.info("Запуск игрового цикла...");
                gameEngine.startGameLoop();
            } catch (Exception e) {
                logger.error("Ошибка в игровом цикле", e);
            }
        });

        // Для остановки игры
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                gameEngine.stopGameLoop();
                sseController.closeAllConnections();
            } catch (Exception e) {
                logger.error("Ошибка при остановке игрового цикла", e);
            } finally {
                executor.shutdown();
            }
        }));

    }
}
