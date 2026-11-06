
package com.mycompany.nasirameskangdadang;

import java.util.List;
import org.hibernate.Session;

public class DataDAO {

    public static List<Menu> getMenu() {
        try (Session s = HibernateUtil.getFactory().openSession()) {
            return s.createQuery("from Menu", Menu.class).getResultList();
        }
    }

    public static List<Bahan> getBahan() {
        try (Session s = HibernateUtil.getFactory().openSession()) {
            return s.createQuery("from Bahan", Bahan.class).getResultList();
        }
    }
}
