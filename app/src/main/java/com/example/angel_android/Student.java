package com.example.angel_android;
import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("enrollmentId")
    private String enrollmentId;

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

    public String getEnrollmentId(){
        return enrollmentId;
    }

    public Student(String id, String name, String enrollmentId) {
        this.name = name;
        this.id = id;
        this.enrollmentId = enrollmentId;
    }
}
