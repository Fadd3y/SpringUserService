package ru.practice.springuserservice.util;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import ru.practice.springuserservice.controllers.UserRESTController;
import ru.practice.springuserservice.dto.UserDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserDTOLinks {

    public Link create(boolean self) {
        return linkTo(methodOn(UserRESTController.class)
                .create(null, null))
                .withRel(self ? "self" : "create")
                .withType("POST");
    }

    public Link read(int id, boolean self) {
        return linkTo(methodOn(UserRESTController.class)
                .read(id))
                .withRel(self ? "self" : "read");
    }

    public Link readAll(boolean self) {
        return linkTo(methodOn(UserRESTController.class)
                .readAll())
                .withRel(self ? "self" : "all");
    }

    public Link update(int id, boolean self) {
        return linkTo(methodOn(UserRESTController.class)
                .update(id, new UserDTO(), null))
                .withRel(self ? "self" : "update")
                .withType("PUT");
    }

    public Link delete(int id, boolean self) {
        return linkTo(methodOn(UserRESTController.class)
                .delete(id))
                .withRel(self ? "self" : "delete")
                .withType("DELETE");
    }

}
