package ru.practice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
