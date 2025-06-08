package ru.practice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UserDTO {

    private int id;

    @Size(max = 256, min = 1, message = "Количество символов в имени должно быть от 1 до 256. ")
    private String name;

    @Size(max = 256, min = 1, message = "Количество символов в email должно быть от 1 до 256. ")
    @Email
    private String email;

    @Min(value = 0, message = "Возраст должен быть не менее 0 лет")
    @Max(value = 120, message = "Возраст должен быть не более 120 лет")
    private int age;

    public UserDTO() {
    }

    public UserDTO(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
