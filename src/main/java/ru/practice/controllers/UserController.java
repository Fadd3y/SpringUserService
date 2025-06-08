package ru.practice.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practice.dto.UserDTO;
import ru.practice.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void create(@RequestBody UserDTO userDTO) {

    }

    @GetMapping("/{id}")
    public void read(@PathVariable int id) {

    }

    @PatchMapping
    public void update(@RequestBody UserDTO userDTO) {

    }

    @DeleteMapping
    public void delete(@RequestBody UserDTO userDTO) {

    }
}
