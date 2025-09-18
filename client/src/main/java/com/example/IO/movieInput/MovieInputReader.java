package com.example.IO.movieInput;

import com.example.IO.exception.EmptyInputException;
import com.example.IO.interfaces.OutputManager;
import com.example.IO.interfaces.InputManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс для чтения полей Movie из пользовательского ввода.
 * Содержит два базовых метода:
 * <ul>
 *   <li>{@link #readNonEmptyInput()} — считывание непустого ввода,</li>
 *   <li>{@link #readEmptyInput()} — считывание ввода, который может быть пустым.</li>
 * </ul>
 * Остальные методы построены на их основе и предназначены для чтения конкретных полей.
 */
public class MovieInputReader {

    private static final Logger log = LogManager.getLogger(MovieInputReader.class);

    private final InputManager cih;
    private final OutputManager coh;

    /**
     * Создает объект MovieInputReader.
     * @param cih менеджер ввода
     * @param coh менеджер вывода
     */
    public MovieInputReader(InputManager cih, OutputManager coh) {
        this.cih = cih;
        this.coh = coh;
    }

    public String readName() throws EmptyInputException {
        coh.printString("Введите название фильма:");
        log.trace("Ввод названия фильма...");
        return readNonEmptyInput();
    }

    public String readOscarsCount() throws EmptyInputException {
        coh.printString("Введите количество полученных Оскаров:");
        log.trace("Ввод количества Оскаров...");
        return readNonEmptyInput();
    }

    public String readUsaBoxOffice() throws EmptyInputException {
        coh.printString("Введите сумму сборов в США:");
        log.trace("Ввод сборов в США...");
        return readNonEmptyInput();
    }

    public String readGenre() {
        coh.printString("Введите жанр фильма (ACTION, DRAMA, MUSICAL, THRILLER, FANTASY) или оставьте пустым:");
        log.trace("Ввод жанра фильма...");
        return readEmptyInput();
    }

    public String readMpaaRating() {
        coh.printString("Введите рейтинг MPAA (G, PG, R, NC_17) или оставьте пустым:");
        log.trace("Ввод рейтинга MPAA...");
        return readEmptyInput();
    }

    public String readCoordinateX() throws EmptyInputException {
        coh.printString("Введите координату X:");
        log.trace("Ввод координаты X...");
        return readNonEmptyInput();
    }

    public String readCoordinateY() throws EmptyInputException {
        coh.printString("Введите координату Y:");
        log.trace("Ввод координаты Y...");
        return readNonEmptyInput();
    }

    public String PersonIsEmpty() throws EmptyInputException {
        coh.printLine("Хотите, чтобы поле Режисер осталось пустым? введите yes или no");
        log.trace("Ввод: оставить ли режиссёра пустым...");
        return readNonEmptyInput();
    }

    public String readDirectorName() throws EmptyInputException {
        coh.printString("Введите имя режиссера:");
        log.trace("Ввод имени режиссёра...");
        return readNonEmptyInput();
    }

    public String readDirectorBirthday() throws EmptyInputException {
        coh.printString("Введите день рождения режиссера (формат: yyyy-MM-dd):");
        log.trace("Ввод дня рождения режиссёра...");
        return readNonEmptyInput();
    }

    public String readDirectorHeight() throws EmptyInputException {
        coh.printString("Введите рост режиссера:");
        log.trace("Ввод роста режиссёра...");
        return readNonEmptyInput();
    }

    public String readDirectorWeight() throws EmptyInputException {
        coh.printString("Введите вес режиссера:");
        log.trace("Ввод веса режиссёра...");
        return readNonEmptyInput();
    }

    public String readDirectorPassportID() throws EmptyInputException {
        coh.printString("Введите паспортный ID режиссера:");
        log.trace("Ввод паспортного ID режиссёра...");
        return readNonEmptyInput();
    }

    /**
     * Считывает строку, которая не может быть пустой.
     * @return введённая строка
     * @throws EmptyInputException если введено пустое значение
     */
    private String readNonEmptyInput() throws EmptyInputException {
        String input = cih.readLine();
        log.trace("Считан ввод (обязательное поле): {}", input);
        if (input.isEmpty()) {
            throw new EmptyInputException("Поле не может быть пустым. Введите корректные данные");
        } else {
            return input;
        }
    }

    /**
     * Считывает строку, которая может быть пустой.
     * @return введённая строка (возможно пустая)
     */
    private String readEmptyInput() {
        String input = cih.readLine();
        log.trace("Считан ввод (допустимо пустое поле): {}", input);
        return input;
    }
}
