
package com.mycompany.nasirameskangdadang;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;

public class Main extends JFrame {

    static final Color RED=new Color(150,35,35);
    static final Color BG=new Color(248,248,246);
    static final Color ACTIVE=new Color(185,54,54);
    static final Color HOVER=new Color(170,45,45);
    static final Color PRESS=new Color(115,26,26);

    private final JPanel content=new JPanel(new BorderLayout());
    private final Map<String,JButton> navigasi=new LinkedHashMap<>();
    private String halamanAktif="";

    public Main() {
        setTitle("Nasi Rames Kang Dadang");
        setSize(1100,680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar=new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setBackground(RED);
        sidebar.setPreferredSize(new Dimension(222,0));
        sidebar.setBorder(
            BorderFactory.createEmptyBorder(10,12,20,12));

        JLabel logo=new JLabel();
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setHorizontalAlignment(SwingConstants.LEFT);
        logo.setMaximumSize(new Dimension(190,190));

        var url=getClass().getResource("/images/Logo.png");
        if(url!=null) {
            Image img=new ImageIcon(url).getImage()
                .getScaledInstance(190,190,Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(img));
        }

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(16));

        String[] pages={
            "Dashboard",
            "Data Menu",
            "Stok Bahan",
            "Transaksi Kasir",
            "Riwayat Transaksi",
            "Laporan Penjualan"
        };

        for(String page:pages) {
            JButton tombol=new JButton(page);
            tombol.setForeground(Color.WHITE);
            tombol.setBackground(RED);
            tombol.setFont(new Font("SansSerif",Font.BOLD,12));
            tombol.setHorizontalAlignment(SwingConstants.LEFT);
            tombol.setFocusPainted(false);
            tombol.setOpaque(true);
            tombol.setAlignmentX(Component.LEFT_ALIGNMENT);
            tombol.setMaximumSize(new Dimension(198,40));
            tombol.setCursor(new Cursor(Cursor.HAND_CURSOR));

            tombol.addActionListener(e->{
                if(page.equals("Laporan Penjualan"))
                    LaporanService.buka(this);
                else
                    tampil(page);
            });

            tombol.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if(!page.equals(halamanAktif))
                        tombol.setBackground(HOVER);
                }
                public void mouseExited(MouseEvent e) {
                    warnaNavigasi();
                }
                public void mousePressed(MouseEvent e) {
                    tombol.setBackground(PRESS);
                }
                public void mouseReleased(MouseEvent e) {
                    warnaNavigasi();
                }
            });

            navigasi.put(page,tombol);
            sidebar.add(tombol);
            sidebar.add(Box.createVerticalStrut(5));
        }

        content.setBackground(BG);
        add(sidebar,BorderLayout.WEST);
        add(content,BorderLayout.CENTER);
        tampil("Dashboard");
    }

    void warnaNavigasi() {
        navigasi.forEach((page,tombol)->{
            boolean aktif=page.equals(halamanAktif);
            tombol.setBackground(aktif?ACTIVE:RED);
            tombol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0,4,0,0,
                    aktif?new Color(255,218,138):RED),
                BorderFactory.createEmptyBorder(0,10,0,4)));
        });
    }

    void tampil(String page) {
        halamanAktif=page;
        warnaNavigasi();

        content.removeAll();

        FadePanel transisi=new FadePanel(new DataPanel(page));
        content.add(transisi,BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
        transisi.mulai();
    }

    static class FadePanel extends JPanel {
        private float progress=0f;

        FadePanel(JPanel isi) {
            super(new BorderLayout());
            setBackground(BG);
            add(isi,BorderLayout.CENTER);
        }

        void mulai() {
            javax.swing.Timer animasi=new javax.swing.Timer(16,null);

            animasi.addActionListener(e->{
                progress=Math.min(1f,progress+0.11f);
                repaint();

                if(progress>=1f) animasi.stop();
            });

            animasi.start();
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();

            g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                Math.max(0.01f,progress)));

            g2.translate(0,Math.round((1f-progress)*8));
            super.paintChildren(g2);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            ()->new Main().setVisible(true));
    }
}
