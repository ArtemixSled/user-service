/**
 * Консольное приложение для управления сущностью User через Hibernate.
 * <p>
 * Предоставляет текстовый интерфейс для выполнения операций создания, чтения,
 * обновления и удаления пользователей.
 */
package org.example;

import org.example.config.HibernateUtil;
import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.dao.DataAccessException;
import org.example.model.User;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private static final Scanner scan = new Scanner(System.in);
    private static final UserDao dao = new UserDaoImpl();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Точка входа в приложение.
     * Запускает цикл обработки команд до ввода команды выхода.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        while (true) {
            printMenu();
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" -> create();
                case "2" -> read();
                case "3" -> update();
                case "4" -> delete();
                case "5" -> listAll();
                case "0" -> { shutdown(); return; }
                default  -> System.out.println("Неверный ввод.");
            }
        }
    }

    /**
     * Выводит меню доступных команд пользователю.
     */
    private static void printMenu() {
        System.out.println("\n=== User Service ===");
        System.out.println("1) Создать пользователя");
        System.out.println("2) Прочитать по ID");
        System.out.println("3) Обновить пользователя");
        System.out.println("4) Удалить по ID");
        System.out.println("5) Список всех");
        System.out.println("0) Выход");
        System.out.print("Выбор: ");
    }

    /**
     * Создаёт нового пользователя на основе данных, введённых в консоли.
     * Выполняет валидацию и сохраняет объект через UserDao.
     */
    private static void create() {
        try {
            User u = new User();
            System.out.print("Имя: ");    u.setName(scan.nextLine());
            System.out.print("Email: ");  u.setEmail(scan.nextLine());
            System.out.print("Возраст: ");u.setAge(Integer.parseInt(scan.nextLine()));
            Long id = dao.create(u);
            System.out.println("Пользователь создан, id=" + id);
        } catch (IllegalArgumentException e) {
            System.err.println("Неверный ввод: " + e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("Ошибка работы с данными: " + e.getMessage());
        }
    }

    /**
     * Читает ID пользователя из консоли, загружает запись и выводит её на экран.
     */
    private static void read() {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(scan.nextLine());
            User u = dao.read(id);
            if (u != null) {
                System.out.printf("▶ %d: %s, %s, %d, %s%n",
                        u.getId(), u.getName(), u.getEmail(), u.getAge(), FMT.format(u.getCreatedAt()));
            } else {
                System.out.println("Пользователь не найден.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Неверный ввод ID: " + e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("Ошибка работы с данными: " + e.getMessage());
        }
    }

    /**
     * Обновляет существующего пользователя.
     * Запрашивает новое имя, email и возраст, затем сохраняет изменения.
     */
    private static void update() {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(scan.nextLine());
            User u = dao.read(id);
            if (u == null) {
                System.out.println("Нет такого ID.");
                return;
            }
            System.out.print("Новое имя (" + u.getName() + "): ");
            String name = scan.nextLine();
            if (!name.isBlank()) u.setName(name);
            System.out.print("Новый email (" + u.getEmail() + "): ");
            String email = scan.nextLine();
            if (!email.isBlank()) u.setEmail(email);
            System.out.print("Новый возраст (" + u.getAge() + "): ");
            String age = scan.nextLine();
            if (!age.isBlank()) u.setAge(Integer.parseInt(age));
            dao.update(u);
            System.out.println("Обновлено.");
        } catch (NumberFormatException e) {
            System.err.println("Неверный ввод: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Неверный ввод: " + e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("Ошибка работы с данными: " + e.getMessage());
        }
    }

    /**
     * Удаляет пользователя по введённому ID.
     */
    private static void delete() {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(scan.nextLine());
            dao.delete(id);
            System.out.println("Удалено.");
        } catch (NumberFormatException e) {
            System.err.println("Неверный ввод ID: " + e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("Ошибка работы с данными: " + e.getMessage());
        }
    }

    /**
     * Получает и выводит список всех пользователей.
     */
    private static void listAll() {
        try {
            List<User> all = dao.findAll();
            if (all.isEmpty()) {
                System.out.println("Нет ни одного пользователя.");
            } else {
                all.forEach(u -> System.out.printf("%d: %s, %s, %d, %s%n",
                        u.getId(), u.getName(), u.getEmail(), u.getAge(), FMT.format(u.getCreatedAt())));
            }
        } catch (DataAccessException e) {
            System.err.println("Ошибка работы с данными: " + e.getMessage());
        }
    }

    /**
     * Завершает работу приложения, закрывая SessionFactory Hibernate.
     */
    private static void shutdown() {
        System.out.println("Завершение работы...");
        HibernateUtil.shutdown();
    }
}
