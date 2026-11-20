🍛 Nasi Rames Kang Dadang

Aplikasi Kasir dan Manajemen Stok Berbasis Java Desktop

Project ini dikembangkan untuk memenuhi tugas Praktikum Rekayasa Perangkat Lunak 2 (RPL 2) Universitas Gunadarma.

Informasi Mahasiswa

Nama: Fitra Naufal Eka Bayu W.

NPM: 50423515

Kelas: 4IA21

Mata Kuliah: Praktikum Rekayasa Perangkat Lunak 2

Deskripsi Project

Nasi Rames Kang Dadang merupakan aplikasi desktop berbasis Java Swing yang dirancang untuk membantu pengelolaan menu makanan, persediaan bahan, transaksi kasir, pembayaran, dan riwayat penjualan.

Aplikasi menggunakan MySQL sebagai database, Hibernate sebagai ORM, dan Spring Framework untuk Dependency Injection. Pada pertemuan 6, aplikasi mulai dikembangkan dengan JasperReports untuk menghasilkan laporan penjualan.

Teknologi yang Digunakan

Java Swing — Antarmuka aplikasi desktop

Apache NetBeans — IDE pengembangan

Maven — Manajemen dependency

MySQL & Laragon — Database

JDBC — Koneksi dan transaksi database

Hibernate ORM — Pengelolaan entity database

Spring Framework — Dependency Injection

Jaspersoft Studio 6.21.5 — Desain laporan

JasperReports — Pembuatan dan penampilan laporan

Git & GitHub — Version control

Fitur Aplikasi

Dashboard: Menampilkan ringkasan penjualan, menu, stok bahan, dan transaksi harian.

Data Menu: Menampilkan daftar nasi rames beserta gambar, deskripsi, dan harga.

Stok Bahan: Menampilkan persediaan Beras, Ayam, Telur, Tempe, dan Sayur.

Tambah Stok: Menambahkan stok langsung dari aplikasi menggunakan tombol plus dan minus.

Transaksi Kasir: Memilih menu, menentukan jumlah pesanan, dan menghitung total pembayaran.

Pembayaran: Mencatat transaksi yang berhasil dibayar.

Riwayat Transaksi: Menampilkan transaksi dengan status LUNAS.

Laporan Penjualan: Pengembangan template dan integrasi JasperReports (M6, dalam proses).

Riwayat Pengembangan

M2 — Pembuatan Antarmuka

Membuat struktur awal aplikasi Java Swing.

Membuat sidebar dan navigasi halaman.

Menyiapkan Dashboard, Data Menu, Stok Bahan, Transaksi Kasir, dan Riwayat Transaksi.

M3 — Integrasi Database

Menghubungkan aplikasi dengan MySQL melalui JDBC.

Menampilkan data menu dan stok bahan.

Mengembangkan transaksi kasir dan perhitungan pembayaran.

Menyimpan transaksi dan menampilkan riwayat pembayaran.

M4 — Implementasi Hibernate ORM

Menambahkan dependency Hibernate.

Membuat entity Menu.java dan Bahan.java.

Membuat HibernateUtil.java dan DataDAO.java.

Mengintegrasikan pengambilan data menu dan bahan menggunakan Hibernate.

Menyempurnakan tampilan Data Menu.

M5 — Implementasi Spring Framework

Menambahkan dependency Spring Context, Spring ORM, dan Spring TX.

Membuat applicationContext.xml, SpringUtil.java, dan BahanService.java.

Mengimplementasikan penambahan stok bahan melalui aplikasi.

Mengubah tampilan stok menjadi card bergambar.

Menambahkan indikator ketersediaan stok.

Menambahkan logo dan animasi ringan pada antarmuka.

Melakukan refactoring kode agar lebih ringkas dan mudah dikelola.

M6 — Implementasi JasperReports (Work in Progress)

Menyiapkan Jaspersoft Studio 6.21.5.

Membuat Data Adapter untuk koneksi MySQL.

Membuat template LaporanPenjualan.jrxml.

Mengatur query data transaksi yang berstatus LUNAS.

Mendesain tampilan laporan penjualan.

Menyiapkan integrasi laporan dengan aplikasi Java NetBeans.

Status M6: Pengembangan masih berlangsung. Pengujian preview laporan melalui aplikasi NetBeans dan penyempurnaan desain akan dilanjutkan pada tahap berikutnya.

Struktur Project

NasiRamesKangDadang/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/mycompany/nasirameskangdadang/
    │   ├── Main.java
    │   ├── DataPanel.java
    │   ├── Koneksi.java
    │   ├── Menu.java
    │   ├── Bahan.java
    │   ├── DataDAO.java
    │   ├── HibernateUtil.java
    │   ├── SpringUtil.java
    │   ├── BahanService.java
    │   └── LaporanService.java
    └── resources/
        ├── applicationContext.xml
        ├── images/
        └── reports/
            └── LaporanPenjualan.jrxml

Catatan: Struktur di atas merupakan struktur target M6. File laporan dan class integrasi perlu dipastikan sudah disalin ke project sebelum tahap pengujian.

Rencana Pengembangan Selanjutnya

Menyelesaikan integrasi JasperReports dengan NetBeans.

Menampilkan preview laporan penjualan.

Menambahkan fitur cetak dan ekspor laporan PDF.

Menyempurnakan desain laporan dengan identitas Nasi Rames Kang Dadang.

Melakukan pengujian fitur laporan menggunakan data transaksi MySQL.
