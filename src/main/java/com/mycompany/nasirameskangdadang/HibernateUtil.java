package com.mycompany.nasirameskangdadang;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory factory = new Configuration()
        .setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
        .setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/nasirameskangdadang")
        .setProperty("hibernate.connection.username", "root")
        .setProperty("hibernate.connection.password", "")
        .setProperty("hibernate.hbm2ddl.auto", "validate")
        .setProperty("hibernate.show_sql", "false")
        .addAnnotatedClass(Menu.class)
        .addAnnotatedClass(Bahan.class)
        .buildSessionFactory();

    public static SessionFactory getFactory() {
        return factory;
    }
}