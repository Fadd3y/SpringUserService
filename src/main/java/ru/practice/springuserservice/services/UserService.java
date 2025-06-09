package ru.practice.springuserservice.services;


import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.models.User;
import ru.practice.springuserservice.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public User create(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public UserDTO read(int id) {
        Optional<User> user  = userRepository.findById(id);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDTO.class);
        } else {
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    public List<UserDTO> readAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user -> modelMapper.map(user, UserDTO.class)))
                .toList();
    }

    @Transactional
    public User update(int id, UserDTO userDTO) {
        User userToBeUpdated = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь для обновления не найден"));

        userToBeUpdated.setName(userDTO.getName());
        userToBeUpdated.setEmail(userDTO.getEmail());
        userToBeUpdated.setAge(userDTO.getAge());

        return userToBeUpdated;
    }

    @Transactional
    public void delete(int id) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь для удаления не найден"));
        userRepository.deleteById(id);
    }
}
