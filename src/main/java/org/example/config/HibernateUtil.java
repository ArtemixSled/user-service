/**
 * Утилитный класс для управления жизненным циклом Hibernate SessionFactory.
 * <p>
 * Инициализирует SessionFactory из hibernate.cfg.xml один раз при запуске приложения
 * и предоставляет методы для получения и закрытия SessionFactory.
 */
package org.example.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.Properties;

public class HibernateUtil {
    /**
     * Единственный экземпляр SessionFactory, инициализируется при загрузке класса.
     */
    private static SessionFactory sessionFactory;

    static {
        try {
            Dotenv dotenv = Dotenv.load();

            Configuration cfg = new Configuration();
            cfg.setProperty("hibernate.connection.url", dotenv.get("DB_URL"));
            cfg.setProperty("hibernate.connection.username", dotenv.get("DB_USER"));
            cfg.setProperty("hibernate.connection.password", dotenv.get("DB_PASS"));
            cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            cfg.setProperty("hibernate.hbm2ddl.auto", "update");
            cfg.setProperty("hibernate.show_sql", "true");

            cfg.addAnnotatedClass(User.class);

            sessionFactory = cfg.buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Ошибка инициализации Hibernate: " + e.getMessage());
        }
    }

    public static SessionFactory buildSessionFactoryWithProperties(Properties props) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        Configuration cfg = new Configuration();
        cfg.setProperties(props);
        cfg.addAnnotatedClass(User.class);
        sessionFactory = cfg.buildSessionFactory();
        return sessionFactory;
    }

    /**
     * Возвращает синглтон SessionFactory.
     *
     * @return экземпляр SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
