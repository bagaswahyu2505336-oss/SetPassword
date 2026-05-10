package Login_Dan_Akses;

import Login_Dan_Akses.UserAuth;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Database.KoneksiDB;

public class Mitra extends UserAuth {
    private int idUser;
    private int idToko;
    private String pesanError; 

    public Mitra(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean login() {
        try {
            Connection conn = KoneksiDB.getKoneksi();
            
            String sql = "SELECT u.id_user, t.id_toko, u.status FROM users u " +
                         "JOIN toko_mitra t ON u.id_user = t.id_user " +
                         "WHERE u.username=? AND u.password=? AND u.role='mitra'";
                         
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String statusUser = rs.getString("status");
                
                if (statusUser != null && statusUser.equalsIgnoreCase("nonaktif")) {
                    this.pesanError = "Akses Ditolak: Akun Anda telah dinonaktifkan oleh Admin!";
                    return false; 
                }

                this.idUser = rs.getInt("id_user");
                this.idToko = rs.getInt("id_toko"); 
                return true; 
                
            } else {
                this.pesanError = "Login gagal! Username atau Password salah.";
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("Login error (Mitra): " + e.getMessage());
            this.pesanError = "Terjadi kesalahan pada sistem/database.";
            return false;
        }
    }

    public int getIdUser() { return idUser; }
    public int getIdToko() { return idToko; }
    public String getPesanError() { return pesanError; }
}