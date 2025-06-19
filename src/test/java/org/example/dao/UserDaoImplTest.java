package org.example.dao;


import org.example.config.HibernateUtil;
import org.example.model.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для {@link UserDaoImpl} с использованием Testcontainers и PostgreSQL.
 */
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoImplTest {

    /**
     * Контейнер PostgreSQL для изолированного тестирования.
     */
    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private static UserDao dao;

    /**
     * Инициализация Hibernate и DAO перед запуском тестов.
     */
    @BeforeAll
    static void setup() {
        Properties props = new Properties();
        props.put("hibernate.connection.url", POSTGRES.getJdbcUrl());
        props.put("hibernate.connection.username", POSTGRES.getUsername());
        props.put("hibernate.connection.password", POSTGRES.getPassword());
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "create-drop");

        HibernateUtil.buildSessionFactoryWithProperties(props);

        dao = new UserDaoImpl();
    }

    /**
     * Тест на создание и чтение пользователя из базы.
     */
    @Test
    @Order(1)
    void createAndReadUser() {
        User u = new User();
        u.setName("Макс");
        u.setEmail("Maks@test.ru");
        u.setAge(30);

        Long id = dao.create(u);
        assertNotNull(id);

        User fetched = dao.read(id);
        assertEquals("Макс", fetched.getName());
    }

    /**
     * Тест на обновление существующего пользователя.
     */
    @Test
    @Order(2)
    void updateUser() {
        User u = new User();
        u.setName("Петр");
        u.setEmail("petr@test.ru");
        u.setAge(25);
        Long id = dao.create(u);

        u.setId(id);
        u.setName("Петя");
        dao.update(u);

        User updated = dao.read(id);
        assertEquals("Петя", updated.getName());
    }

    /**
     * Тест на удаление пользователя и проверку, что он больше не читается.
     */
    @Test
    @Order(3)
    void deleteUser() {
        User u = new User();
        u.setName("Удаляемый");
        u.setEmail("delete@test.ru");
        u.setAge(40);
        Long id = dao.create(u);

        dao.delete(id);
        assertNull(dao.read(id));
    }
}