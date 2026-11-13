
package com.mycompany.nasirameskangdadang;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtil {

    private static final ClassPathXmlApplicationContext context;

    static {
        System.out.println("=== MEMUAT SPRING ===");

        System.out.println("Lokasi XML: "+
            SpringUtil.class.getResource("/applicationContext.xml"));

        context=new ClassPathXmlApplicationContext(
            "applicationContext.xml");

        System.out.println("Spring berhasil dijalankan!");
    }

    public static BahanService getBahanService() {
        return context.getBean("bahanService",BahanService.class);
    }
}
