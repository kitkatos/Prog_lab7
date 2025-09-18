package com.example.DB;

import com.example.common.model.*;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Хранит копию бдшки.
 * Содержит методы для выполнения команд получения данных из коллекции(копии бдшки).
 * Содержит методы для изменения бдшки и синхронизации с коллекцией.
 * Содержит метод для аутентификации.
 * Методы, изменяющие бдшку, используют синхронизацию чтения и записи.
 */
@Log4j2
public class TreeSetCollectionManager implements CollectionManager{
    private TreeSet<Movie> collection;
    private Connection connection;

    private final ReentrantLock lock = new ReentrantLock();

    public TreeSetCollectionManager(TreeSet<Movie> collection, Connection connection){ // poamotret
        this.collection = collection;
        this.connection = connection;
    }

    public String getInfoAboutCollection() {
        return "Тип данных: " + collection.getClass().getName() + "\n"
                + "Количество элементов: " + collection.size();
    }

    @Override
    public Movie getElemById(int id) {
        return collection.stream()
            .filter(movie -> movie.getId() == id)
            .findAny().orElse(null);
    }


    @Override
    public int getCollectionSize(){
        return collection.size();
    }


    @Override
    public TreeSet<Movie> getCollection(){
        return collection;
    }

    @Override
    public Movie getElemWithMinCreationDate() {
        return collection.stream()
                .min(Comparator.comparing(Movie::getCreationDate))
                .orElse(null);
    }

    @Override
    public Movie getElemWithMaxId() {
        return collection.stream()
                .max(Comparator.comparing(Movie::getId))
                .orElse(null);
    }

    public TreeSet<Movie> getElemsWithMatchName(String filter){
        TreeSet<Movie> result = new TreeSet<>(collection.comparator());
        collection.stream()
            .filter(m -> m.getName().startsWith(filter))
            .forEach(m -> result.add(m));
        return result;
    }




