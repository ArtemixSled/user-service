/**
 * Реализация интерфейса UserDao для управления сущностью User с помощью Hibernate.
 * <p>
 * Методы класса обеспечивают операции создания, чтения, обновления, удаления и поиска всех пользователей,
 * а также обёртывают каждую операцию в транзакцию Hibernate и выполняют валидацию входных данных.
 */
package org.example.dao;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.example.config.HibernateUtil;
import org.example.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Set;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private static final ValidatorFactory VF = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VF.getValidator();

    /**
     * Сохраняет нового пользователя в базу данных.
     * <p>
     * Выполняет валидацию полей user перед сохранением.
     * Открывает сессию и транзакцию, сохраняет объект и фиксирует изменения.
     *
     * @param user объект User для сохранения
     *
     * @return идентификатор созданного пользователя
     *
     * @throws DataAccessException при ошибке сохранения или валидации
     */
    @Override
    public Long create(User user) {
        Set<ConstraintViolation<User>> violations = VALIDATOR.validate(user);
        if (!violations.isEmpty()) {
            violations.forEach(v ->
                    logger.warn("Ошибка валидации: поле '{}' — {}", v.getPropertyPath(), v.getMessage())
            );
            throw new IllegalArgumentException("Валидация не пройдена");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Long id = (Long) session.save(user);
            tx.commit();
            return id;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка создания пользователя", e);
            throw new DataAccessException("Не удалось создать пользователя", e);
        }
    }

    /**
     * Выполняет поиск пользователя по идентификатору.
     * <p>
     * Открывает сессию, загружает объект и возвращает его.
     *
     * @param id идентификатор пользователя
     * @return объект User или null, если не найден
     * @throws DataAccessException при ошибке доступа к данным
     */
    @Override
    public User read(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            tx.commit();
            return user;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка чтения пользователя с id=" + id, e);
            throw new DataAccessException("Не удалось получить пользователя", e);
        }
    }

    /**
     * Обновляет данные существующего пользователя.
     * <p>
     * Открывает сессию и транзакцию, выполняет merge и фиксирует изменения.
     *
     * @param user объект User с обновлёнными полями
     * @throws DataAccessException при ошибке обновления или валидации
     */
    @Override
    public void update(User user) {
        Set<ConstraintViolation<User>> violations = VALIDATOR.validate(user);
        if (!violations.isEmpty()) {
            violations.forEach(v ->
                    logger.warn("Ошибка валидации при обновлении: поле '{}' — {}", v.getPropertyPath(), v.getMessage())
            );
            throw new IllegalArgumentException("Валидация не пройдена");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка обновления пользователя с id=" + user.getId(), e);
            throw new DataAccessException("Не удалось обновить пользователя", e);
        }
    }

    /**
     * Удаляет пользователя из базы по идентификатору.
     * <p>
     * Открывает сессию и транзакцию, выполняет удаление и фиксирует изменения.
     *
     * @param id идентификатор удаляемого пользователя
     * @throws DataAccessException при ошибке удаления
     */
    @Override
    public void delete(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User u = session.get(User.class, id);
            if (u != null) {
                session.delete(u);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка удаления пользователя с id=" + id, e);
            throw new DataAccessException("Не удалось удалить пользователя", e);
        }
    }

    /**
     * Возвращает список всех пользователей из базы данных.
     * <p>
     * Открывает сессию, выполняет HQL-запрос и возвращает результат.
     *
     * @return список объектов User
     * @throws DataAccessException при ошибке выполнения запроса
     */
    @Override
    public List<User> findAll() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            List<User> list = session.createQuery("from User", User.class).list();
            tx.commit();
            return list;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Ошибка получения списка пользователей", e);
            throw new DataAccessException("Не удалось получить список пользователей", e);
        }
    }
}
