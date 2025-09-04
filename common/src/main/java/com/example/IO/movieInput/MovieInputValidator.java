package com.example.IO.movieInput;

import com.example.IO.exception.*;
import com.example.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Класс для валидации полей Movie при создании объекта.
 * Содержит методы под каждое поле Movie.
 * Если поле имеет неверное значение, то выбрасывается исключение.
 * Методы этого класса не используют логировае или вывод, потому что все исключения с сообщениями пробрасываются выше.
 */

public class MovieInputValidator {
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public String validateName(String rawName){
        return rawName;
    }

    public long validateOscarsCount(String rawOscars) throws OutOfRangeException, NumberFormatException{
        try {
            long value = Long.parseLong(rawOscars);
            if (value <= 0) {
                throw new OutOfRangeException("Количество Оскаров должно быть больше 0");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Оскары задаются с помощью числа");
        }
    }

    public Long validateUsaBoxOffice(String rawBoxOffice) throws OutOfRangeException, NumberFormatException{
        try {
            Long value = Long.parseLong(rawBoxOffice);
            if (value <= 0) {
                throw new OutOfRangeException("Сборы должны быть больше 0");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Сборы должны быть числом");
        }
    }

    public Double validateCoordinateX(String rawX) throws OutOfRangeException, NumberFormatException{
        try {
            Double value = Double.valueOf(rawX);
            if (value <= -382) {
                throw new OutOfRangeException("Координата X должна быть больше -382");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Координата X должна быть числом");
        }
    }

    public double validateCoordinateY(String rawY) throws NumberFormatException{
        try {
            return Double.parseDouble(rawY);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Координата Y должна быть числом");
        }
    }

    public MovieGenre validateGenre(String rawGenre) throws NumberFormatException {
        if (rawGenre.isEmpty()) {
            return null;
        }
        try {
            return MovieGenre.valueOf(rawGenre.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Жанр указан некорректно");
        }
    }

    public MpaaRating validateMpaaRating(String rawRating) throws NumberFormatException {
        if (rawRating.isEmpty()) {
            return null;
        }
        try {
            return MpaaRating.valueOf(rawRating.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Рейтинг MPAA указан некорректно");
        }
    }

    public boolean validatePersonIsEmpty(String ans) throws NumberFormatException{
        String word = ans.toLowerCase();
        if (word.equals("yes") || word.equals("lf") || word.equals("нуы") || word.equals("да")) {
            return true;
        } else if (word.equals("no") || word.equals("ytn") || word.equals("тщ") || word.equals("нет")) {
            return false;
        } else {
            throw new NumberFormatException("Введите yes или no");
        }
    }
    public String validateDirectorName(String rawName){
        return rawName;
    }

    public Date validateDirectorBirthday(String rawBirthday) throws OutOfRangeException, NumberFormatException{
        try {
            Date date = DATE_FORMAT.parse(rawBirthday);
            if (date.after(new Date())) {
                throw new OutOfRangeException("Дата не может быть в будущем");
            }
            return date;
        } catch (ParseException e) {
            throw new NumberFormatException("Неверный формат даты. Используйте yyyy-MM-dd");
        }
    }

    public Long validateDirectorHeight(String rawHeight)  throws OutOfRangeException, NumberFormatException{
        try {
            Long value = Long.valueOf(rawHeight);
            if (value <= 0) {
                throw new OutOfRangeException("Рост должен быть больше 0");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Рост должен быть числом");
        }
    }

    public Integer validateDirectorWeight(String rawWeight)  throws OutOfRangeException, NumberFormatException {
        try {
            int value = Integer.parseInt(rawWeight);
            if (value <= 0) {
                throw new OutOfRangeException("Вес должен быть больше 0");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Вес должен быть числом");
        }
    }

    public String validateDirectorPassportID(String rawPassportID)  throws OutOfRangeException{
        if (rawPassportID.length() < 6 || rawPassportID.length() > 47) {
            throw new OutOfRangeException("Длина паспорта должна быть от 6 до 47 символов");
        }
        return rawPassportID;
    }
}
