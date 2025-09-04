package com.example.IO.console;

import com.example.IO.interfaces.InputManager;

import java.util.Scanner;

/**
 * Класс для чтения из консоли.
 * Использует Scanner.
 */

public class ConsoleInputManager implements InputManager {
    private Scanner scanner;

    public ConsoleInputManager() {
        resetScanner();
    }

    /**
     * Читает строку из консоли с помощью сканера.
     * @return прочитанная строка
     */
    @Override
    public String readLine() {
        try {
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            } else {
                return null;
                }
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Пересоздает сканер на ввод из консоли.
     */
    private void resetScanner() {
        if (scanner != null) {
            scanner.close();
        }
        scanner = new Scanner(System.in);
    }
}
