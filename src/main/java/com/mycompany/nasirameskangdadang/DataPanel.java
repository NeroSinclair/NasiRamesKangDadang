
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
        new String[]{"Menu","Qty","Subtotal"},0) {
        public boolean isCellEditable(int r,int c) { return false; }
    };
    static int total=0;

    public DataPanel(String page) {
        setLayout(new BorderLayout(0,18));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(28,32,28,32));
        add(judul(page),BorderLayout.NORTH);

        switch(page) {
            case "Dashboard" -> dashboard();
            case "Data Menu" -> menu();
            case "Stok Bahan" -> stok();
            case "Transaksi Kasir" -> transaksi();
            default -> riwayat();
        }
    }

    JLabel judul(String s) {
        JLabel l=new JLabel(s);
        l.setFont(new Font("SansSerif",Font.BOLD,28));
        return l;
    }

    // ================= DASHBOARD =================

    void dashboard() {
        JPanel p=grid(2,2);

        p.add(card("TOTAL PENJUALAN",rupiah(angka(
            "SELECT COALESCE(SUM(total),0) FROM transaksi WHERE status='LUNAS'"))));

        p.add(card("MENU TERSEDIA",
            angka("SELECT COUNT(*) FROM menu")+" Menu"));

        p.add(card("STOK BAHAN",
            angka("SELECT COUNT(*) FROM bahan")+" Bahan"));

        p.add(card("TRANSAKSI HARI INI",angka(
            "SELECT COUNT(*) FROM transaksi WHERE status='LUNAS' "+
            "AND DATE(tanggal)=CURDATE()")+" Transaksi"));

        add(p);
    }

    JPanel card(String a,String b) {
        JPanel p=box();
        JLabel x=new JLabel(a),y=new JLabel(b);

        x.setForeground(Color.GRAY);
        y.setForeground(RED);
        y.setFont(new Font("SansSerif",Font.BOLD,24));

        p.add(x); jarak(p,10); p.add(y);
        return p;
    }

    // ================= DATA MENU =================

    void menu() {
        JPanel p=grid(0,3);

        try {
            for(Menu m:DataDAO.getMenu()) {
                String n=m.getNamaMenu();
                JPanel c=box(), isi=vertical();

                c.setLayout(new BorderLayout(0,15));

                JLabel foto=image(gambar(n));
                foto.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel nama=label(n,19,Color.BLACK);

                JTextArea desc=new JTextArea(deskripsi(n));
                desc.setFont(new Font("SansSerif",Font.PLAIN,13));
                desc.setForeground(Color.GRAY);
                desc.setLineWrap(true);
                desc.setWrapStyleWord(true);
                desc.setEditable(false);
                desc.setOpaque(false);
                desc.setFocusable(false);
                desc.setMaximumSize(new Dimension(260,65));

                String bahan=n.toLowerCase().contains("ayam")?"Ayam":
                    n.toLowerCase().contains("telur")?"Telur":"Tempe";

                isi.add(foto); jarak(isi,18);
                isi.add(nama); jarak(isi,12);
                isi.add(desc); jarak(isi,15);
                isi.add(new JSeparator()); jarak(isi,15);
                isi.add(label("Nasi • "+bahan+" • Sayur",12,RED));

                JPanel hargaBox=new JPanel(new BorderLayout());
                hargaBox.setBackground(new Color(250,235,235));
                hargaBox.setBorder(
                    BorderFactory.createEmptyBorder(15,10,15,10));

                hargaBox.add(label(rupiah(m.getHarga()),20,RED));

                c.add(isi,BorderLayout.NORTH);
                c.add(hargaBox,BorderLayout.SOUTH);
                p.add(c);
            }
        } catch(Exception e) { error(e); }

        add(scroll(p));
    }

    // ================= STOK BAHAN =================

    void stok() {
        JPanel daftar=new JPanel(new GridLayout(0,1,0,12));
        daftar.setOpaque(false);

        try {
            var list=DataDAO.getBahan().stream()
                .filter(b->Arrays.asList(
                    "beras","ayam","telur","tempe","sayur")
                    .contains(b.getNamaBahan().toLowerCase(Locale.ROOT)))
                .sorted(Comparator.comparingInt(Bahan::getIdBahan))
                .toList();

            JPanel baris=null;

            for(int i=0;i<list.size();i++) {
                if(i%3==0) {
                    baris=new JPanel(
                        new FlowLayout(FlowLayout.CENTER,12,0));

                    baris.setOpaque(false);
                    daftar.add(baris);
                }

                baris.add(cardStok(list.get(i)));
            }

        } catch(Exception e) { error(e); }

        JPanel wrapper=new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(daftar,BorderLayout.NORTH);

        add(scroll(wrapper));
    }

    JPanel cardStok(Bahan b) {
        JPanel c=new JPanel(new BorderLayout(0,8));
        c.setBackground(Color.WHITE);
        c.setPreferredSize(new Dimension(240,278));

        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,225,225)),
            BorderFactory.createEmptyBorder(12,14,12,14)));

        JPanel isi=vertical();

        String n=b.getNamaBahan();

        JLabel foto=image(gambarBahan(n),180,105);
        foto.setAlignmentX(Component.CENTER_ALIGNMENT);

        String status=b.getStok()>10?"Stok Aman":
            b.getStok()>0?"Stok Menipis":"Stok Habis";

        Color warna=b.getStok()>10?
            new Color(40,140,80):Color.RED;

        isi.add(foto); jarak(isi,10);
        isi.add(label(n,18,Color.BLACK)); jarak(isi,5);
        isi.add(label("Stok tersedia",12,Color.GRAY));
        isi.add(label(""+b.getStok(),28,RED)); jarak(isi,4);
        isi.add(label(status,12,warna));

        JButton tambah=button("+ Tambah Stok");
        tambah.setPreferredSize(new Dimension(200,34));
        tambah.addActionListener(e->inputStok(b));

        c.add(isi,BorderLayout.CENTER);
        c.add(tambah,BorderLayout.SOUTH);

        return c;
    }

    void inputStok(Bahan b) {
        JPanel p=vertical();

        JLabel foto=image(
            gambarBahan(b.getNamaBahan()),180,105);

        foto.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qty=label("1",26,RED);
        JLabel hasil=label(
            "Stok akhir: "+((long)b.getStok()+1),
            15,new Color(40,140,80));

        JButton min=button("−");
        JButton plus=button("+");

        min.setPreferredSize(new Dimension(50,38));
        plus.setPreferredSize(new Dimension(50,38));

        JPanel q=new JPanel(new GridLayout(1,3,10,0));
        q.setOpaque(false);
        q.setMaximumSize(new Dimension(230,42));
        q.add(min); q.add(qty); q.add(plus);

        Runnable update=()->{
            int x=Integer.parseInt(qty.getText());

            hasil.setText("Stok akhir: "+((long)b.getStok()+x));

            min.setEnabled(x>1);
            plus.setEnabled(x<10000);
        };

        plus.addActionListener(e->{
            int x=Integer.parseInt(qty.getText());

            if(x<10000) {
                qty.setText(""+(x+1));
                update.run();
            }
        });

        min.addActionListener(e->{
            int x=Integer.parseInt(qty.getText());

            if(x>1) {
                qty.setText(""+(x-1));
                update.run();
            }
        });

        update.run();

        p.add(foto); jarak(p,10);
        p.add(label(b.getNamaBahan(),20,Color.BLACK));

        jarak(p,6);
        p.add(label("Stok saat ini: "+b.getStok(),14,Color.GRAY));

        jarak(p,18);
        p.add(label("Jumlah tambahan",13,Color.GRAY));

        jarak(p,10);
        p.add(q);

        jarak(p,18);
        p.add(hasil);

        Object[] opsi={"Simpan Stok","Batal"};

        int ok=JOptionPane.showOptionDialog(
            this,p,"Tambah Stok",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,opsi,opsi[0]);

        if(ok!=0) return;

        try {
            int x=Integer.parseInt(qty.getText());

            if((long)b.getStok()+x>Integer.MAX_VALUE)
                throw new IllegalArgumentException(
                    "Jumlah stok terlalu besar!");

            SpringUtil.getBahanService().tambahStok(
                b.getIdBahan(),x);

            JOptionPane.showMessageDialog(this,
                "Stok "+b.getNamaBahan()+" berhasil ditambahkan!\n"+
                "Jumlah tambahan: "+x);

            refreshStok();

        } catch(Exception e) { error(e); }
    }

    String gambarBahan(String n) {
        return n.substring(0,1).toUpperCase(Locale.ROOT)
            +n.substring(1).toLowerCase(Locale.ROOT)+".jpg";
    }

    void refreshStok() {
        refreshHalaman("Stok Bahan",this::stok);
    }

    // ================= TRANSAKSI =================

    void transaksi() {
        JTable t=new JTable(cart);
        table(t);

        JPanel f=box();
        f.setLayout(new BorderLayout());

        JLabel sum=new JLabel("Total  "+rupiah(total));
        sum.setFont(new Font("SansSerif",Font.BOLD,22));

        JPanel btn=new JPanel();

        JButton add=button("+ Tambah Menu");
        JButton pay=button("Bayar");

        add.addActionListener(e->tambahMenu());
        pay.addActionListener(e->bayar());

        btn.add(add); btn.add(pay);

        f.add(sum);
        f.add(btn,BorderLayout.EAST);

        JPanel p=new JPanel(new BorderLayout(0,15));
        p.setOpaque(false);

        p.add(scroll(t));
        p.add(f,BorderLayout.SOUTH);

        add(p);
    }

    void tambahMenu() {
        String[] list=getMenu();
        if(list.length==0) return;

        JComboBox<String> menu=new JComboBox<>(list);

        JLabel foto=image(gambar(list[0]));
        JLabel info=center(""),qty=center("1");

        JButton min=button("−"),plus=button("+");

        JPanel q=new JPanel(new GridLayout(1,3,8,0));
        q.add(min); q.add(qty); q.add(plus);

        JPanel bawah=new JPanel(new BorderLayout());
        bawah.add(info);
        bawah.add(q,BorderLayout.SOUTH);

        JPanel p=new JPanel(new BorderLayout(8,8));
        p.add(menu,BorderLayout.NORTH);
        p.add(foto);
        p.add(bawah,BorderLayout.SOUTH);

        Runnable update=()->{
            String n=(String)menu.getSelectedItem();

            foto.setIcon(image(gambar(n)).getIcon());

            info.setText("<html><center><b>"+n+"</b><br>"+
                rupiah(harga(n))+"</center></html>");
        };

        update.run();

        menu.addActionListener(e->update.run());

        plus.addActionListener(e->qty.setText(
            ""+(Integer.parseInt(qty.getText())+1)));

        min.addActionListener(e->{
            int x=Integer.parseInt(qty.getText());
            if(x>1) qty.setText(""+(x-1));
        });

        int ok=JOptionPane.showConfirmDialog(
            this,p,"Tambah Pesanan",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if(ok!=JOptionPane.OK_OPTION) return;

        String n=(String)menu.getSelectedItem();
        int x=Integer.parseInt(qty.getText()),h=harga(n);

        cart.addRow(new Object[]{n,x,rupiah(h*x)});

        total+=h*x;
        refresh();
    }

    void bayar() {
        if(cart.getRowCount()==0) {
            JOptionPane.showMessageDialog(
                this,"Belum ada pesanan.");
            return;
        }

        int id=0;

        try(var c=Koneksi.getConnection()) {

            var t=c.prepareStatement(
                "INSERT INTO transaksi(total,metode_pembayaran,uang_bayar,"+
                "kembalian,status) VALUES(?,'Cash',?,0,'PENDING')",
                Statement.RETURN_GENERATED_KEYS);

            t.setInt(1,total);
            t.setInt(2,total);
            t.executeUpdate();

            var k=t.getGeneratedKeys();
            if(k.next()) id=k.getInt(1);

            var d=c.prepareStatement(
                "INSERT INTO detail_transaksi(id_transaksi,id_menu,jumlah,"+
                "harga,subtotal) VALUES(?,?,?,?,?)");

            for(int i=0;i<cart.getRowCount();i++) {
                String n=cart.getValueAt(i,0).toString();
                int q=(int)cart.getValueAt(i,1),h=harga(n);

                d.setInt(1,id);
                d.setInt(2,idMenu(n));
                d.setInt(3,q);
                d.setInt(4,h);
                d.setInt(5,h*q);
                d.addBatch();
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

            p.setInt(1,id);
            p.executeUpdate();

        } catch(Exception e) { error(e); return; }

        JOptionPane.showMessageDialog(this,"Pembayaran berhasil!");

        cart.setRowCount(0);
        total=0;
        refresh();
    }

    // ================= RIWAYAT =================

    void riwayat() {
        DefaultTableModel m=new DefaultTableModel(
            new String[]{"ID","Tanggal","Total","Status"},0) {

            public boolean isCellEditable(int r,int c) {
                return false;
            }
        };

        try(var c=Koneksi.getConnection();
            var s=c.createStatement();
            var r=s.executeQuery(
                "SELECT * FROM transaksi WHERE status='LUNAS' "+
                "ORDER BY id_transaksi DESC")) {

            while(r.next()) {
                m.addRow(new Object[]{
                    r.getInt("id_transaksi"),
                    r.getString("tanggal"),
                    rupiah(r.getInt("total")),
                    r.getString("status")
                });
            }

        } catch(Exception e) { error(e); }

        JTable t=new JTable(m);
        table(t);
        add(scroll(t));
    }

    // ================= DATABASE =================

    String[] getMenu() {
        ArrayList<String> x=new ArrayList<>();

        try(var c=Koneksi.getConnection();
            var s=c.createStatement();
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
        try(var c=Koneksi.getConnection();
            var p=c.prepareStatement(
                "SELECT "+kolom+" FROM menu WHERE nama_menu=?")) {

            p.setString(1,n);

            var r=p.executeQuery();
            if(r.next()) return r.getInt(1);

        } catch(Exception e) { error(e); }

        return 0;
    }

    int angka(String sql) {
        try(var c=Koneksi.getConnection();
            var s=c.createStatement();
            var r=s.executeQuery(sql)) {

            if(r.next()) return r.getInt(1);

        } catch(Exception e) { error(e); }

        return 0;
    }

    // ================= KOMPONEN UI =================

    JPanel box() {
        JPanel p=new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

        p.setBorder(
            BorderFactory.createEmptyBorder(18,18,18,18));

        return p;
    }

    JPanel vertical() {
        JPanel p=new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    void jarak(JPanel p,int x) {
        p.add(Box.createVerticalStrut(x));
    }

    JPanel grid(int r,int c) {
        JPanel p=new JPanel(new GridLayout(r,c,16,16));
        p.setOpaque(false);
        return p;
    }

    JButton button(String s) {
        JButton b=new JButton(s);

        b.setBackground(RED);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);

        b.setBorder(
            BorderFactory.createEmptyBorder(8,12,8,12));

        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return b;
    }

    JLabel label(String s,int ukuran,Color warna) {
        JLabel l=center(s);

        l.setFont(new Font("SansSerif",Font.BOLD,ukuran));
        l.setForeground(warna);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        return l;
    }

    void table(JTable t) {
        t.setRowHeight(36);
        t.setShowVerticalLines(false);
    }

    JScrollPane scroll(Component c) {
        JScrollPane s=new JScrollPane(c);

        s.setBorder(null);
        s.getViewport().setBackground(BG);
        s.getVerticalScrollBar().setUnitIncrement(16);

        return s;
    }

    JLabel image(String f) {
        return image(f,200,135);
    }

    JLabel image(String f,int w,int h) {
        var u=getClass().getResource("/images/"+f);

        if(u==null) return center("No Image");

        Image i=new ImageIcon(u).getImage()
            .getScaledInstance(w,h,Image.SCALE_SMOOTH);

        return new JLabel(
            new ImageIcon(i),SwingConstants.CENTER);
    }

    String gambar(String n) {
        n=n.toLowerCase();

        if(n.contains("ayam"))
            return "nasi_rames_ayam.jpg";

        if(n.contains("telur"))
            return "nasi_rames_telur.jpg";

        return "nasi_rames_tempe.jpg";
    }

    String deskripsi(String n) {
        n=n.toLowerCase();

        if(n.contains("ayam"))
            return "Nasi putih dengan ayam goreng, sayuran, dan sambal.";

        if(n.contains("telur"))
            return "Nasi putih dengan telur, sayuran, dan sambal.";

        return "Nasi putih dengan tempe, sayuran, dan sambal.";
    }

    String rupiah(int n) {
        return "Rp"+NumberFormat.getNumberInstance(
            new Locale("id","ID")).format(n);
    }

    JLabel center(String s) {
        return new JLabel(s,SwingConstants.CENTER);
    }

    void refresh() {
        refreshHalaman("Transaksi Kasir",this::transaksi);
    }

    void refreshHalaman(String judul,Runnable halaman) {
        removeAll();

        add(judul(judul),BorderLayout.NORTH);
        halaman.run();

        revalidate();
        repaint();
    }

    void error(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,e.getMessage());
    }
}
