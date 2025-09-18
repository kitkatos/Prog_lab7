package com.example;

import com.example.IO.movieInput.MovieInputValidator;
import com.example.IO.interfaces.OutputManager;
import com.example.IO.exception.OutOfRangeException;
import com.example.common.model.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

@Log4j2
@Getter
@Setter
public class MovieBuilder {
    private final MovieInputValidator validator = new MovieInputValidator();
    private final OutputManager outputManager;
    private final long userId;

    private String name;
    private Coordinates coordinates;
    private long oscarsCount;
    private Long usaBoxOffice;
    private MovieGenre genre;
    private MpaaRating mpaaRating;
    private Person director;
    private PersonBuilder personBuilder;
    private boolean personIsEmpty;

    private int currentStep;
    private static final int TOTAL_STEPS = 8;

    public MovieBuilder(long userId, OutputManager outputManager) {
        this.userId = userId;
        this.outputManager = outputManager;
        this.currentStep = 0;
    }

    /**
     * Обрабатывает ввод от пользователя и пытается установить следующее поле.
     * @param input Строка ввода от пользователя.
     */
    public void processInput(String input) {
        if (personBuilder != null) {
            personBuilder.processInput(input);
            if (personBuilder.isComplete()) {
                this.director = personBuilder.build();
                personBuilder = null;
                currentStep = TOTAL_STEPS;
                return;
            } else {
                return;
            }
        }
        try {
            switch (currentStep) {
                case 0: // Имя фильма
                    this.name = validator.validateName(input);
                    outputManager.printLine("Название принято. Введите координату X:");
                    break;
                case 1: // Координата X
                    Double x = validator.validateCoordinateX(input);
                    if (this.coordinates == null) {
                        this.coordinates = new Coordinates(0.0, 0);
                    }
                    this.coordinates.setX(x);
                    outputManager.printLine("X принято. Введите координату Y:");
                    break;
                case 2: // Координата Y
                    double y = validator.validateCoordinateY(input);
                    this.coordinates.setY(y);
                    outputManager.printLine("Y принято. Введите количество Оскаров:");
                    break;
                case 3: // Oscars Count
                    this.oscarsCount = validator.validateOscarsCount(input);
                    outputManager.printLine("Количество Оскаров принято. Введите сборы в США:");
                    break;
                case 4: // USA Box Office
                    this.usaBoxOffice = validator.validateUsaBoxOffice(input);
                    outputManager.printLine("Сборы приняты. Введите жанр:");
                    break;
                case 5: // Genre
                    this.genre = validator.validateGenre(input);
                    outputManager.printLine("Жанр принят. Введите рейтинг MPAA:");
                    break;
                case 6: // MPAA Rating
                    this.mpaaRating = validator.validateMpaaRating(input);
                    outputManager.printLine("Рейтинг принят. Хотите, чтобы поле Режиссер осталось пустым? (yes/no)");
                    break;
                case 7: // Директор
                    this.personIsEmpty = validator.validatePersonIsEmpty(input);
                    if (personIsEmpty) {
                        this.director = null;
                    } else {
                        personBuilder = new PersonBuilder(outputManager);
                        outputManager.printLine("Введите имя режиссера:");
                    }
                    break;
                default:
                    outputManager.printLine("Неизвестный шаг. Пожалуйста, начните сначала.");
                    return;
            }
            if(personBuilder == null){
                currentStep++;
            }
        } catch (IllegalArgumentException |OutOfRangeException e) {
            outputManager.printError(e.getMessage());
        } catch (Exception e) {
            outputManager.printError("Произошла неизвестная ошибка.");
            log.error("Неизвестная ошибка в MovieBuilder: {}", e.getMessage());
        }
    }

    /**
     * Проверяет, все ли обязательные поля заполнены.
     * @return true, если все поля заполнены, иначе false.
     */
    public boolean isComplete() {
        return currentStep >= TOTAL_STEPS;
    }

    /**
     * Возвращает готовый объект Movie.
     *
     * @return Объект Movie или null, если не все поля заполнены.
     */
    public Movie build() {
        if (!isComplete()) {
            return null;
        }
        return new Movie(name, coordinates, oscarsCount, usaBoxOffice, genre, mpaaRating, director);
    }

    /**
     * Помощник для Person, который строит объект пошагово
     */
    private class PersonBuilder {
        private final OutputManager outputManager;
        private String name;
        private Date birthday;
        private Long height;
        private Integer weight;
        private String passportID;

        private int currentStep = 0;
        private static final int TOTAL_STEPS = 5;

        public PersonBuilder(OutputManager outputManager) {
            this.outputManager = outputManager;
        }
        public void processInput(String input){
            try {
                switch (currentStep) {
                    case 0:
                        this.name = validator.validateDirectorName(input);
                        outputManager.printLine("Имя принято. Введите дату рождения (yyyy-MM-dd):");
                        break;
                    case 1:
                        this.birthday = validator.validateDirectorBirthday(input);
                        outputManager.printLine("Дата принята. Введите рост:");
                        break;
                    case 2:
                        this.height = validator.validateDirectorHeight(input);
                        outputManager.printLine("Рост принят. Введите вес:");
                        break;
                    case 3:
                        this.weight = validator.validateDirectorWeight(input);
                        outputManager.printLine("Вес принят. Введите ID паспорта:");
                        break;
                    case 4:
                        this.passportID = validator.validateDirectorPassportID(input);
                        outputManager.printLine("Паспортный ID принят. Объект режиссера собран.");
                        break;
                }
                currentStep++;
            }catch (Exception e) {
                outputManager.printError(e.getMessage());
            }
        }

        public boolean isComplete() {
            return currentStep >= TOTAL_STEPS;
        }
        public Person build() {
            if (!isComplete()) {
                return null;
            }
            return new Person(name, birthday, height, weight, passportID);
        }
    }
}
