package com.mycompany.nasirameskangdadang;

import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    JPanel content;
    final Color RED = new Color(150,35,35);

    public Main() {
        setTitle("Nasi Rames Kang Dadang");
        setSize(1100,680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(sidebar(),BorderLayout.WEST);

        content=new JPanel(new BorderLayout());
        content.setBackground(new Color(248,248,246));
        add(content);

        tampil("Dashboard");
    }

    JPanel sidebar() {
        JPanel s=new JPanel();
        s.setPreferredSize(new Dimension(220,0));
        s.setBackground(RED);
        s.setLayout(new BoxLayout(s,BoxLayout.Y_AXIS));
        s.setBorder(BorderFactory.createEmptyBorder(30,15,20,15));

        JLabel logo=new JLabel(
            "<html><b>NASI RAMES</b><br>Kang Dadang</html>");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif",Font.PLAIN,20));
        logo.setAlignmentX(LEFT_ALIGNMENT);

        s.add(logo);
        s.add(Box.createVerticalStrut(35));

        String[] menu={"Dashboard","Data Menu","Stok Bahan",
            "Transaksi Kasir","Riwayat Transaksi"};

        for(String m:menu) s.add(nav(m));

        return s;
    }

    JButton nav(String text) {
        JButton b=new JButton(text);
        b.setMaximumSize(new Dimension(190,45));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBackground(RED);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
        b.addActionListener(e->tampil(text));
        return b;
    }

    void tampil(String page) {
        content.removeAll();
        content.add(new DataPanel(page));
        content.revalidate();
        content.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new Main().setVisible(true));
    }
}