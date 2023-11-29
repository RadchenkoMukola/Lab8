package org.example.Core.Models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class Player implements Serializable {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String PLAYER = "Player";

    private int id;
    private int age;
    private String name;

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}