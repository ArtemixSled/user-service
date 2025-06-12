package org.example.dao;

import org.example.model.User;
import java.util.List;

public interface UserDao {

    Long create(User user);

    User read(Long id);

    void update(User user);

    void delete(Long id);

    List<User> findAll();
}
