
package com.mycompany.nasirameskangdadang;

import jakarta.persistence.*;

@Entity
@Table(name="bahan")
public class Bahan {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_bahan")
    private int idBahan;

    @Column(name="nama_bahan")
    private String namaBahan;

    private int stok;
    private String satuan;

    public Bahan() {}

    public Bahan(String nama,int stok,String satuan) {
        this.namaBahan=nama;
        this.stok=stok;
        this.satuan=satuan;
    }

    public int getIdBahan() { return idBahan; }
    public String getNamaBahan() { return namaBahan; }
    public int getStok() { return stok; }
    public String getSatuan() { return satuan; }

    public void setNamaBahan(String nama) { this.namaBahan=nama; }
    public void setStok(int stok) { this.stok=stok; }
    public void setSatuan(String satuan) { this.satuan=satuan; }
}
