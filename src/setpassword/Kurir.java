package setpassword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Kurir extends UserAuth {
    private int idKurir;

    public Kurir(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean login() {
        try {
            Connection conn = KoneksiDB.getKoneksi();
            // Melakukan JOIN untuk langsung mendapatkan id_kurir
            String sql = "SELECT u.id_user, k.id_kurir FROM users u " +
                         "JOIN kurir k ON u.id_user = k.id_user " +
                         "WHERE u.username=? AND u.password=? AND u.role='kurir'";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                this.idKurir = rs.getInt("id_kurir");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    public int getIdKurir() { return idKurir; }
}