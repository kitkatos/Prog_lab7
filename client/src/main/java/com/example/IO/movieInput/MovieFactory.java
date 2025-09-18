package com.example.common.IO.movieInput;

import com.example.common.model.*;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * Фабрика для создания фильмов через ввод.
 * Ипользует метод readAndValidate для считывания каждого из полей.
 * Содержит в себе методы для создания Фильма с конкретным ID,
 * и со случайным ID.
 */

@Log4j2
public class MovieFactory {
    private final MovieInputReader reader;
    private final MovieInputChecker checker;
    private final MovieInputValidator validator;

    public MovieFactory(MovieInputReader reader,  MovieInputValidator validator, MovieInputChecker checker) {
        this.reader = reader;
        this.checker = checker;
        this.validator = validator;
    }

    public Movie createMovie() {
        String name = checker.readAndValidate(reader::readName, validator::validateName);
        Coordinates coordinates = createCoordinates();  // вызываем метод для создания координат
        long oscarsCount = checker.readAndValidate(reader::readOscarsCount, validator::validateOscarsCount);
        Long usaBoxOffice = checker.readAndValidate(reader::readUsaBoxOffice, validator::validateUsaBoxOffice);
        MovieGenre genre = checker.readAndValidate(reader::readGenre, validator::validateGenre);
        MpaaRating mpaaRating = checker.readAndValidate(reader::readMpaaRating, validator::validateMpaaRating);
        boolean directorIsEmpty = checker.readAndValidate(reader::PersonIsEmpty, validator::validatePersonIsEmpty);
        Person director;
        if (directorIsEmpty) {
            director = null;
        } else {
            director = createDirector();
        }
        Movie ans = new Movie(name, coordinates, oscarsCount, usaBoxOffice, genre, mpaaRating, director);
        log.debug("Создан фильм с автоматическим ID: {}", ans.toString());
        return ans;
    }

    public Movie createMovieWithId(int id) {
        String name = checker.readAndValidate(reader::readName, validator::validateName);
        Coordinates coordinates = createCoordinates();  // вызываем метод для создания координат
        long oscarsCount = checker.readAndValidate(reader::readOscarsCount, validator::validateOscarsCount);
        Long usaBoxOffice = checker.readAndValidate(reader::readUsaBoxOffice, validator::validateUsaBoxOffice);
        MovieGenre genre = checker.readAndValidate(reader::readGenre, validator::validateGenre);
        MpaaRating mpaaRating = checker.readAndValidate(reader::readMpaaRating, validator::validateMpaaRating);
        boolean directorIsEmpty = checker.readAndValidate(reader::PersonIsEmpty, validator::validatePersonIsEmpty);
        Person director;
        if (directorIsEmpty) {
            director = null;
        } else {
            director = createDirector();

        }
        Movie ans = new Movie(id, name, coordinates, oscarsCount, usaBoxOffice, genre, mpaaRating, director);
        log.debug("Создан фильм c ID: {}", ans.toString());
        return ans;
    }

    private Coordinates createCoordinates() {
        Double x = checker.readAndValidate(reader::readCoordinateX, validator::validateCoordinateX);
        double y = checker.readAndValidate(reader::readCoordinateY, validator::validateCoordinateY);
        return new Coordinates(x, y);
    }

    private Person createDirector() {
        String name = checker.readAndValidate(reader::readDirectorName, validator::validateDirectorName);
        Date birthday = checker.readAndValidate(reader::readDirectorBirthday, validator::validateDirectorBirthday);
        Long height = checker.readAndValidate(reader::readDirectorHeight, validator::validateDirectorHeight);
        Integer weight = checker.readAndValidate(reader::readDirectorWeight, validator::validateDirectorWeight);
        String passportID = checker.readAndValidate(reader::readDirectorPassportID, validator::validateDirectorPassportID);

        return new Person(name, birthday, height, weight, passportID);
    }

}
