package com.example.file;

import com.example.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j2;

/**
 * Класс для работы с XML.
 * Все парсеры и конверторы в одном месте.
 */

@Log4j2
public class ParserXML {
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Превращает коллекцию фильмов в строку формата XML.
     * @param collection коллекция фильмов
     * @return строка формата XML
     */
    public String convertCollectionToXMLString(TreeSet<Movie> collection){
        log.trace("Начало метод convertCollectionToXMLString()");
        String ans = "<collection>\n";
        for (Movie movie : collection) {
            ans += "\t<movie>\n"
            +  "\t\t<id>" + movie.getId() + "</id>\n"
            +  "\t\t<name>" + movie.getName() + "</name>\n"
            +  "\t\t<coordinates>\n"
            +  "\t\t\t<x>" + movie.getCoordinates().getX() + "</x>\n"
            +  "\t\t\t<y>" + movie.getCoordinates().getY() + "</y>\n"
            +  "\t\t</coordinates>\n"
            +  "\t\t<creationDate>" + DATE_FORMAT.format(movie.getCreationDate()) + "</creationDate>\n"
            +  "\t\t<oscarsCount>" + movie.getOscarsCount() + "</oscarsCount>\n"
            +  "\t\t<usaBoxOffice>" + movie.getUsaBoxOffice() + "</usaBoxOffice>\n"
            +  "\t\t<genre>" + (movie.getGenre() == null ? "" : movie.getGenre()) + "</genre>\n"
            +  "\t\t<mpaaRating>" + (movie.getGenre() == null ? "" : movie.getGenre()) + "</mpaaRating>\n";
            if (movie.getDirector() != null) {
                ans += "\t\t<director>\n"
                +  "\t\t\t<directorName>" + movie.getDirector().getName() + "</directorName>\n"
                +  "\t\t\t<directorBirthday>" + DATE_FORMAT.format(movie.getDirector().getBirthday()) + "</directorBirthday>\n"
                +  "\t\t\t<directorHeight>" + movie.getDirector().getHeight() + "</directorHeight>\n"
                +  "\t\t\t<directorWeight>" + movie.getDirector().getWeight() + "</directorWeight>\n"
                +  "\t\t\t<directorPassportID>" + movie.getDirector().getPassportID() + "</directorPassportID>\n"
                +  "\t\t</director>\n";
            } else {
                ans  += "\t\t<director></director>\n";
            }
            ans += "\t</movie>\n";
        }
        ans += "</collection>";
        log.debug("Изначальная коллекция: {}", collection.toString());
        log.debug("Полученный XML файл: {}", ans);
        return ans;
    }

    /**
     * Ищет фильмы в XML файле и преобразует с помощью parseMovie.
     * @param xmlLines прочитанный XML файл
     * @return коллекция фильмов
     */
    public TreeSet<Movie> getCollectionFromXML(List<String> xmlLines){
        log.trace("Начало метод getCollectionFromXML()");
        String xml = String.join("\n", xmlLines);
        TreeSet<Movie> movies = new TreeSet<>();
        Pattern regMovieContent = Pattern.compile("<movie>(.+?)</movie>", Pattern.DOTALL);
        Matcher movieMatcher = regMovieContent.matcher(xml);

        while (movieMatcher.find()) {
            String movieXML = movieMatcher.group(1);
            Movie movie = parseMovie(movieXML);
            movies.add(movie);
        }
        log.debug("XML файл: {}", xml);
        log.debug("Полученная коллекция: {}", movies.toString());
        return movies;
    }

    /**
     * Преобразует XML фильм в объект Movie.
     * Валидация не предусмотрена.
     * Использует getValue для получения содержимого тега.
     * @param xmlMovie строка с фильмом в формате XML
     * @return объект Movie
     */
    public Movie parseMovie(String xmlMovie){
        log.trace("Начало метод parseMovie()");

        String name = getValue(xmlMovie, "name");
        Movie movie;

        Double x = Double.valueOf(getValue(xmlMovie, "x"));
        double y = Double.parseDouble(getValue(xmlMovie, "y"));
        Coordinates coordinates = new Coordinates(x, y);

        long oscarsCount = Long.parseLong(getValue(xmlMovie, "oscarsCount"));
        Long usaBoxOffice = Long.valueOf(getValue(xmlMovie, "usaBoxOffice"));

        MovieGenre genre = (getValue(xmlMovie, "genre").isEmpty() ? null : MovieGenre.valueOf(getValue(xmlMovie, "genre")));
        MpaaRating mpaaRating = (getValue(xmlMovie, "mpaaRating").isEmpty() ? null : MpaaRating.valueOf(getValue(xmlMovie, "mpaaRating")));
        Person director;
        if (getValue(xmlMovie, "director").isEmpty()){
            director = null;
        } else {
            String directorName = getValue(xmlMovie, "directorName");
            Date birthday;
            try {
                birthday = DATE_FORMAT.parse(getValue(xmlMovie, "directorBirthday"));
            } catch (ParseException e) {
                log.error("Ошибка парсинга даты во время преобразованяи XML файла: {}", e.getMessage());
                log.debug(e.getStackTrace());
                birthday = new Date();
            }
            Long height = Long.valueOf(getValue(xmlMovie, "directorHeight"));
            int weight = Integer.parseInt(getValue(xmlMovie, "directorWeight"));
            String passportID = getValue(xmlMovie, "directorPassportID");
            director = new Person(directorName, birthday, height, weight, passportID);
        }
        movie = new Movie(name, coordinates, oscarsCount, usaBoxOffice, genre, mpaaRating, director);
        log.debug("XML фильм: {}", xmlMovie);
        log.debug("Полученный фильм: {}", movie.toString());
        return movie;
    }

    /**
     * Получает содержимое XML тега.
     * @param xml XML тег с началом и концом
     * @param tagName имя XML тега
     * @return содержимое XML тега
     */
    private String getValue(String xml, String tagName) {
        log.trace("Начало метод getValue()");
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)</" + tagName + ">", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);
        String ans;
        if (matcher.find()) {
            ans = matcher.group(1).trim();
        } else {
            ans = "";
        }
        log.debug("Получено содержимое тега: {}", ans);
        return ans;
    }
}
