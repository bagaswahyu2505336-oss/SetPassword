package setpassword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Admin extends UserAuth {
    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean login() {
        try {
            Connection conn = KoneksiDB.getKoneksi(); // Diubah menjadi KoneksiDB
            String sql = "SELECT * FROM users WHERE username=? AND password=? AND role='admin'";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }
}