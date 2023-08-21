package com.example.angel_android;

import com.google.gson.annotations.SerializedName;

public class Course {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Course(String id, String name) {
        this.name = name;
        this.id = id;

    }
}
