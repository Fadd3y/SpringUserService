package ru.practice.springuserservice.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.models.User;
import ru.practice.springuserservice.services.UserService;
import ru.practice.springuserservice.util.UserDTOValidator;
import ru.practice.springuserservice.util.UserResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserDTOValidator validator;

    public UserController(UserService userService, UserDTOValidator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        validator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            UserResponse response = new UserResponse("Пользователь не сохранен: " + fieldErrorsToString(bindingResult));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        User user = userService.create(userDTO);
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
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        User user = userService.update(id, userDTO);
        UserResponse response = new UserResponse("Пользователь с id = " + user.getId() + " обновлен.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable int id) {
        userService.delete(id);
        UserResponse response = new UserResponse("Пользователь с id = " + id + " удален.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<UserResponse> handleException(EntityNotFoundException e) {
        UserResponse response = new UserResponse(
                e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
