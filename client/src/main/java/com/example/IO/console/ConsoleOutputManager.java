package com.example.common.IO.console;

import com.example.common.IO.interfaces.OutputManager;
import lombok.extern.log4j.Log4j2;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Класс для вывода текста в консоль в формате UTF8.
 * Основные потоки (stdout и stderr) инициализируются с кодировкой UTF-8.
 * Если UTF-8 не поддерживается, используются стандартные потоки {@code System.out} и {@code System.err}.
 */

@Log4j2
public class ConsoleOutputManager implements OutputManager {
    private final PrintStream utf8Out;
    private final PrintStream utf8Err;

    private final PrintStream fallbackOut;
    private final PrintStream fallbackErr;

    public ConsoleOutputManager() {
        this.fallbackOut = System.out;
        this.fallbackErr = System.err;

        // Инициализируем UTF-8 потоки
        PrintStream utf8OutTemp;
        PrintStream utf8ErrTemp;
        try {
            utf8OutTemp = new PrintStream(System.out, true, StandardCharsets.UTF_8.name());
            utf8ErrTemp = new PrintStream(System.err, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Если UTF-8 не поддерживается, используем фолбэк
            utf8OutTemp = fallbackOut;
            utf8ErrTemp = fallbackErr;
        }
        this.utf8Out = utf8OutTemp;
        this.utf8Err = utf8ErrTemp;
    }

    @Override
    public void printLine(String str) {
        try {
            utf8Out.println(str);
        } catch (Exception e) {
            fallbackOut.println(str);
        }
    }

    @Override
    public void printString(String str) {
        try {
            utf8Out.print(str);
        } catch (Exception e) {
            fallbackOut.print(str);
        }
    }

    @Override
    public void printError(String str) {
        try {
            utf8Err.println("ERROR: " + str);
            log.error(str);
        } catch (Exception e) {
            fallbackErr.println("ERROR: " + str);
            log.error(str);
        }
    }
}