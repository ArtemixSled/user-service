/**
 * Утилитный класс для управления жизненным циклом Hibernate SessionFactory.
 * <p>
 * Инициализирует SessionFactory из hibernate.cfg.xml один раз при запуске приложения
 * и предоставляет методы для получения и закрытия SessionFactory.
 */
package org.example.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.HibernateException;


public class HibernateUtil {
    /**
     * Единственный экземпляр SessionFactory, инициализируется при загрузке класса.
     */
    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (HibernateException ex) {
            System.err.println("Ошибка при инициализации SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Возвращает синглтон SessionFactory.
     *
     * @return экземпляр SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
