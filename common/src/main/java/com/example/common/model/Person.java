package com.example.common.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;

@Data
public class Person implements Serializable {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private String name;
    private Date birthday;
    private Long height;
    private int weight;
    private String passportID;

    public Person(String name, Date birthday, Long height, int weight, String passportID) {
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.passportID = passportID.trim();
    }

    @Override
    public String toString(){
        return "Person{name=" + name + ", birthday=" + DATE_FORMAT.format(birthday)
        + ", height=" + height + ", weight=" + weight 
        + ", passportID=" + passportID + "}";
    }
    public boolean validate(){
        if (name == null || name.isEmpty()) return false;
        if (birthday == null) return false;
        if (height <= 0) return false;
        if (weight <= 0) return false;
        return (!(passportID == null) && passportID.length() >= 6 && passportID.length() <= 47);
    }

    /**
     * Преобразует строковое представление объекта Person в сам объект.
     * Ожидаемый формат: "Person{name=<имя>, birthday=<дата>, height=<рост>, weight=<вес>, passportID=<паспорт>}"
     *
     * @param personString Строковое представление объекта Person.
     * @return Объект Person.
     * @throws IllegalArgumentException если строка имеет некорректный формат или данные недействительны.
     */
    public static Person parseFromString(String personString) {
        if (personString == null || personString.isEmpty()) {
            throw new IllegalArgumentException("Строка Person не может быть пустой.");
        }

        try {
            // Удаляем префикс и суффикс
            String content = personString.trim();
            if (!content.startsWith("Person{") || !content.endsWith("}")) {
                throw new IllegalArgumentException("Некорректный формат строки Person.");
            }
            content = content.substring("Person{".length(), content.length() - 1);

            // Регулярное выражение для извлечения полей
            Pattern pattern = Pattern.compile("(\\w+)=(?:'([^']*)'|([^,]+))");
            Matcher matcher = pattern.matcher(content);

            Map<String, String> fields = new HashMap<>();
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
                fields.put(key.trim(), value.trim());
            }

            // Извлечение и преобразование полей
            String name = fields.get("name");
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Поле 'name' не найдено или пусто.");
            }

            Date birthday = DATE_FORMAT.parse(fields.get("birthday"));
            Long height = Long.parseLong(fields.get("height"));
            int weight = Integer.parseInt(fields.get("weight"));
            String passportID = fields.get("passportID");

            return new Person(name, birthday, height, weight, passportID);

        } catch (ParseException e) {
            throw new IllegalArgumentException("Некорректный формат даты в поле 'birthday'. Ожидаемый формат: yyyy-MM-dd.", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректный формат числа в полях 'height' или 'weight'.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при парсинге строки Person: " + e.getMessage(), e);
        }
    }
}