package Database;

import Database.KoneksiDB;
import Model.Pengguna;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static boolean cekStatusPending(String user, String pass) throws Exception {
        boolean isPending = false;
        String sql = "SELECT status FROM calon_mitra WHERE username = ? AND password = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && "pending".equalsIgnoreCase(rs.getString("status"))) isPending = true;
        }
        return isPending;
    }
    
    public static List<Pengguna> getAllUserAktif() throws Exception {
    List<Pengguna> list = new ArrayList<>();
    try (Connection conn = KoneksiDB.getKoneksi()) {
        String sqlUsers = "SELECT id_user, username, role, status FROM users WHERE role != 'admin'";
        PreparedStatement ps = conn.prepareStatement(sqlUsers);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Pengguna(
                rs.getInt("id_user"), 
                rs.getString("username"), 
                rs.getString("role"), 
                rs.getString("status")
            ));
        }
    }
    return list;
}

    public static void cabutAksesUser(String id, String role) throws Exception {
        Connection conn = null;
        try {
            conn = KoneksiDB.getKoneksi();
            conn.setAutoCommit(false); 

            int idUser = Integer.parseInt(id);

            String sqlUpdateUser = "UPDATE users SET status = 'nonaktif' WHERE id_user = ?";
            PreparedStatement psUpdateUser = conn.prepareStatement(sqlUpdateUser);
            psUpdateUser.setInt(1, idUser);
            int terupdate = psUpdateUser.executeUpdate();
            
            if (terupdate == 0) throw new Exception("Data User dengan ID: " + idUser + " tidak ditemukan!");

            if (role.equalsIgnoreCase("Mitra")) {
                String sqlCariUser = "SELECT username FROM users WHERE id_user = ?";
                PreparedStatement psCariUser = conn.prepareStatement(sqlCariUser);
                psCariUser.setInt(1, idUser);
                ResultSet rsUser = psCariUser.executeQuery();

                if (rsUser.next()) {
                    String usernameUser = rsUser.getString("username");

                    String sqlUpdateToko = "UPDATE toko_mitra SET status_kerjasama = 'nonaktif' WHERE id_user = ?";
                    PreparedStatement psUpdateToko = conn.prepareStatement(sqlUpdateToko);
                    psUpdateToko.setInt(1, idUser);
                    psUpdateToko.executeUpdate();

                    String sqlUpdateCalon = "UPDATE calon_mitra SET status = 'nonaktif' WHERE username = ?";
                    PreparedStatement psUpdateCalon = conn.prepareStatement(sqlUpdateCalon);
                    psUpdateCalon.setString(1, usernameUser);
                    psUpdateCalon.executeUpdate();
                }
            } else if (role.equalsIgnoreCase("Kurir")) {
                String sqlUpdateKurir = "UPDATE kurir SET status = 'nonaktif' WHERE id_user = ?";
                PreparedStatement psUpdateKurir = conn.prepareStatement(sqlUpdateKurir);
                psUpdateKurir.setInt(1, idUser);
                psUpdateKurir.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Gagal mencabut akses: " + e.getMessage());
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }
}