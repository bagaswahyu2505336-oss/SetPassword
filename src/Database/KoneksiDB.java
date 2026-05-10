package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Error_Exception.GrosirException;

public class KoneksiDB {
    private static final String URL = "jdbc:mysql://localhost:3306/sistem_grosir";
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static Connection getKoneksi() throws GrosirException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new GrosirException("Library MySQL Connector (JDBC) BELUM ditambahkan ke project Anda!", e);
        } catch (SQLException e) {
            throw new GrosirException("Gagal terhubung ke Database MySQL. Pastikan XAMPP menyala!", e);
        }
    }
}