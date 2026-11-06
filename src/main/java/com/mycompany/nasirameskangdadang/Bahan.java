
package com.mycompany.nasirameskangdadang;

import jakarta.persistence.*;

@Entity
@Table(name = "bahan")
public class Bahan {

    @Id
    @Column(name = "id_bahan")
    private int idBahan;

    @Column(name = "nama_bahan")
    private String namaBahan;

    @Column(name = "stok")
    private int stok;

    @Column(name = "satuan")
    private String satuan;

    public Bahan() {}

    public int getIdBahan() { return idBahan; }
    public String getNamaBahan() { return namaBahan; }
    public int getStok() { return stok; }
    public String getSatuan() { return satuan; }
}
