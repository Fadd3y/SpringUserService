package ru.practice.springuserservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.hibernate.JDBCException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.services.UserService;
import ru.practice.springuserservice.util.UserDTOLinks;
import ru.practice.springuserservice.util.UserDTOValidator;
import ru.practice.springuserservice.util.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRESTController {

    private final UserService userService;
    private final UserDTOValidator validator;
    private final UserDTOLinks links;

    public UserRESTController(UserService userService, UserDTOValidator validator, UserDTOLinks links) {
        this.userService = userService;
        this.validator = validator;
        this.links = links;
    }

    @Operation(summary = "Создание пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> create(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        validator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            UserResponse response = new UserResponse("Пользователь не сохранен: " + fieldErrorsToString(bindingResult));
            return new ResponseEntity<>(EntityModel.of(
                    response,
                    links.create(true), links.readAll(false)),
                    HttpStatus.BAD_REQUEST
            );
        }

        UserDTO user = userService.create(userDTO);

        UserResponse response = new UserResponse("Пользователь сохранен под id = " + user.getId());
        return new ResponseEntity<>(EntityModel.of(
                response,
                links.create(true), links.readAll(false), links.read(user.getId(), false)),
                HttpStatus.OK);
    }

    @Operation(summary = "Получение пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public EntityModel<UserDTO> read(@PathVariable int id) {
        UserDTO userDTO = userService.read(id);

        return EntityModel.of(
                userDTO,
                links.read(id, true),
                links.delete(id,false),
                links.update(id,false));
    }

    @Operation(summary = "Получение всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "400", description = "Пользователи не найдены")
    })
    @GetMapping("")
    public CollectionModel<EntityModel<UserDTO>> readAll() {
        List<EntityModel<UserDTO>> users = userService.readAll().stream()
                .map(u -> EntityModel.of(u, links.read(u.getId(), true)))
                .toList();
        return CollectionModel.of(
                users,
                links.readAll(true));
    }

    @Operation(summary = "Обновление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "400", description = "Пользователь не обновлен")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> update(@PathVariable int id,
                                               @RequestBody @Valid UserDTO userDTO,
                                               BindingResult bindingResult) {
        userDTO.setId(id);
        validator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            UserResponse response = new UserResponse("Пользователь не обновлен: " + fieldErrorsToString(bindingResult));

            return new ResponseEntity<>(EntityModel.of(
                    response,
                    links.update(id, true), links.read(id, false), links.readAll(false)),
                    HttpStatus.BAD_REQUEST);
        }

        UserDTO user = userService.update(id, userDTO);
        UserResponse response = new UserResponse("Пользователь с id = " + user.getId() + " обновлен.");

        return new ResponseEntity<>(EntityModel.of(
                response,
                links.update(user.getId(), true), links.read(id, false), links.readAll(false)),
                HttpStatus.OK);
    }

    @Operation(summary = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь удален"),
            @ApiResponse(responseCode = "400", description = "Пользователь для удаления не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> delete(@PathVariable int id) {
        userService.delete(id);
        UserResponse response = new UserResponse("Пользователь с id = " + id + " удален.");

        return new ResponseEntity<>(EntityModel.of(
                response,
                links.readAll(false)),
                HttpStatus.OK);
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