    /**
     * Добавляет новый элемент в бдшку.
     * Если он добавился - добавляет этот элемент в таблицу.
     * @param movie новый элемент
     */
    @Override
    public void addElem(Movie movie) {
        String sql = """
            INSERT INTO movies (
                name, coordinate_x, coordinate_y, creation_date,
                oscars_count, usa_box_office, genre, mpaa_rating,
                director_name, director_birthday, director_height,
                director_weight, director_passport_id, owner_login
            ) VALUES (?, ?, ?, ?, ?, ?, ?::movie_genre, ?::mpaa_rating, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        lock.lock();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setMovieParameters(pstmt, movie);

            try (ResultSet rs = pstmt.executeQuery()) {
                log.info("Элемент успешно добавлен в бд");
                if (rs.next()) {
                    movie.setId(rs.getInt("id"));
                    collection.add(movie);
                    log.info("Элемент успешно добавлен в коллекцию");
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка добавления элемента в бд: {}", e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Обновляет элемент в дбшке.
     * Если элемент обновился - обновляет элемет в коллекции.
     * Использует значения логина из newMovie для проверки совственности.
     * @param id айди обновляемого элемента
     * @param newMovie новый фильм
     */
    @Override
    public void updateElemById(int id, Movie newMovie) {
        String sql = """
            UPDATE movies 
            SET name = ?, coordinate_x = ?, coordinate_y = ?, creation_date = ?,
                oscars_count = ?, usa_box_office = ?, genre = ?, mpaa_rating = ?,
                director_name = ?, director_birthday = ?, director_height = ?,
                director_weight = ?, director_passport_id = ?, owner_login = ?
            WHERE (id = ?) and (owner_login = ?)
            """;
        lock.lock();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setMovieParameters(pstmt, newMovie);
            pstmt.setInt(15, id);
            pstmt.setString(16, newMovie.getOwnerLogin());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                log.info("Элемент с id={} успешно обновлен в бд", id);
                Movie oldMovie = collection.stream()
                        .filter(movie -> movie.getId() == id)
                        .findFirst()
                        .orElse(null);

                if (oldMovie != null) {
                    collection.remove(oldMovie);
                    newMovie.setId(id);
                    collection.add(newMovie);
                    log.info("Элемент с id={} успешно обновлен в коллекции", id);
                } else {
                    log.error("Элемент с id={}не найден в коллекции", id);
                }
            } else {
                log.error("Элемент с id={} не найден в БД", id);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка обновления элемента в бд: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет элемент из бдшки, а затем и из коллекции.
     * Использует логин для проверки собственности.
     * @param id айди удаляемого элемента
     */
    @Override
    public void removeElemById(int id, String login) {
        String sql = "DELETE FROM movies WHERE (id = ?) and (owner_login = ?)";
        lock.lock();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, login);
            pstmt.executeUpdate();
            log.info("Элемент в id={} удален из бд", id);

            Movie movie = getElemById(id);
            if (movie != null) {
                collection.remove(movie);
                log.info("Элемент в id={} удален из коллекции", id);
            }
        } catch (SQLException e) {
            log.error("Ошибка удаления элемента из бд: {}", e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет все элементы из бд, которые принадлежат пользователю с логином.
     * Затем удаляет все элементы из коллекции
     * @param login идентификатор пользователя
     */
    @Override
    public void deleteAllElem(String login){
        String sql = "DELETE FROM movies WHERE owner_login = ?";
        lock.lock();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate(sql);
            log.info("Элементы с владельцем {} успешно удалены из бдшки", login);

            collection.removeIf(movie -> {
                return movie.getOwnerLogin().equals(login);
            });
            log.info("Элементы с владельцем {} успешно удалены из коллекции", login);
        } catch (SQLException e) {
            log.error("Ошибка очистки коллекции: {} ",e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Добавляет элемент в бд и коллекцию, если он максимальный.
     * Использует метод addElem(Movie movie).
     * @param maxMovie проверяемый элемент
     * @return результат добавления
     */
    @Override
    public boolean addElemIfMax(Movie maxMovie){
        boolean ans =  collection.stream()
                .filter(m -> m.compareTo(maxMovie) > 0).findAny().isPresent();
        if (ans) {
            addElem(maxMovie);
        } else {
            log.info("Элемент не оказался максимальным и не добавлен в бд");
        }
        return ans;
    }

    /**
     * Удаляет элементы, большие заданного.
     * Использует removeElemById.
     * Производит поиск удаляемых элементов по коллекции.
     * @param movie элемент для сравнения
     * @return
     */
    public int removeGreaterElements(Movie movie) {
        List<Integer> idsToDelete = collection.stream()
                .filter(m -> m.compareTo(movie) > 0)  // По условию сравнения
                .filter(m -> m.getOwnerLogin() != null && m.getOwnerLogin().equals(movie.getOwnerLogin()))  // Проверка прав
                .map(Movie::getId)
                .collect(Collectors.toList());
        log.info("Получен список элементов пользователя, больших заданного");

        int deletedCount = 0;
        for (int id : idsToDelete) {
            try {
                removeElemById(id, movie.getOwnerLogin());
                deletedCount++;
                log.info("Элемент с id={} успешно удален", id);
            } catch (Exception e) {
                log.error("Ошибка удаления элемента с id={}: {}", id,e.getMessage());
            }
        }
        log.info("Удалено {} элементов", deletedCount);
        return deletedCount;
    }


    /**
     * Вспомогательный метод для задания параметров PreparedStatement
     * во время добавления нового фильма в бдшку.
     * @param pstmt sql запрос в формате PreparedStatement
     * @param movie фильм
     * @throws SQLException любые ошибки sql
     */
    private void setMovieParameters(PreparedStatement pstmt, Movie movie) throws SQLException {
        pstmt.setString(1, movie.getName());
        pstmt.setDouble(2, movie.getCoordinates().getX());
        pstmt.setDouble(3, movie.getCoordinates().getY());
        pstmt.setTimestamp(4, new Timestamp(movie.getCreationDate().getTime()));
        pstmt.setLong(5, movie.getOscarsCount());
        pstmt.setDouble(6, movie.getUsaBoxOffice());
        pstmt.setString(7, movie.getGenre() != null ? movie.getGenre().name() : null);
        pstmt.setString(8, movie.getMpaaRating() != null ? movie.getMpaaRating().name() : null);

        if (movie.getDirector() != null) {
            pstmt.setString(9, movie.getDirector().getName());
            pstmt.setTimestamp(10, new Timestamp(movie.getDirector().getBirthday().getTime()));
            pstmt.setDouble(11, movie.getDirector().getHeight());
            pstmt.setInt(12, movie.getDirector().getWeight());
            pstmt.setString(13, movie.getDirector().getPassportID());
        } else {
            pstmt.setNull(9, Types.VARCHAR);
            pstmt.setNull(10, Types.TIMESTAMP);
            pstmt.setNull(11, Types.DOUBLE);
            pstmt.setNull(12, Types.INTEGER);
            pstmt.setNull(13, Types.VARCHAR);
        }

        pstmt.setString(14, movie.getOwnerLogin());
    }

    /**
     * Вспомогательный метод для генерации фильма при получении его из бдшки.
     * @param rs результат запроса
     * @return новый фильм
     * @throws SQLException любые ошибки sql
     */
    private Movie resultSetToMovie(ResultSet rs) throws SQLException {
        Person director = new Person("Default Director", new Date(), 170L, 70, "default_passport");
        Movie movie = new Movie(
                -1,
                "Default Movie",
                new Coordinates(0.0, 0.0),
                1L,
                1L,
                MovieGenre.ACTION,
                MpaaRating.PG,
                director
        );
        movie.setId(rs.getInt("id"));
        movie.setName(rs.getString("name"));

        movie.setCoordinates(new Coordinates(rs.getDouble("coordinate_x"), rs.getDouble("coordinate_y")));

        movie.setCreationDate(new Date(rs.getTimestamp("creation_date").getTime()));
        movie.setOscarsCount(rs.getInt("oscars_count"));
        movie.setUsaBoxOffice(rs.getLong("usa_box_office"));

        String genre = rs.getString("genre");
        if (genre != null) {
            movie.setGenre(MovieGenre.valueOf(genre));
        }

        String rating = rs.getString("mpaa_rating");
        if (rating != null) {
            movie.setMpaaRating(MpaaRating.valueOf(rating));
        }

        String directorName = rs.getString("director_name");
        if (directorName != null) {
            director.setName(directorName);
            director.setBirthday(new Date(rs.getTimestamp("director_birthday").getTime()));
            director.setHeight(rs.getLong("director_height"));
            director.setWeight(rs.getInt("director_weight"));
            director.setPassportID(rs.getString("director_passport_id"));
            movie.setDirector(director);
        }

        movie.setOwnerLogin(rs.getString("owner_login"));

        return movie;
    }


    /**
     * Проводит аутентификацию пользователей.
     * Если логин нашелся, то возвращается тру или фолс.
     * Если логин не нашелся, то добавляет нового пользователя.
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return успех или провал аутентификации
     */
    public boolean authenticate(String login, String password) {
        String hashedPassword = PasswordHasher.sha1(password);
        lock.lock();

        try {
            String selectSql = "SELECT password FROM users WHERE login = ?";
            try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
                stmt.setString(1, login);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        log.info("Пользователь идентефицирован");
                        return storedPassword.equals(hashedPassword);
                    }
                }
            }

            String insertSql = "INSERT INTO users (login, password) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setString(1, login);
                stmt.setString(2, hashedPassword);
                stmt.executeUpdate();
                log.info("Пользователь успешно добавлен");
                return true;
            }
        } catch (SQLException e) {
            log.error("Ошибка при аутентицикации: {}", e.getMessage());
            return false;
        } finally {
            lock.unlock();
        }

    }

    /**
     * Синхронизирует коллекцию с бдшкой в начале работы.
     * Полностью очищает коллекцию и добавляет все элементы из бдшки.
     */
    public void fullSynchronization() {
        collection.clear();
        String sql = "SELECT * FROM movies";
        lock.lock();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("name"),
                        new Coordinates(
                                rs.getObject("coordinate_x", Double.class),
                                rs.getDouble("coordinate_y")
                        ),
                        rs.getTimestamp("creation_date") != null
                                ? new Date(rs.getTimestamp("creation_date").getTime())
                                : null,
                        rs.getLong("oscars_count"),
                        rs.getObject("usa_box_office", Long.class),
                        rs.getString("genre") != null ? MovieGenre.valueOf(rs.getString("genre")) : null,
                        rs.getString("mpaa_rating") != null ? MpaaRating.valueOf(rs.getString("mpaa_rating")) : null,
                        rs.getString("director_name") != null
                                ? new Person(
                                rs.getString("director_name"),
                                rs.getTimestamp("director_birthday") != null
                                        ? new Date(rs.getTimestamp("director_birthday").getTime())
                                        : null,
                                rs.getObject("director_height", Long.class),
                                rs.getInt("director_weight"),
                                rs.getString("director_passport_id")
                        )
                                : null,

                        rs.getString("owner_login")
                );
            }
        } catch (SQLException e) {
            log.error("Ошибка пересоздания таблицы : {}", e.getMessage());
        } finally {
            lock.unlock();
        }

    }
}
