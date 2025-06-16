package ru.practice.springuserservice.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.services.UserService;
import ru.practice.springuserservice.util.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Validator validator;

    public UserController(UserService userService, Validator userDTOValidator) {
        this.userService = userService;
        this.validator = userDTOValidator;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        validator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            UserResponse response = new UserResponse("Пользователь не сохранен: " + fieldErrorsToString(bindingResult));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        UserDTO user = userService.create(userDTO);
        UserResponse response = new UserResponse("Пользователь сохранен под id = " + user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public UserDTO read(@PathVariable int id) {
        return userService.read(id);
    }

    @GetMapping("")
    public List<UserDTO> readAll() {
        return userService.readAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable int id,
                                               @RequestBody @Valid UserDTO userDTO,
                                               BindingResult bindingResult) {
        userDTO.setId(id);
        validator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            UserResponse response = new UserResponse("Пользователь не обновлен: " + fieldErrorsToString(bindingResult));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        UserDTO user = userService.update(id, userDTO);
        UserResponse response = new UserResponse("Пользователь с id = " + user.getId() + " обновлен.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable int id)  {
        userService.delete(id);
        UserResponse response = new UserResponse("Пользователь с id = " + id + " удален.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<UserResponse> handleException(EntityNotFoundException e) {
        UserResponse response = new UserResponse(
                e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<UserResponse> handleException(JDBCException e) {
        UserResponse response = new UserResponse(
                "Ошибка при обращении к базе данных:" + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String fieldErrorsToString(BindingResult bindingResult) {
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder
                    .append(fieldError.getField())
                    .append(" error: ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }
        return builder.toString();
    }
}
