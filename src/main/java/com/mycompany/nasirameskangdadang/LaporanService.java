
package com.mycompany.nasirameskangdadang;

import java.awt.Component;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

public class LaporanService {

    public static void buka(Component parent) {
        try (
            InputStream file=LaporanService.class.getResourceAsStream(
                "/reports/LaporanPenjualan.jrxml");
            InputStream logo=LaporanService.class.getResourceAsStream(
                "/images/LogoIMG.png");
            Connection koneksi=Koneksi.getConnection()
        ) {
            if(file==null)
                throw new Exception("LaporanPenjualan.jrxml tidak ditemukan!");

            JasperReport laporan=JasperCompileManager.compileReport(file);

            Map<String,Object> parameter=new HashMap<>();
            if(logo!=null) parameter.put("LOGO_IMAGE",logo);

            JasperPrint hasil=JasperFillManager.fillReport(
                laporan,parameter,koneksi);

            JasperViewer.viewReport(hasil,false);

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                "Gagal membuka laporan:\n"+e.getMessage(),
                "Error Laporan",JOptionPane.ERROR_MESSAGE);
        }
    }
}
