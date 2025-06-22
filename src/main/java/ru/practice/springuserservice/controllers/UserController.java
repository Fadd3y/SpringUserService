package ru.practice.springuserservice.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.services.UserService;
import ru.practice.springuserservice.util.UserDTOValidator;

import java.util.List;

@Controller
@RequestMapping("")
public class UserController {

    private final UserService userService;
    private final UserDTOValidator validator;

    public UserController(UserService userService, UserDTOValidator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    @GetMapping("/homepage")
    public String startingPage() {
        return "homepage";
    }

    @GetMapping("/users")
    public String showAllUsersPage(Model model) {
        try {
            model.addAttribute("users", userService.readAll());
        } catch (EntityNotFoundException e) {
            model.addAttribute("users", List.of());
        }
        return "showAll";
    }

    @GetMapping("/users/{id}")
    public String showUserPage(@PathVariable int id, Model model) {
        try {
            model.addAttribute("user", userService.read(id));
        } catch (Exception e) {
            model.addAttribute("user", null);
        }
        return "show";
    }

    @GetMapping("/users/new")
    public String createUserForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        return "create";
    }

    @GetMapping("/users/{id}/update")
    public String updateUserPage(@PathVariable int id, Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", userService.read(id));
        } 

        return "update";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("user") @Valid UserDTO userDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        validator.validate(userDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", userDTO);
            return "redirect:/users/new";
        }

        userService.create(userDTO);
        return "redirect:/homepage";
    }

    @PatchMapping("/users/{id}")
    public String updateUser(@PathVariable int id,
                             @ModelAttribute("user") @Valid UserDTO userDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        userDTO.setId(id);
        validator.validate(userDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", userDTO);
            return "redirect:/users/{id}/update";
        }

        userService.update(id, userDTO);
        return "redirect:/users/{id}";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.delete(id);
        return "redirect:/users";
    }
}
