package ru.practice.springuserservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;
import ru.practice.springuserservice.controllers.UserController;
import ru.practice.springuserservice.dto.UserDTO;
import ru.practice.springuserservice.services.UserService;
import ru.practice.springuserservice.util.UserDTOValidator;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDTOValidator validator;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    void testCreate_whenOk() throws Exception {
        UserDTO userDTO = new UserDTO(0, "test", "test@gmail.com", 59);
        UserDTO userDTOReturn = new UserDTO(1, "test", "test@gmail.com", 59);

        doNothing().when(validator).validate(any(), any(Errors.class));
        when(userService.create(any())).thenReturn(userDTOReturn);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate_whenInvalidFieldData() throws Exception {
        UserDTO userDTO = new UserDTO(0, "test", "test@gmail.com", 69);

        doAnswer(invocationOnMock -> {
            Object obj = invocationOnMock.getArgument(0);
            Errors errors = invocationOnMock.getArgument(1);

            errors.rejectValue("email", "", "Пользователь с таким email уже существует.");
            return null;
        }).when(validator).validate(any(), any(Errors.class));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_whenInternalServerError() throws Exception {
        UserDTO userDTO = new UserDTO(0, "test", "test@gmail.com", 69);

        //doThrow(new DataIntegrityViolationException("DB fail")).when(userService.create(any()));
        when(userService.create(any())).thenThrow(new JDBCException("", new SQLException()));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRead_whenOk() throws Exception {
        UserDTO user = new UserDTO(1, "test", "test@gmail.com", 43);

        when(userService.read(1)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.age").value(user.getAge()));
    }

    @Test
    void testRead_whenUserNotFound() throws Exception {
        when(userService.read(1)).thenThrow(new EntityNotFoundException("В базе данных нет пользователей."));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("В базе данных нет пользователей."));
    }

    @Test
    void testUpdate_whenOk() throws Exception {
        UserDTO user = new UserDTO(1, "test", "test@gmail.com", 43);

        doNothing().when(validator).validate(any(), any(Errors.class));
        when(userService.update(eq(user.getId()), any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(patch("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь с id = " + user.getId() + " обновлен."));
    }

    @Test
    void testUpdate_whenInvalidFieldData() throws Exception {
        UserDTO userDTO = new UserDTO(1, "test", "test@gmail.com", 26);

        doAnswer(invocationOnMock -> {
            Object obj = invocationOnMock.getArgument(0);
            Errors errors = invocationOnMock.getArgument(1);

            errors.rejectValue("email", "", "Пользователь с таким email уже существует.");
            return null;
        }).when(validator).validate(any(), any(Errors.class));

        mockMvc.perform(patch("/api/users/" + userDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDelete_whenOk() throws Exception {
        int id = 1;

        doNothing().when(userService).delete(id);

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь с id = " + id + " удален."));
    }

    @Test
    void testDelete_whenUserNotFound() throws Exception {
        int id = 1;

        doThrow(new EntityNotFoundException("Пользователь для удаления не найден")).when(userService).delete(id);

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь для удаления не найден"));
    }
}
