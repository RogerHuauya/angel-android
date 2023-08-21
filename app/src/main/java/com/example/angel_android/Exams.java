package com.example.angel_android;

import com.google.gson.annotations.SerializedName;

public class Exams {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("exam_grade")
    private String exam_grade;

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

    public void setExamGrade(String exam_grade){
        this.exam_grade = exam_grade;
    }

    public String getExamGrade(){
        return this.exam_grade;
    }

    public Exams(String id, String name) {
        this.name = name;
        this.id = id;
    }
    public Exams(String id, String name, String exam_grade) {
        this.name = name;
        this.id = id;
        this.exam_grade = exam_grade;
    }
}
