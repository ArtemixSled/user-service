package org.example.dao;

import org.example.model.User;
import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link User}.
 * Определяет CRUD-операции и метод получения всех пользователей.
 */
public interface UserDao {

    /**
     * Создаёт нового пользователя.
     *
     * @param user пользователь для сохранения
     * @return идентификатор созданного пользователя
     */
    Long create(User user);

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь или {@code null}, если не найден
     */
    User read(Long id);

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param user пользователь с обновлёнными данными
     */
    void update(User user);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     */
    void delete(Long id);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> findAll();
}
