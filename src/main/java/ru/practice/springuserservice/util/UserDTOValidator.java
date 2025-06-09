package ru.practice.springuserservice.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.models.User;
import ru.practice.springuserservice.repositories.UserRepository;

import java.util.Optional;

@Component
public class UserDTOValidator implements Validator {

    private final UserRepository userRepository;

    public UserDTOValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO user = (UserDTO) target;

        Optional<User> checkUser = userRepository.findByEmail(user.getEmail());
        if (checkUser.isPresent() && user.getId() != checkUser.get().getId()) {
            errors.rejectValue("email", "", "Пользователь с таким email уже существует.");
        }
    }
}
