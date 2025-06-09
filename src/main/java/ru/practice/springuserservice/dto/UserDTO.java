package ru.practice.springuserservice.dto;

import jakarta.validation.constraints.*;

public class UserDTO {

    @Max(value = 0, message = "id должен быть либо равен нулю, либо не указан вовсе. ")
    @Min(value = 0, message = "id должен быть либо равен нулю, либо не указан вовсе. ")
    private int id;

    @NotEmpty(message = "Имя не должно быть пустым. ")
    @Size(max = 256, min = 1, message = "Количество символов в имени должно быть от 1 до 256. ")
    private String name;

    @NotEmpty(message = "Email не должен быть пустым. ")
    @Size(max = 256, min = 1, message = "Количество символов в email должно быть от 1 до 256. ")
    @Email(message = "Должен быть корректный формат электронной почты. ")
    private String email;

    @Min(value = 0, message = "Возраст должен быть не менее 0 лет. ")
    @Max(value = 120, message = "Возраст должен быть не более 120 лет. ")
    private int age;

    public UserDTO() {
    }

    public UserDTO(int id, String name, String email, int age) {
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

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
