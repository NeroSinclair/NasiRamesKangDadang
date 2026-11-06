
package com.mycompany.nasirameskangdadang;

import jakarta.persistence.*;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @Column(name = "id_menu")
    private int idMenu;

    @Column(name = "nama_menu")
    private String namaMenu;

    @Column(name = "harga")
    private int harga;

    public Menu() {}

    public int getIdMenu() { return idMenu; }
    public String getNamaMenu() { return namaMenu; }
    public int getHarga() { return harga; }
}
