package Database;

import Database.KoneksiDB;
import Model.ModelData;
import Error_Exception.GrosirException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PengirimanDAO {

    public static List<ModelData.JadwalKurir> getJadwalKurir(int idKurir) throws GrosirException {
        List<ModelData.JadwalKurir> daftar = new ArrayList<>();
        String sql = "SELECT p.id_pengiriman, t.nama_toko, t.alamat, p.status, t.no_hp " +
                     "FROM pengiriman p JOIN pesanan ps ON p.id_pesanan = ps.id_pesanan " +
                     "JOIN toko_mitra t ON ps.id_toko = t.id_toko WHERE p.id_kurir = ?";
                     
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKurir);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                daftar.add(new ModelData.JadwalKurir(
                    rs.getString("id_pengiriman"), rs.getString("nama_toko"), rs.getString("alamat"), 
                    rs.getString("status"), rs.getString("nama_toko"), rs.getString("no_hp")     
                ));
            }
        } catch (SQLException e) { 
            throw new GrosirException("Gagal muat jadwal: " + e.getMessage()); 
        }
        return daftar;
    }

    public static boolean updateStatusPengiriman(String idPengiriman, String statusKurir, String pathFoto) throws GrosirException {
        Connection conn = null;
        try {
            conn = KoneksiDB.getKoneksi();
            conn.setAutoCommit(false); 

            String sql1 = "UPDATE pengiriman SET status = ?, bukti_foto = ? WHERE id_pengiriman = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, statusKurir); ps1.setString(2, pathFoto); ps1.setString(3, idPengiriman);
            int updated = ps1.executeUpdate();

            if (updated > 0) {
                String sqlCari = "SELECT id_pesanan FROM pengiriman WHERE id_pengiriman = ?";
                PreparedStatement psCari = conn.prepareStatement(sqlCari);
                psCari.setString(1, idPengiriman);
                ResultSet rs = psCari.executeQuery();

                if (rs.next()) {
                    String idPesanan = rs.getString("id_pesanan");
                    String statusBaruAdmin = "diproses"; 

                    if (statusKurir.equalsIgnoreCase("dalam perjalanan")) {
                        statusBaruAdmin = "dikirim";
                    } else if (statusKurir.equalsIgnoreCase("sampai")) {
                        statusBaruAdmin = "sampai_tujuan";
                    } else if (statusKurir.equalsIgnoreCase("selesai")) {
                        statusBaruAdmin = "selesai";
                    }

                    String sql2 = "UPDATE pesanan SET status = ? WHERE id_pesanan = ?";
                    PreparedStatement ps2 = conn.prepareStatement(sql2);
                    ps2.setString(1, statusBaruAdmin); ps2.setString(2, idPesanan);
                    ps2.executeUpdate();
                }
            }
            conn.commit(); 
            return updated > 0;
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (Exception ex) {} }
            throw new GrosirException("Gagal Update: " + e.getMessage());
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (Exception ex) {} }
        }
    }

    public static String getBuktiFoto(String idPesanan) {
        String path = null;
        String sql = "SELECT bukti_foto FROM pengiriman WHERE id_pesanan = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPesanan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) path = rs.getString("bukti_foto");
        } catch (Exception e) { e.printStackTrace(); }
        return path;
    }

    public static List<String[]> getDaftarKurir() throws Exception {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT k.id_kurir, u.username FROM kurir k JOIN users u ON k.id_user = u.id_user";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{ rs.getString("id_kurir"), rs.getString("username") });
            }
        }
        return list;
    }

    public static String hitungPengirimanSelesai(int idKurir) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM pengiriman WHERE id_kurir = ? AND LOWER(status) = 'selesai'";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKurir);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total + " Selesai";
    }
}