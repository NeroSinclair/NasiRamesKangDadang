
package com.mycompany.nasirameskangdadang;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class BahanService {

    private final SessionFactory factory;

    public BahanService(SessionFactory factory) {
        this.factory=factory;
    }

    public void tambahBahan(String nama,int stok,String satuan) {
        try(Session s=factory.openSession()) {
            Transaction tx=s.beginTransaction();

            try {
                s.persist(new Bahan(nama,stok,satuan));
                tx.commit();
            } catch(RuntimeException e) {
                if(tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public void tambahStok(int id,int jumlah) {

        System.out.println("=== TEST TAMBAH STOK M5 ===");
        System.out.println("ID Bahan: "+id);
        System.out.println("Jumlah tambahan: "+jumlah);

        if(jumlah<=0)
            throw new IllegalArgumentException(
                "Jumlah harus lebih dari 0!");

        try(Session s=factory.openSession()) {
            Transaction tx=s.beginTransaction();

            try {
                int awal=s.doReturningWork(c->bacaStok(c,id));

                System.out.println("Stok awal: "+awal);

                int hasil=s.createMutationQuery(
                    "UPDATE Bahan b SET b.stok=b.stok+:jumlah "+
                    "WHERE b.idBahan=:id")
                    .setParameter("jumlah",jumlah)
                    .setParameter("id",id)
                    .executeUpdate();

                System.out.println("Data diperbarui: "+hasil);

                if(hasil!=1)
                    throw new IllegalStateException(
                        "Data bahan tidak ditemukan!");

                int akhir=s.doReturningWork(c->bacaStok(c,id));

                System.out.println("Stok setelah UPDATE: "+akhir);

                tx.commit();

                System.out.println("COMMIT BERHASIL!");

            } catch(RuntimeException e) {
                if(tx.isActive()) tx.rollback();
                e.printStackTrace();
                throw e;
            }
        }

        // Cek ulang menggunakan koneksi JDBC aplikasi
        try(Connection c=Koneksi.getConnection()) {

            int terbaru=bacaStok(c,id);

            System.out.println(
                "Stok terbaru dari MySQL: "+terbaru);

        } catch(Exception e) {
            System.out.println("Gagal memeriksa ulang stok.");
            e.printStackTrace();
        }
    }

    private int bacaStok(Connection c,int id)
            throws SQLException {

        System.out.println("Database: "+c.getCatalog());
        System.out.println("URL: "+c.getMetaData().getURL());

        try(var p=c.prepareStatement(
                "SELECT stok FROM bahan WHERE id_bahan=?")) {

            p.setInt(1,id);

            try(var r=p.executeQuery()) {
                if(r.next()) return r.getInt("stok");
            }
        }

        throw new SQLException("Bahan tidak ditemukan!");
    }
}
