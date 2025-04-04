public class Main {

    public static void main(String[] args) {
        final int xCoordinat = 2;
        final Race race = Race.GNOME;

        System.out.println("Всего рас = " + GameStatistics.countRace());
        System.out.println("Всего золота = " + GameStatistics.sumAllGold());
        System.out.printf("Количество объектов по координате x%d = %d%n", xCoordinat, GameStatistics.countObjects(xCoordinat));
        System.out.println("Третий по количеству золота = " + GameStatistics.hirdGold());
        System.out.printf("Общее количество золота по расе %s = %d%n", race,GameStatistics.allSumGoldRace(race));

        System.out.println("\n" + GameStatistics.listGame());

        new Thread(()-> System.out.println("ПОТОК")).start();
    }
}






