# 🍛 Nasi Rames Kang Dadang

Repository ini dibuat untuk memenuhi tugas **Praktikum Rekayasa Perangkat Lunak 2 (RPL 2)**.

## 👤 Identitas

**Nama:** Fitra Naufal Eka Bayu W.  
**NPM:** 50423515  
**Kelas:** 4IA21  
**Mata Kuliah:** Praktikum Rekayasa Perangkat Lunak 2

## 📌 Tentang Project

**Nasi Rames Kang Dadang** merupakan aplikasi yang dikembangkan sebagai project dalam Praktikum Rekayasa Perangkat Lunak 2.

Aplikasi ini dibuat menggunakan **Java** dan dikembangkan melalui **Apache NetBeans**.

## 🛠️ Teknologi yang Digunakan

Java — Bahasa pemrograman utama.

Java Swing — Pembuatan antarmuka aplikasi desktop.

Apache NetBeans — IDE pengembangan aplikasi.

MySQL & Laragon — Penyimpanan dan pengelolaan database.

JDBC — Koneksi dan pengolahan database secara langsung.

Hibernate ORM — Pemetaan tabel database menjadi objek Java.

Apache Maven — Pengelolaan dependency project.


## 📚 Progress

-  RPL M3 — Pengembangan Database dan Transaksi

    Pada pertemuan M3, kita melanjutkan pengembangan aplikasi dengan menambahkan beberapa fitur utama, yaitu:
     - Menghubungkan aplikasi Java dengan database MySQL menggunakan JDBC.
     - Membuat halaman Data Menu dan Stok Bahan.
     - Menambahkan fitur transaksi pembelian beserta keranjang pesanan.
     - Membuat perhitungan total pembayaran secara otomatis.
     - Menambahkan proses konfirmasi pembayaran.
     - Menyimpan data transaksi ke database dan menampilkan pembayaran yang berhasil pada halaman Riwayat Transaksi.

-  RPL M4 — Implementasi ORM Hibernate dan Pengembangan Tampilan

    Pada pertemuan M4, kita berfokus pada penerapan Object-Relational Mapping (ORM) menggunakan framework Hibernate untuk meningkatkan struktur pengolahan data pada aplikasi. Pengembangan yang dilakukan meliputi:
     - Menghubungkan aplikasi Java dengan database MySQL menggunakan JDBC.
     - Menambahkan dependency Hibernate ORM pada konfigurasi Maven (pom.xml).
     - Membuat class HibernateUtil.java untuk mengatur konfigurasi Hibernate dan koneksi ke MySQL.
     - Membuat Entity Menu.java dan Bahan.java untuk memetakan tabel database menjadi objek Java.
     - Membuat class DataDAO.java untuk mengambil data menu dan bahan menggunakan Hibernate Query Language (HQL).
     - Mengintegrasikan Hibernate ke halaman Data Menu dan Stok Bahan sebagai pengganti pengambilan data menggunakan JDBC manual pada kedua halaman tersebut.
     - Melakukan pengujian pengambilan data dari database melalui Hibernate.
     - Memperbarui tampilan Data Menu dengan menambahkan deskripsi makanan, informasi komposisi, dan kotak harga agar lebih informatif serta menarik.

-  RPL M5 — Implementasi Spring Framework dan Hibernate

    Pada pertemuan M5 dilakukan pengembangan aplikasi dengan mengimplementasikan Spring Framework dan Hibernate ORM untuk meningkatkan pengelolaan data serta efisiensi kode program.
    Pengembangan yang dilakukan meliputi:
     - Mengintegrasikan Spring Framework dan Hibernate dengan database MySQL.
     - Menambahkan konfigurasi applicationContext.xml.
     - Mengimplementasikan entity Bahan.java untuk pemetaan data bahan ke database.
     - Menambahkan BahanService.java dan SpringUtil.java untuk pengelolaan data dan konfigurasi Spring.
     - Mengembangkan fitur penambahan stok bahan secara langsung melalui aplikasi tanpa harus mengubah data secara manual di database.
     - Menambahkan pilihan bahan, tombol plus dan minus, serta preview jumlah stok akhir.
     - Memperbarui tampilan stok secara otomatis setelah perubahan berhasil disimpan ke database.
