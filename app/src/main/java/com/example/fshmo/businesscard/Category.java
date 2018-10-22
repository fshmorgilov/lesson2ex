package com.example.fshmo.businesscard;

import java.io.Serializable;

public class Category implements Serializable {
    private final int id;
    private final String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name.toUpperCase();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}