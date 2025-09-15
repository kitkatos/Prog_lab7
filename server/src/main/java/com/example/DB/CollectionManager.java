package com.example.DB;

import com.example.model.Movie;

import java.util.Date;
import java.util.TreeSet;

/**
 * Интерфейс, описывающий взаимодействие с данным в бдшке и коллекции.
 */
public interface CollectionManager {
    Movie getElemById(int id);
    int getCollectionSize();
    void addElem(Movie movie);

    /**
     * Удаляет прошлый элемент с id и
     * заменяет его на новый фильм
     * @param id айди обновляемого элемента
     * @param newMovie новый фильм
     */
    void updateElemById(int id, Movie newMovie);
    void removeElemById(int id, String login);

    /**
     * Удаляет только те элементы, которые создан пользователем.
     * Только если пользователь не админ.
     */
    void deleteAllElem(String login);

    TreeSet<Movie> getCollection();
    boolean addElemIfMax(Movie movie);

    /**
     * Находит все элементы, больше заданного.
     * Удаляет те, которые созданы тобой.
     * Или все, если пользователь админ.
     * @param movie элемент для сравнения
     * @return число удаленных элементов
     */
    int removeGreaterElements(Movie movie);
    Movie getElemWithMinCreationDate();
    Movie getElemWithMaxId();
    public TreeSet<Movie> getElemsWithMatchName(String filter);
}

