package com.example.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.Comparable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Movie implements Comparable<Movie>, Serializable {
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @Getter @Setter
    private int id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private Coordinates coordinates;
    @Getter @Setter
    private Date creationDate;
    @Getter @Setter
    private long oscarsCount;
    @Getter @Setter
    private Long usaBoxOffice;
    @Getter @Setter
    private MovieGenre genre;
    @Getter @Setter
    private MpaaRating mpaaRating;
    @Getter @Setter
    private Person director;
    @Getter @Setter
    private String ownerLogin;

    public Movie(int id, String name, Coordinates coordinates, long oscarsCount,
                 Long usaBoxOffice, MovieGenre genre, MpaaRating mpaaRating, Person director){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date();
        this.oscarsCount = oscarsCount;
        this.usaBoxOffice = usaBoxOffice;
        this.genre = genre;
        this.mpaaRating = mpaaRating;
        this.director = director;
    }

    public Movie(String name, Coordinates coordinates, long oscarsCount,
                 Long usaBoxOffice, MovieGenre genre, MpaaRating mpaaRating, Person director){
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date();
        this.oscarsCount = oscarsCount;
        this.usaBoxOffice = usaBoxOffice;
        this.genre = genre;
        this.mpaaRating = mpaaRating;
        this.director = director;
    }

    public Movie(int id, String name, Coordinates coordinates, Date creationDate,
                 long oscarsCount, Long usaBoxOffice, MovieGenre genre,
                 MpaaRating mpaaRating, Person director, String ownerLogin) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate != null ? creationDate : new Date();
        this.oscarsCount = oscarsCount;
        this.usaBoxOffice = usaBoxOffice;
        this.genre = genre;
        this.mpaaRating = mpaaRating;
        this.director = director;
        this.ownerLogin = ownerLogin;
    }

    @Override
    public String toString(){
        return String.format(
            "Movie{id=%s, name='%s', coordinates=%s, creationDate=%s, oscarsCount=%s, usaBoxOffice=%s%s%s%s, owner_id=%s}",
            id, name, coordinates, DATE_FORMAT.format(creationDate), oscarsCount, usaBoxOffice,
            (genre != null ? ", genre=" + genre : ""),
            (mpaaRating != null ? ", mpaaRating=" + mpaaRating : ""),
            (director != null ? ", director=" + director : ""), ownerLogin
        );
    }

    @Override
    public int compareTo(Movie movie){
        int res = Long.compare(this.oscarsCount, movie.oscarsCount);

        if (res == 0) {
            return Long.compare(this.usaBoxOffice, movie.usaBoxOffice);
        }

        return res;
        /*
            this > movie 1
            this = movie 0
            this < movie -1
         */
    }

    public static Movie parseFromString(String movieString) {
        if (movieString == null || movieString.isEmpty()) {
            throw new IllegalArgumentException("Строка фильма не может быть пустой.");
        }

        try {
            // Убираем внешние скобки и пробелы
            movieString = movieString.trim().substring("Movie{".length(), movieString.length() - 1);

            // Используем Map для удобного парсинга полей
            Map<String, String> fields = new HashMap<>();
            // Используем регулярное выражение для разделения полей по запятым, игнорируя запятые внутри скобок
            Pattern pattern = Pattern.compile("(\\w+)=(?:'([^']*)'|([^,]+)),?");
            Matcher matcher = pattern.matcher(movieString);

            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
                fields.put(key, value.trim());
            }

            // Извлекаем и преобразуем каждое поле
            int id = Integer.parseInt(fields.get("id"));
            String name = fields.get("name");

            // Парсинг Coordinates
            String coordsString = fields.get("coordinates").trim();
            coordsString = coordsString.substring(1, coordsString.length() - 1); // Удаляем скобки
            String[] coordsParts = coordsString.split(",");
            Double x = Double.parseDouble(coordsParts[0].trim());
            Float y = Float.parseFloat(coordsParts[1].trim());
            Coordinates coordinates = new Coordinates(x, y);

            // Парсинг дат и других полей
            Date creationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(fields.get("creationDate"));
            Long oscarsCount = Long.parseLong(fields.get("oscarsCount"));
            Long usaBoxOffice = Long.parseLong(fields.get("usaBoxOffice"));
            MovieGenre genre = fields.containsKey("genre") ? MovieGenre.valueOf(fields.get("genre").toUpperCase()) : null;
            MpaaRating mpaaRating = fields.containsKey("mpaaRating") ? MpaaRating.valueOf(fields.get("mpaaRating").toUpperCase()) : null;

            // Парсинг Director
            String directorString = fields.get("director");
            Person director = null;
            if (directorString != null) {
                director = Person.parseFromString(directorString);
            }

            String ownerLogin = fields.get("owner_id");

            return new Movie(id, name, coordinates, creationDate, oscarsCount, usaBoxOffice, genre, mpaaRating, director, ownerLogin);

        } catch (Exception e) {
            // Логируем ошибку и бросаем исключение с понятным сообщением
            throw new IllegalArgumentException("Ошибка при парсинге строки фильма: " + e.getMessage(), e);
        }
    }

    public boolean validate(){
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null) return false;
        if (oscarsCount == 0) return false;
        if (usaBoxOffice == 0) return false;
        if (genre == null) return false;
        if (mpaaRating == null) return false;
        return director != null;

    }
}