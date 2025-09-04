package com.example.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

@Data
public class Person implements Serializable {
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
}