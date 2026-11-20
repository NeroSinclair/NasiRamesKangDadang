
package com.mycompany.nasirameskangdadang;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DataPanel extends JPanel {
    static final Color BG=new Color(248,248,246), RED=new Color(150,35,35);
    static final Color GREEN=new Color(40,140,80);
    static final DefaultTableModel cart=model("Menu","Qty","Subtotal");
    static int total=0;
    public DataPanel(String page) {
        setLayout(new BorderLayout(0,18)); setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(28,32,28,32));
        showPage(page);
    }
    void showPage(String page) {
        removeAll();
        JLabel title=label(page,28,Color.BLACK);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        add(title,BorderLayout.NORTH);
        switch(page) {
            case "Dashboard" -> dashboard();
            case "Data Menu" -> menu();
            case "Stok Bahan" -> stok();
            case "Transaksi Kasir" -> transaksi();
            default -> riwayat();
        }
        revalidate(); repaint();
    }
    // DASHBOARD
    void dashboard() {
        JPanel p=grid(2,2);
        p.add(card("TOTAL PENJUALAN",rupiah(angka(
            "SELECT COALESCE(SUM(total),0) FROM transaksi WHERE status='LUNAS'"))));
        p.add(card("MENU TERSEDIA",angka("SELECT COUNT(*) FROM menu")+" Menu"));
        p.add(card("STOK BAHAN",angka("SELECT COUNT(*) FROM bahan")+" Bahan"));
        p.add(card("TRANSAKSI HARI INI",angka("SELECT COUNT(*) FROM transaksi "+
            "WHERE status='LUNAS' AND DATE(tanggal)=CURDATE()")+" Transaksi"));
        add(p);
    }
    JPanel card(String title,String value) {
        JPanel p=box();
        p.add(new JLabel(title)); gap(p,10); p.add(label(value,24,RED));
        return p;
    }
    // DATA MENU
    void menu() {
        JPanel p=grid(0,3);
        try {
            for(Menu m:DataDAO.getMenu()) {
                String n=m.getNamaMenu(), lauk=lauk(n);
                JPanel isi=stack();
                JTextArea d=new JTextArea("Nasi putih dengan "+lauk.toLowerCase(Locale.ROOT)
                    +(lauk.equals("Ayam")?" goreng":"")+", sayuran, dan sambal.");
                d.setFont(new Font("SansSerif",Font.PLAIN,13)); d.setForeground(Color.GRAY);
                d.setLineWrap(true); d.setWrapStyleWord(true);
                d.setEditable(false); d.setOpaque(false); d.setFocusable(false);
                d.setMaximumSize(new Dimension(260,65));
                isi.add(image(gambar(n),200,135)); gap(isi,18);
                isi.add(label(n,19,Color.BLACK)); gap(isi,12);
                isi.add(d); gap(isi,15); isi.add(new JSeparator()); gap(isi,15);
                isi.add(label("Nasi • "+lauk+" • Sayur",12,RED));
                JLabel h=label(rupiah(m.getHarga()),20,RED);
                h.setOpaque(true); h.setBackground(new Color(250,235,235));
                h.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
                p.add(tile(isi,h,15));
            }
        } catch(Exception e) { error(e); }
        add(scroll(p));
    }
    // STOK BAHAN
    void stok() {
        JPanel p=panel(new GridLayout(0,1,0,12)), row=null;
        try {
            var list=DataDAO.getBahan().stream().filter(b->Set.of(
                "beras","ayam","telur","tempe","sayur").contains(
                b.getNamaBahan().toLowerCase(Locale.ROOT)))
                .sorted(Comparator.comparingInt(Bahan::getIdBahan)).toList();
            for(int i=0;i<list.size();i++) {
                if(i%3==0) {
                    row=panel(new FlowLayout(FlowLayout.CENTER,12,0)); p.add(row);
                }
                row.add(cardStok(list.get(i)));
            }
        } catch(Exception e) { error(e); }
        JPanel wrap=panel(new BorderLayout()); wrap.add(p,BorderLayout.NORTH);
        add(scroll(wrap));
    }
    JPanel cardStok(Bahan b) {
        int s=b.getStok(); JPanel isi=stack();
        isi.add(image(gambarBahan(b.getNamaBahan()),180,105)); gap(isi,10);
        isi.add(label(b.getNamaBahan(),18,Color.BLACK)); gap(isi,5);
        isi.add(label("Stok tersedia",12,Color.GRAY));
        isi.add(label(""+s,28,RED));
        String teks=s>10?"Stok Aman":s>0?"Stok Menipis":"Stok Habis";
        Color warna=s>10?GREEN:s>0?new Color(174,112,19):RED;
        JLabel status=label(teks,12,warna);
        status.setOpaque(true);
        status.setBackground(s>10?new Color(232,247,237):
            s>0?new Color(255,245,223):new Color(255,235,235));
        status.setBorder(BorderFactory.createEmptyBorder(6,4,6,4));
        JButton btn=button("+ Tambah Stok",()->inputStok(b));
        btn.setPreferredSize(new Dimension(200,36));
        JPanel bawah=panel(new BorderLayout(0,8));
        bawah.add(status,BorderLayout.NORTH); bawah.add(btn,BorderLayout.SOUTH);
        JPanel c=tile(isi,bawah,10);
        c.setPreferredSize(new Dimension(240,322));
        c.setBorder(stokBorder(new Color(230,225,225)));
        c.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { c.setBorder(stokBorder(new Color(176,74,74))); }
            public void mouseExited(MouseEvent e) { c.setBorder(stokBorder(new Color(230,225,225))); }
        });
        return c;
    }
    javax.swing.border.Border stokBorder(Color c) {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(c),
            BorderFactory.createEmptyBorder(12,14,12,14));
    }
    void inputStok(Bahan b) {
        JPanel p=stack(); JLabel qty=label("1",26,RED), hasil=label("",15,GREEN);
        p.add(image(gambarBahan(b.getNamaBahan()),180,105)); gap(p,10);
        p.add(label(b.getNamaBahan(),20,Color.BLACK)); gap(p,6);
        p.add(label("Stok saat ini: "+b.getStok(),14,Color.GRAY)); gap(p,18);
        p.add(label("Jumlah tambahan",13,Color.GRAY)); gap(p,10);
        p.add(quantity(qty,10000,()->hasil.setText("Stok akhir: "+((long)b.getStok()+number(qty)))));
        gap(p,18); p.add(hasil);
        if(!confirm(p,"Tambah Stok","Simpan Stok")) return;
        try {
            int x=number(qty);
            if((long)b.getStok()+x>Integer.MAX_VALUE)
                throw new IllegalArgumentException("Jumlah stok terlalu besar!");
            SpringUtil.getBahanService().tambahStok(b.getIdBahan(),x);
            JOptionPane.showMessageDialog(this,"Stok "+b.getNamaBahan()+" berhasil ditambahkan!");
            showPage("Stok Bahan");
        } catch(Exception | LinkageError e) { error(e); }
    }
    // TRANSAKSI KASIR
    void transaksi() {
        JPanel f=panel(new BorderLayout()), btn=panel(new FlowLayout(FlowLayout.RIGHT));
        f.add(label("Total  "+rupiah(total),22,Color.BLACK),BorderLayout.WEST);
        btn.add(button("+ Tambah Menu",this::tambahMenu)); btn.add(button("Bayar",this::bayar));
        f.add(btn,BorderLayout.EAST);
        JPanel p=panel(new BorderLayout(0,15)); p.add(scroll(table(cart)));
        JPanel footer=box(); footer.setLayout(new BorderLayout()); footer.add(f);
        p.add(footer,BorderLayout.SOUTH); add(p);
    }
    void tambahMenu() {
        try {
            var list=DataDAO.getMenu(); if(list.isEmpty()) return;
            JComboBox<String> pilihan=new JComboBox<>(list.stream().map(Menu::getNamaMenu).toArray(String[]::new));
            JLabel foto=image(gambar(list.get(0).getNamaMenu()),200,135), info=label("",16,RED), qty=label("1",22,RED);
            JPanel p=panel(new BorderLayout(8,8)), bawah=stack();
            p.add(pilihan,BorderLayout.NORTH); p.add(foto); p.add(bawah,BorderLayout.SOUTH);
            bawah.add(info); gap(bawah,8); bawah.add(quantity(qty,Integer.MAX_VALUE,()->{}));
            Runnable update=()->{
                Menu m=list.get(pilihan.getSelectedIndex());
                JLabel img=image(gambar(m.getNamaMenu()),200,135);
                foto.setIcon(img.getIcon()); foto.setText(img.getText());
                info.setText(m.getNamaMenu()+" - "+rupiah(m.getHarga()));
            };
            update.run(); pilihan.addActionListener(e->update.run());
            if(!confirm(p,"Tambah Pesanan","Tambah")) return;
            Menu m=list.get(pilihan.getSelectedIndex()); int q=number(qty);
            int subtotal=Math.multiplyExact(m.getHarga(),q), baru=Math.addExact(total,subtotal);
            cart.addRow(new Object[]{m.getNamaMenu(),q,rupiah(subtotal)});
            total=baru; showPage("Transaksi Kasir");
        } catch(Exception e) { error(e); }
    }
    void bayar() {
        if(cart.getRowCount()==0) { JOptionPane.showMessageDialog(this,"Belum ada pesanan."); return; }
        int id;
        try(var c=Koneksi.getConnection(); var t=c.prepareStatement(
            "INSERT INTO transaksi(total,metode_pembayaran,uang_bayar,kembalian,status) "+
            "VALUES(?,'Cash',?,0,'PENDING')",Statement.RETURN_GENERATED_KEYS)) {
            t.setInt(1,total); t.setInt(2,total); t.executeUpdate();
            try(var k=t.getGeneratedKeys()) {
                if(!k.next()) throw new SQLException("ID transaksi tidak ditemukan!");
                id=k.getInt(1);
            }
            try(var d=c.prepareStatement("INSERT INTO detail_transaksi(id_transaksi,id_menu,jumlah,harga,subtotal) VALUES(?,?,?,?,?)")) {
                for(int i=0;i<cart.getRowCount();i++) {
                    String n=cart.getValueAt(i,0).toString();
                    int q=(int)cart.getValueAt(i,1), h=menuValue(n,"harga");
                    d.setInt(1,id); d.setInt(2,menuValue(n,"id_menu")); d.setInt(3,q);
                    d.setInt(4,h); d.setInt(5,h*q); d.addBatch();
                }
                d.executeBatch();
            }
        } catch(Exception e) { error(e); return; }
        if(!confirm("Total yang harus dibayar:\n"+rupiah(total),"Pembayaran","Bayar")) return;
        try(var c=Koneksi.getConnection(); var p=c.prepareStatement("UPDATE transaksi SET status='LUNAS' WHERE id_transaksi=?")) {
            p.setInt(1,id); p.executeUpdate();
        } catch(Exception e) { error(e); return; }
        JOptionPane.showMessageDialog(this,"Pembayaran berhasil!");
        cart.setRowCount(0); total=0; showPage("Transaksi Kasir");
    }
    void riwayat() {
        DefaultTableModel m=model("ID","Tanggal","Total","Status");
        try(var c=Koneksi.getConnection(); var s=c.createStatement(); var r=s.executeQuery(
            "SELECT * FROM transaksi WHERE status='LUNAS' ORDER BY id_transaksi DESC")) {
            while(r.next()) m.addRow(new Object[]{r.getInt("id_transaksi"),r.getString("tanggal"),
                rupiah(r.getInt("total")),r.getString("status")});
        } catch(Exception e) { error(e); }
        add(scroll(table(m)));
    }
    // DATABASE
    int menuValue(String n,String kolom) throws SQLException {
        try(var c=Koneksi.getConnection(); var p=c.prepareStatement("SELECT "+kolom+" FROM menu WHERE nama_menu=?")) {
            p.setString(1,n);
            try(var r=p.executeQuery()) { if(r.next()) return r.getInt(1); }
        }
        throw new SQLException("Menu tidak ditemukan: "+n);
    }
    int angka(String sql) {
        try(var c=Koneksi.getConnection(); var s=c.createStatement(); var r=s.executeQuery(sql)) {
            if(r.next()) return r.getInt(1);
        } catch(Exception e) { error(e); }
        return 0;
    }
    // KOMPONEN UI YANG DIPAKAI ULANG
    JPanel panel(LayoutManager l) { JPanel p=new JPanel(l); p.setOpaque(false); return p; }
    JPanel stack() { JPanel p=panel(null); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); return p; }
    JPanel grid(int r,int c) { return panel(new GridLayout(r,c,16,16)); }
    void gap(JPanel p,int n) { p.add(Box.createVerticalStrut(n)); }
    JPanel box() {
        JPanel p=stack(); p.setOpaque(true); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(18,18,18,18)); return p;
    }
    JPanel tile(Component isi,Component footer,int gap) {
        JPanel p=box(); p.setLayout(new BorderLayout(0,gap));
        p.add(isi); p.add(footer,BorderLayout.SOUTH); return p;
    }
    JLabel label(String s,int size,Color color) {
        JLabel l=new JLabel(s,SwingConstants.CENTER); l.setForeground(color);
        l.setFont(new Font("SansSerif",Font.BOLD,size)); l.setAlignmentX(Component.CENTER_ALIGNMENT); return l;
    }
    JButton button(String s,Runnable action) {
        JButton b=new JButton(s); b.setBackground(RED); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if(action!=null) b.addActionListener(e->action.run());
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if(b.isEnabled()) b.setBackground(new Color(174,49,49)); }
            public void mouseExited(MouseEvent e) { b.setBackground(RED); }
            public void mousePressed(MouseEvent e) { if(b.isEnabled()) b.setBackground(new Color(116,27,27)); }
            public void mouseReleased(MouseEvent e) { b.setBackground(b.contains(e.getPoint())?new Color(174,49,49):RED); }
        });
        return b;
    }
    JPanel quantity(JLabel qty,int max,Runnable changed) {
        JPanel p=panel(new GridLayout(1,3,10,0)); p.setMaximumSize(new Dimension(230,42));
        JButton min=button("−",null), plus=button("+",null);
        min.setPreferredSize(new Dimension(50,38));
        plus.setPreferredSize(new Dimension(50,38));
        Runnable update=()->{ min.setEnabled(number(qty)>1); plus.setEnabled(number(qty)<max); changed.run(); };
        min.addActionListener(e->{ if(number(qty)>1) qty.setText(""+(number(qty)-1)); update.run(); });
        plus.addActionListener(e->{ if(number(qty)<max) qty.setText(""+(number(qty)+1)); update.run(); });
        p.add(min); p.add(qty); p.add(plus); update.run(); return p;
    }
    int number(JLabel l) { return Integer.parseInt(l.getText()); }
    boolean confirm(Object content,String title,String action) {
        Object[] options={action,"Batal"};
        return JOptionPane.showOptionDialog(this,content,title,JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,null,options,options[0])==0;
    }
    static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c) { return false; } };
    }
    JTable table(DefaultTableModel m) {
        JTable t=new JTable(m); t.setRowHeight(36); t.setShowVerticalLines(false); return t;
    }
    JScrollPane scroll(Component c) {
        JScrollPane s=new JScrollPane(c); s.setBorder(null); s.getViewport().setBackground(BG);
        s.getVerticalScrollBar().setUnitIncrement(16); return s;
    }
    JLabel image(String f,int w,int h) {
        JLabel l=label("",13,Color.GRAY); var u=getClass().getResource("/images/"+f);
        if(u==null) l.setText("No Image");
        else l.setIcon(new ImageIcon(new ImageIcon(u).getImage().getScaledInstance(w,h,Image.SCALE_SMOOTH)));
        l.setPreferredSize(new Dimension(w,h)); return l;
    }
    String lauk(String n) {
        n=n.toLowerCase(Locale.ROOT); return n.contains("ayam")?"Ayam":n.contains("telur")?"Telur":"Tempe";
    }
    String gambar(String n) { return "nasi_rames_"+lauk(n).toLowerCase(Locale.ROOT)+".jpg"; }
    String gambarBahan(String n) { return n.substring(0,1).toUpperCase(Locale.ROOT)+n.substring(1).toLowerCase(Locale.ROOT)+".jpg"; }
    String rupiah(int n) { return "Rp"+NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(n); }
    void error(Throwable e) {
        e.printStackTrace(); Throwable cause=e;
        while(cause.getCause()!=null && cause.getCause()!=cause) cause=cause.getCause();
        JOptionPane.showMessageDialog(this,cause.getMessage()==null?cause.toString():cause.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
    }
}
