package com.mycompany.nasirameskangdadang;

import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DataPanel extends JPanel {

    static final Color BG=new Color(248,248,246), RED=new Color(150,35,35);
    static final DefaultTableModel cart=new DefaultTableModel(
        new String[]{"Menu","Qty","Subtotal"},0);
    static int total=0;

    public DataPanel(String page) {
        setLayout(new BorderLayout(0,18)); setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(28,32,28,32));

        JLabel t=new JLabel(page);
        t.setFont(new Font("SansSerif",Font.BOLD,28));
        add(t,BorderLayout.NORTH);

        switch(page) {
            case "Dashboard" -> dashboard();
            case "Data Menu" -> menu();
            case "Stok Bahan" -> stok();
            case "Transaksi Kasir" -> transaksi();
            default -> riwayat();
        }
    }

    void dashboard() {
        JPanel p=grid(2,2);
        p.add(card("TOTAL PENJUALAN",rupiah(angka(
            "SELECT COALESCE(SUM(total),0) FROM transaksi WHERE status='LUNAS'"))));
        p.add(card("MENU TERSEDIA",angka("SELECT COUNT(*) FROM menu")+" Menu"));
        p.add(card("STOK BAHAN",angka("SELECT COUNT(*) FROM bahan")+" Bahan"));
        p.add(card("TRANSAKSI HARI INI",angka(
            "SELECT COUNT(*) FROM transaksi WHERE status='LUNAS' "+
            "AND DATE(tanggal)=CURDATE()")+" Transaksi"));
        add(p);
    }

    JPanel card(String a,String b) {
        JPanel p=box(); JLabel x=new JLabel(a),y=new JLabel(b);
        x.setForeground(Color.GRAY); y.setForeground(RED);
        y.setFont(new Font("SansSerif",Font.BOLD,24));
        p.add(x); p.add(Box.createVerticalStrut(10)); p.add(y);
        return p;
    }

    void menu() {
        JPanel p=grid(0,3);

        try(var c=Koneksi.getConnection(); var s=c.createStatement();
            var r=s.executeQuery("SELECT * FROM menu")) {
            while(r.next()) {
                String n=r.getString("nama_menu");
                JPanel m=box(); m.setLayout(new BorderLayout(0,8));
                m.add(image(gambar(n)));
                m.add(new JLabel("<html><b>"+n+"</b><br><font color='#962323'>"+
                    rupiah(r.getInt("harga"))+"</font></html>"),BorderLayout.SOUTH);
                p.add(m);
            }
        } catch(Exception e) { error(e); }

        add(scroll(p));
    }

    void stok() {
        DefaultTableModel m=new DefaultTableModel(
            new String[]{"ID","Bahan","Stok","Satuan"},0);

        try(var c=Koneksi.getConnection(); var s=c.createStatement();
            var r=s.executeQuery("SELECT * FROM bahan")) {
            while(r.next()) m.addRow(new Object[]{
                r.getInt("id_bahan"),r.getString("nama_bahan"),
                r.getInt("stok"),r.getString("satuan")});
        } catch(Exception e) { error(e); }

        JTable t=new JTable(m); table(t); add(scroll(t));
    }

    void transaksi() {
        JTable t=new JTable(cart); table(t);
        JPanel f=box(); f.setLayout(new BorderLayout());

        JLabel sum=new JLabel("Total  "+rupiah(total));
        sum.setFont(new Font("SansSerif",Font.BOLD,22));

        JPanel btn=new JPanel();
        JButton add=button("+ Tambah Menu"), pay=button("Bayar");
        add.addActionListener(e->tambahMenu());
        pay.addActionListener(e->bayar());
        btn.add(add); btn.add(pay);

        f.add(sum); f.add(btn,BorderLayout.EAST);

        JPanel p=new JPanel(new BorderLayout(0,15)); p.setOpaque(false);
        p.add(scroll(t)); p.add(f,BorderLayout.SOUTH); add(p);
    }

    void tambahMenu() {
        String[] list=getMenu();
        if(list.length==0) return;

        JComboBox<String> menu=new JComboBox<>(list);
        JLabel foto=image(gambar(list[0])), info=center(""), qty=center("1");
        JButton min=button("−"), plus=button("+");

        JPanel q=new JPanel(new GridLayout(1,3,8,0));
        q.add(min); q.add(qty); q.add(plus);

        JPanel bawah=new JPanel(new BorderLayout());
        bawah.add(info); bawah.add(q,BorderLayout.SOUTH);

        JPanel p=new JPanel(new BorderLayout(8,8));
        p.add(menu,BorderLayout.NORTH); p.add(foto); p.add(bawah,BorderLayout.SOUTH);

        Runnable update=()->{
            String n=(String)menu.getSelectedItem();
            foto.setIcon(image(gambar(n)).getIcon());
            info.setText("<html><center><b>"+n+"</b><br>"+
                rupiah(harga(n))+"</center></html>");
        };

        update.run(); menu.addActionListener(e->update.run());
        plus.addActionListener(e->qty.setText(""+(Integer.parseInt(qty.getText())+1)));
        min.addActionListener(e->{int x=Integer.parseInt(qty.getText());
            if(x>1) qty.setText(""+(x-1));});

        int ok=JOptionPane.showConfirmDialog(this,p,"Tambah Pesanan",
            JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(ok!=JOptionPane.OK_OPTION) return;

        String n=(String)menu.getSelectedItem();
        int x=Integer.parseInt(qty.getText()), h=harga(n);

        cart.addRow(new Object[]{n,x,rupiah(h*x)});
        total+=h*x; refresh();
    }

    void bayar() {
        if(cart.getRowCount()==0) {
            JOptionPane.showMessageDialog(this,"Belum ada pesanan."); return;
        }

        int id=0;

        try(var c=Koneksi.getConnection()) {
            var t=c.prepareStatement(
                "INSERT INTO transaksi(total,metode_pembayaran,uang_bayar,"+
                "kembalian,status) VALUES(?,'Cash',?,0,'PENDING')",
                Statement.RETURN_GENERATED_KEYS);

            t.setInt(1,total); t.setInt(2,total); t.executeUpdate();
            var k=t.getGeneratedKeys(); if(k.next()) id=k.getInt(1);

            var d=c.prepareStatement(
                "INSERT INTO detail_transaksi(id_transaksi,id_menu,jumlah,"+
                "harga,subtotal) VALUES(?,?,?,?,?)");

            for(int i=0;i<cart.getRowCount();i++) {
                String n=cart.getValueAt(i,0).toString();
                int q=(int)cart.getValueAt(i,1), h=harga(n);

                d.setInt(1,id); d.setInt(2,idMenu(n)); d.setInt(3,q);
                d.setInt(4,h); d.setInt(5,h*q); d.addBatch();
            }
            d.executeBatch();

        } catch(Exception e) { error(e); return; }

        Object[] opsi={"Bayar"};
        int ok=JOptionPane.showOptionDialog(this,
            "Total yang harus dibayar:\n"+rupiah(total),
            "Pembayaran",JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,null,opsi,opsi[0]);

        if(ok!=0) return;

        try(var c=Koneksi.getConnection();
            var p=c.prepareStatement(
                "UPDATE transaksi SET status='LUNAS' WHERE id_transaksi=?")) {
            p.setInt(1,id); p.executeUpdate();
        } catch(Exception e) { error(e); return; }

        JOptionPane.showMessageDialog(this,"Pembayaran berhasil!");
        cart.setRowCount(0); total=0; refresh();
    }

    void riwayat() {
        DefaultTableModel m=new DefaultTableModel(
            new String[]{"ID","Tanggal","Total","Status"},0);

        try(var c=Koneksi.getConnection(); var s=c.createStatement();
            var r=s.executeQuery("SELECT * FROM transaksi "+
                "WHERE status='LUNAS' ORDER BY id_transaksi DESC")) {
            while(r.next()) m.addRow(new Object[]{
                r.getInt("id_transaksi"),r.getString("tanggal"),
                rupiah(r.getInt("total")),r.getString("status")});
        } catch(Exception e) { error(e); }

        JTable t=new JTable(m); table(t); add(scroll(t));
    }

    String[] getMenu() {
        ArrayList<String> x=new ArrayList<>();
        try(var c=Koneksi.getConnection(); var s=c.createStatement();
            var r=s.executeQuery("SELECT nama_menu FROM menu")) {
            while(r.next()) x.add(r.getString(1));
        } catch(Exception e) { error(e); }
        return x.toArray(String[]::new);
    }

    int harga(String n) {
        return menuValue(n,"harga");
    }

    int idMenu(String n) {
        return menuValue(n,"id_menu");
    }

    int menuValue(String n,String kolom) {
        try(var c=Koneksi.getConnection(); var p=c.prepareStatement(
            "SELECT "+kolom+" FROM menu WHERE nama_menu=?")) {
            p.setString(1,n); var r=p.executeQuery();
            if(r.next()) return r.getInt(1);
        } catch(Exception e) { error(e); }
        return 0;
    }

    int angka(String sql) {
        try(var c=Koneksi.getConnection(); var s=c.createStatement();
            var r=s.executeQuery(sql)) {
            if(r.next()) return r.getInt(1);
        } catch(Exception e) { error(e); }
        return 0;
    }

    JPanel box() {
        JPanel p=new JPanel(); p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        return p;
    }

    JPanel grid(int r,int c) {
        JPanel p=new JPanel(new GridLayout(r,c,16,16)); p.setOpaque(false);
        return p;
    }

    JButton button(String s) {
        JButton b=new JButton(s); b.setBackground(RED);
        b.setForeground(Color.WHITE); b.setFocusPainted(false); return b;
    }

    void table(JTable t) {
        t.setRowHeight(36); t.setShowVerticalLines(false);
    }

    JScrollPane scroll(Component c) {
        JScrollPane s=new JScrollPane(c); s.setBorder(null); return s;
    }

    JLabel image(String f) {
        var u=getClass().getResource("/images/"+f);
        if(u==null) return center("No Image");
        Image i=new ImageIcon(u).getImage()
            .getScaledInstance(200,135,Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(i),SwingConstants.CENTER);
    }

    String gambar(String n) {
        n=n.toLowerCase();
        if(n.contains("ayam")) return "nasi_rames_ayam.jpg";
        if(n.contains("telur")) return "nasi_rames_telur.jpg";
        return "nasi_rames_tempe.jpg";
    }

    String rupiah(int n) {
        return "Rp"+NumberFormat.getNumberInstance(
            new Locale("id","ID")).format(n);
    }

    JLabel center(String s) {
        return new JLabel(s,SwingConstants.CENTER);
    }

    void refresh() {
        removeAll();
        JLabel j=new JLabel("Transaksi Kasir");
        j.setFont(new Font("SansSerif",Font.BOLD,28));
        add(j,BorderLayout.NORTH); transaksi();
        revalidate(); repaint();
    }

    void error(Exception e) {
        JOptionPane.showMessageDialog(this,e.getMessage());
    }
}