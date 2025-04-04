// + найти множество всех рас
// + посчитать общее количество золота на карте
// + найти количество объектов по координате x = 2
// + найти третьего по количеству золота на карте
// + посчитать общее количество золота по расе
// - найти второго по количеству золота на каждой клетке

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameStatistics {

    /** Найти количество всех рас */
    public static long countRace() {

        return GameWorld.game.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .map(GameObject::getRace)
                .distinct()
                .count();
    }


    /** Посчитать общее количество золота на карте  */
    public static int sumAllGold() {
        Optional<Integer> sum = Optional.of(GameWorld.game.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .map(GameObject::getGold)
                .reduce(Integer::sum)
                .get());

        return sum.orElse(0);
    }

    /** Находит количество объектов по координате x
     * @param x Координата x, для которой нужно найти количество объектов.
     * @return Количество объектов на указанной координате x
     */
    public static long countObjects(int x) {
        Optional<Long> count = Optional.of(GameWorld.game.entrySet().stream()
                .filter(entry -> entry.getKey().getX() == x) // Фильтруем по координате X
                .mapToLong(map -> map.getValue().size())
                .sum()); // Подсчёт количества объектов

        return count.orElse(0L);
    }


    /** Список всех объектов */
    public static List<GameObject> listGame() {
        return GameWorld.game.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .sorted((o1, o2) -> o1.getGold() - o2.getGold())
                .collect(Collectors.toList());
    }

    /** Найти третьего по количеству золота на карте */
    public static int hirdGold() {

        Optional<Integer> sum = Optional.of(GameWorld.game.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .map(GameObject::getGold)
                .sorted(((o1, o2) -> o2 - o1))
                .skip(2).findFirst()
                .get());

        return sum.orElse(0);
    }

    /** Посчитать общее количество золота по расе
     * @param race раса для которой требуется посчитать количество золота.
     */
    public static int allSumGoldRace(Race race) {
        Optional<Integer> sum = Optional.of(GameWorld.game.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(o -> o.getRace() == race)
                .map(GameObject::getGold)
                .reduce(Integer::sum)
                .get());

        return sum.orElse(0);
    }


}
