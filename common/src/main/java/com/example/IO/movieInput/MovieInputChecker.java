package com.example.IO.movieInput;

import com.example.IO.interfaces.OutputManager;
import com.example.IO.exception.*;
import lombok.extern.log4j.Log4j2;

/**
 * Класс для объединения Чекера и Ридера в одном месте.
 * Содержит главный фабричный метод - readAndValidate.
 */

@Log4j2
public class MovieInputChecker {
    private final OutputManager coh;
    public MovieInputChecker(OutputManager coh) {
        this.coh = coh;
    }

    /**
     * Читает и валидирует одно поле Movie.
     * Содержит в себе бесконечный цикл чтения и валидирования.
     * Цикл завершается, когда поле введено верно.
     * @param reader метод для чтения поля
     * @param validator методя для валидации поля
     * @return поле
     * @param <T> тип данных поля
     */
    public <T> T readAndValidate(ThrowingSupplier<String> reader, ThrowingFunction<String, T> validator){
        log.trace("Начало метод readAndValidate()");
        while (true) {
            try {
                String input = reader.get();
                log.debug("Прочитано значения поля {}", input);
                T ans = validator.apply(input);
                log.debug("Полученное значения поля {}", ans);
                return ans;
            } catch (EmptyInputException | OutOfRangeException | IllegalArgumentException e) {
                coh.printError(e.getMessage());
            } catch (Exception e) {
                coh.printError("Неизвестная ошибка: " + e.getMessage());
            }

        }
    }
}
