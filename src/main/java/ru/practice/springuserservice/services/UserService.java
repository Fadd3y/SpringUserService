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
    public UserDTO create(UserDTO userDTO) {
        User user = convertDTOToUser(userDTO);
        user.setCreatedAt(LocalDateTime.now());
        return convertUserToDTO(userRepository.save(user)) ;
    }

    public UserDTO read(int id) {
        Optional<User> user  = userRepository.findById(id);
        if (user.isPresent()) {
            return convertUserToDTO(user.get());
        } else {
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    public List<UserDTO> readAll() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new EntityNotFoundException("В базе данных нет пользователей.");
        }

        return users.stream()
                .map((this::convertUserToDTO))
                .toList();
    }

    @Transactional
    public UserDTO update(int id, UserDTO userDTO) {
        User userToBeUpdated = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь для обновления не найден"));

        userToBeUpdated.setName(userDTO.getName());
        userToBeUpdated.setEmail(userDTO.getEmail());
        userToBeUpdated.setAge(userDTO.getAge());

        return convertUserToDTO(userToBeUpdated);
    }

    @Transactional
    public void delete(int id) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь для удаления не найден"));
        userRepository.deleteById(id);
    }

    private UserDTO convertUserToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertDTOToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
