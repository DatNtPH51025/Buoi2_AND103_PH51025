package com.example.lab1_ph51025;

public class UserModel {
    private String id;  // Trường lưu ID của người dùng
    private String name;
    private int age;
    private String occupation;
    private String city;

    public UserModel(String id, String name, int age, String occupation, String city) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.occupation = occupation;
        this.city = city;
    }

    public UserModel() {
    }

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
