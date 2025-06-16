package ru.practice.springuserservice.cli;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.services.UserService;

import java.util.List;
import java.util.Scanner;

@Component
public class CLI implements CommandLineRunner {

    private final Scanner scanner;
    private final UserService userService;
    private final Validator validator;

    public CLI(UserService userService, Validator userDTOValidator) {
        this.scanner = new Scanner(System.in);
        this.userService = userService;
        this.validator = userDTOValidator;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean isExit = false;
        String line;

        while (!isExit) {
            System.out.println("""
                    Выберите действие:\s
                    1. создать пользователя\s
                    2. найти пользователя\s
                    3. найти всех пользователей\s
                    4. обновить пользователя\s
                    5. удалить пользователя\s
                    6. выход
                    """);

            line = scanner.nextLine();

            //logger.debug("Main menu option: {} (1-save, 2-show user, 3-show all, 4-update user, 5-delete user, 6-exit)", line);

            switch (line) {
                case "1" -> createUser();
                case "2" -> readUser();
                case "3" -> readAllUsers();
                case "4" -> updateUser();
                case "5" -> deleteUser();
                case "6" -> isExit = true;
                default -> {
                    //logger.warn("Unsupported command");
                }
            }
        }
    }

    private void createUser() {
        //logger.info("Creating user");

        System.out.println("Введите имя:");
        String name = scanner.nextLine();

        System.out.println("Введите email:");
        String email = scanner.nextLine();

        System.out.println("Введите возраст:");
        Integer age = readInt();

        if (age == null) {
            //logger.error("User was not created: Invalid age");
            System.out.println("Пользователь не был создан: некорректный возраст");
            return;
        }

        //logger.debug("name={}, email={}, age={}", name, email, age);

        UserDTO user = new UserDTO(0, name, email, age);
        Errors errors = validateUser(user);

        if (errors.hasErrors()) {
            System.out.println("Пользователь не был сохранен. " + fieldErrorsToString(errors));
            return;
        }

        try {
            userService.create(user);
        } catch (Exception e) {
            //logger.error("User was not created: {}", e.getMessage());
            System.out.println("Что то не так с базой данных. Пользователь не был создан");
        }
    }

    private void readUser() {
        //logger.info("Reading user");

        System.out.println("Введите id: ");

        //logger.debug("Parsing id");

        Integer id = readInt();

        if (id == null || id <= 0) {
            //logger.error("Invalid user id");
            System.out.println("Пользователь не был создан: некорректный id");
            return;
        }

        //logger.debug("user id = {}", id);

        UserDTO user = null;
        try {
            user = userService.read(id);
            System.out.println(user);
        } catch (EntityNotFoundException e) {
            System.out.println("Пользователь не найден: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            //logger.error("User was not read: {}", e.getStackTrace());
        }
    }

    private void readAllUsers() {
        //logger.info("Reading all users");

        List<UserDTO> users = null;
        try {
            users = userService.readAll();
            users.forEach(System.out::println);
        } catch (EntityNotFoundException e) {
            System.out.println("Пользователи не найдены: " + e.getMessage());
        } catch (Exception e) {
            //logger.error("Users was not read: {}", e.getStackTrace());
            System.out.println("Что то не так с базой данных. Пользователи не были найдены");
            return;
        }
    }

    private void updateUser() {
        //logger.info("Updating user");

        System.out.println("Введите id: ");

        //logger.debug("Parsing id");

        Integer id = readInt();

        if (id == null || id <= 0) {
            //logger.info("Invalid user id");
            System.out.println("Пользователь не был найден: некорректный id.");
            return;
        }

        UserDTO userToUpdate = null;
        try {
            userToUpdate = userService.read(id);
        } catch (EntityNotFoundException e) {
            System.out.println("Пользователь не найден: " + e.getMessage());
            return;
        } catch (Exception e) {
            //logger.error("User was not checked for existence: {}", e.getStackTrace());
            System.out.println("Что то не так с базой данных. Пользователь для обновления не был найден");
            return;
        }

        boolean isSave = false;
        while (!isSave) {
            System.out.println("\nТекущее значение полей: " + userToUpdate);
            System.out.println("Выберите действие: \n1. сменить имя \n2. сменить email \n3. сменить возраст \n4. применить изменения");
            String line = scanner.nextLine();

            switch (line) {
                case "1" -> {
                    System.out.println("Введите новое имя: ");
                    userToUpdate.setName(scanner.nextLine());
                }
                case "2" -> {
                    System.out.println("Введите новый email: ");
                    userToUpdate.setEmail(scanner.nextLine());
                }
                case "3" -> {
                    System.out.println("Введите новый возраст: ");
                    Integer age = readInt();
                    if (age == null) continue;
                    userToUpdate.setAge(age);
                }
                case "4" -> isSave = true;
                default -> {
                    //logger.warn("Unsupported command");
                }
            }
        }
        // logger.debug("Fields state to update: {}", userToUpdate);
        Errors errors = validateUser(userToUpdate);
        if (errors.hasErrors()) {
            System.out.println("Пользователь не был сохранен. " + fieldErrorsToString(errors));
            return;
        }

        try {
            userService.update(userToUpdate.getId(), userToUpdate);
            System.out.println("Пользователь успешно обновлен");
        } catch (EntityNotFoundException e) {
            System.out.println("Пользователь не найден: " + e.getMessage());
        } catch (Exception e) {
            //logger.error("User was not updated: {}", e.getMessage());
            System.out.println("Что-то не так с базой данных. Пользователь не был обновлен.");
        }
    }

    private void deleteUser() {
        //logger.info("Deleting user");

        System.out.println("Введите id: ");

        //logger.debug("Parsing id");

        Integer id = readInt();
        if (id == null || id <= 0) {
            // logger.error("Invalid user id");
            System.out.println("Пользователь не был удален: некорректный id");
            return;
        }

        try {
            userService.delete(id);
        } catch (EntityNotFoundException e) {
            System.out.println("Пользователь не найден: " + e.getMessage());
        } catch (Exception e) {
            // logger.error("User was not deleted: {}", e.getStackTrace());
            System.out.println("Что-то не так с базой данных. Пользователь не был удален");
        }
    }

    private Integer readInt() {
        try {
            String line = scanner.nextLine();
            //logger.debug("Parsing age");
            //logger.debug("Line to parse as int: {}", line);

            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            //logger.error("Line does not contain int. {}", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    private Errors validateUser(UserDTO user) {
        Errors errors = new BeanPropertyBindingResult(user, "UserDTO");

        int nameLength = user.getName().length();
        if (nameLength > 256 || nameLength == 0) {
            //logger.warn("Name length should be between 1 and 256 characters");
            errors.rejectValue("name", "" ,"Длина имени должна быть от 1 до 256 символов. ");
        }

        int emailLength = user.getEmail().length();
        if (emailLength > 256 || emailLength == 0) {
            //logger.warn("Email length should be between 1 and 256 characters");
            errors.rejectValue("email", "" ,"Длина email должна быть от 1 до 256 символов. ");
        }

        int userAge = user.getAge();
        if (userAge < 0 || userAge > 120) {
            //logger.warn("Age should be in range of 0 and 120 years");
            errors.rejectValue("age", "" ,"Возраст должен быть в диапазоне от 0  до 120 лет ");
        }

        validator.validate(user, errors);
        return errors;
    }

    private String fieldErrorsToString(Errors errors) {
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : errors.getFieldErrors()) {
            builder
                    .append(fieldError.getField())
                    .append(" error: ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }
        return builder.toString();
    }
}
