package Database;

import Database.KoneksiDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.CalonMitra;

public class MitraDAO {

    public static String hitungTotalMitra() {
        String sql = "SELECT COUNT(*) AS total FROM toko_mitra";
        try (Connection conn = KoneksiDB.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total") + " Mitra";
            }
        } catch (Exception e) { return "0 Mitra"; }
        return "0 Mitra";
    }

    public static String getStatusToko(int idToko) {
        String status = "Aktif";
        String sql = "SELECT status_kerjasama FROM toko_mitra WHERE id_toko = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idToko);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) status = rs.getString("status_kerjasama");
        } catch (Exception e) { e.printStackTrace(); }
        return status;
    }

    public static void ajukanPendaftaranMitra(String nama, String hp, String dummy, String toko, String alamat, String user, String pass) throws Exception {
        String sql = "INSERT INTO calon_mitra (nama_lengkap, no_hp, nama_toko, alamat_toko, username, password, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama); ps.setString(2, hp); ps.setString(3, toko);
            ps.setString(4, alamat); ps.setString(5, user); ps.setString(6, pass);
            ps.executeUpdate();
        }
    }
    
    public static List<CalonMitra> getCalonMitraPending() throws Exception {
    List<CalonMitra> list = new ArrayList<>();
    String sql = "SELECT id_pendaftaran, nama_lengkap, nama_toko, no_hp, username FROM calon_mitra WHERE status = 'pending'";
    try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new CalonMitra(
                rs.getInt("id_pendaftaran"), 
                rs.getString("nama_lengkap"), 
                rs.getString("nama_toko"), 
                rs.getString("no_hp"), 
                rs.getString("username")
            ));
        }
    }
    return list;
}

    public static void accMitra(String idDaftar) throws Exception {
        Connection conn = null;
        PreparedStatement psCari = null, psUser = null, psToko = null, psMitraUser = null, psUpdate = null;
        ResultSet rs = null, rsKeys = null, rsKeysToko = null;

        try {
            conn = KoneksiDB.getKoneksi();
            conn.setAutoCommit(false); 

            String sqlCari = "SELECT * FROM calon_mitra WHERE id_pendaftaran = ?";
            psCari = conn.prepareStatement(sqlCari);
            psCari.setString(1, idDaftar);
            rs = psCari.executeQuery();

            if (rs.next()) {
                String namaLengkap = rs.getString("nama_lengkap");
                String namaToko = rs.getString("nama_toko");
                String alamat = rs.getString("alamat_toko"); 
                String noHp = rs.getString("no_hp");
                String email = rs.getString("email");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String idKecamatan = rs.getString("id_kecamatan"); 
                
                String sqlUser = "INSERT INTO users (username, password, role) VALUES (?, ?, 'mitra')";
                psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
                psUser.setString(1, user); psUser.setString(2, pass);
                psUser.executeUpdate();

                rsKeys = psUser.getGeneratedKeys();
                int idUserBaru = 0;
                if (rsKeys.next()) idUserBaru = rsKeys.getInt(1); 
 
                String sqlToko = "INSERT INTO toko_mitra (id_user, nama_toko, alamat, id_kecamatan, no_hp, status_kerjasama) VALUES (?, ?, ?, ?, ?, 'aktif')";
                psToko = conn.prepareStatement(sqlToko, Statement.RETURN_GENERATED_KEYS);
                psToko.setInt(1, idUserBaru); psToko.setString(2, namaToko);
                psToko.setString(3, alamat); psToko.setString(4, idKecamatan);
                psToko.setString(5, noHp);
                psToko.executeUpdate();
                
                rsKeysToko = psToko.getGeneratedKeys();
                int idTokoBaru = 0;
                if (rsKeysToko.next()) idTokoBaru = rsKeysToko.getInt(1);
                
                String sqlMitraUser = "INSERT INTO mitra_user (id_user, id_toko, nama_lengkap, no_hp, email, nama_toko, alamat_toko) VALUES (?, ?, ?, ?, ?, ?, ?)";
                psMitraUser = conn.prepareStatement(sqlMitraUser);
                psMitraUser.setInt(1, idUserBaru); psMitraUser.setInt(2, idTokoBaru);
                psMitraUser.setString(3, namaLengkap); psMitraUser.setString(4, noHp);
                psMitraUser.setString(5, email); psMitraUser.setString(6, namaToko);
                psMitraUser.setString(7, alamat);
                psMitraUser.executeUpdate();
                
                String sqlUpdate = "UPDATE calon_mitra SET status = 'aktif' WHERE id_pendaftaran = ?";
                psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setString(1, idDaftar);
                psUpdate.executeUpdate();

                conn.commit(); 
            } else {
                throw new Exception("Data pendaftaran tidak ditemukan!");
            }
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rsKeysToko != null) rsKeysToko.close();
            if (rsKeys != null) rsKeys.close();
            if (rs != null) rs.close();
            if (psUpdate != null) psUpdate.close();
            if (psMitraUser != null) psMitraUser.close();
            if (psToko != null) psToko.close();
            if (psUser != null) psUser.close();
            if (psCari != null) psCari.close();
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }

    public static void tolakMitra(String id) throws Exception {
        String sql = "UPDATE calon_mitra SET status = 'ditolak' WHERE id_pendaftaran = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }
}