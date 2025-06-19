package org.example.dao;

import org.example.model.User;
import org.example.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Проверяет, что create возвращает корректный ID и вызывает DAO.
     */
    @Test
    void testCreate() {
        User user = new User();
        user.setName("Тест");
        user.setEmail("test@mail.ru");
        user.setAge(20);

        when(userDao.create(user)).thenReturn(1L);

        Long id = userService.create(user);

        assertEquals(1L, id);
        verify(userDao).create(user);
    }

    /**
     * Проверяет, что read возвращает нужного пользователя по ID.
     */
    @Test
    void testRead() {
        User u = new User();
        u.setId(10L);
        when(userDao.read(10L)).thenReturn(u);

        User res = userService.read(10L);

        assertNotNull(res);
        assertEquals(10L, res.getId());
    }

    /**
     * Проверяет, что update вызывает соответствующий метод DAO.
     */
    @Test
    void testUpdate() {
        User user = new User();
        user.setId(1L);
        user.setName("Старое имя");
        user.setEmail("old@mail.com");
        user.setAge(30);

        doNothing().when(userDao).update(user);

        userService.update(user);

        verify(userDao, times(1)).update(user);
    }

    /**
     * Проверяет, что delete корректно вызывает DAO с нужным ID.
     */
    @Test
    void testDelete() {
        Long userId = 1L;

        doNothing().when(userDao).delete(userId);

        userService.delete(userId);

        verify(userDao, times(1)).delete(userId);
    }

    /**
     * Проверяет, что findAll возвращает полный список пользователей.
     */
    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Артем");
        user1.setEmail("artem@mail.ru");
        user1.setAge(25);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Иван");
        user2.setEmail("ivan@yandex.ru");
        user2.setAge(30);

        List<User> users = List.of(user1, user2);

        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Артем", result.get(0).getName());
        assertEquals("Иван", result.get(1).getName());

        verify(userDao, times(1)).findAll();
    }
}
