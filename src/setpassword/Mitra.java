package setpassword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Mitra extends UserAuth {
    private int idUser;
    private int idToko;

    public Mitra(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean login() {
        try {
            Connection conn = KoneksiDB.getKoneksi();
            // Melakukan JOIN untuk langsung mendapatkan id_toko
            String sql = "SELECT u.id_user, m.id_toko FROM users u " +
                         "JOIN mitra_user m ON u.id_user = m.id_user " +
                         "WHERE u.username=? AND u.password=? AND u.role='mitra'";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                this.idUser = rs.getInt("id_user");
                this.idToko = rs.getInt("id_toko");
                return true; // Login berhasil
            }
            return false;
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    // Getter untuk digunakan saat memanggil DashboardMitra
    public int getIdUser() { return idUser; }
    public int getIdToko() { return idToko; }
}