package View;

import java.util.Scanner;

public class ConsoleView {
    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        /* отображение главного меню */
        System.out.println("===== CryptoAnalizer - Шифр Цезаря =====");
        System.out.println("1. Путь к файлу что бы зашифровать текст");
        System.out.println("2. Путь к файлу что бы расшифровать текст");
        System.out.println("3. Выход");
        System.out.print("Выберите действие (1-3): ");
    }

    public String getFilePathInput() {
        System.out.print("Введите путь к файлу: ");
        return scanner.nextLine(); // Возвращаем введенный путь к файлу
    }

    public String getTextInput() {
        /* запрос текста или параметров у пользователя. */
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    public void displayResult(String result) {
        /* вывод результата на экран */
        System.out.println("Результат:");
        System.out.println(result);
    }

    public void displayError(String message) {
        /* вывод ошибок */
        System.err.println("Ошибка: " + message);
    }
}
