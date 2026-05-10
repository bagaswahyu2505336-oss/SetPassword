package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WilayahDAO {


    public static List<String> getDaftarProvinsi() throws Exception {
        List<String> list = new ArrayList<>();
        String query = "SELECT nama_provinsi FROM provinsi ORDER BY nama_provinsi ASC";
        try (Connection conn = KoneksiDB.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(rs.getString("nama_provinsi"));
            }
        }
        return list;
    }

    public static List<String> getDaftarKabupaten(String namaProvinsi) throws Exception {
        List<String> list = new ArrayList<>();
        String query = "SELECT k.nama_kabupaten FROM kabupaten k " +
                       "JOIN provinsi prov ON k.id_provinsi = prov.id_provinsi " +
                       "WHERE prov.nama_provinsi = ? ORDER BY k.nama_kabupaten ASC";
        try (Connection conn = KoneksiDB.getKoneksi(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, namaProvinsi);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("nama_kabupaten"));
                }
            }
        }
        return list;
    }

    public static List<String> getDaftarKecamatan(String namaKabupaten) throws Exception {
        List<String> list = new ArrayList<>();
        String query = "SELECT k.nama_kecamatan FROM kecamatan k " +
                       "JOIN kabupaten kab ON k.id_kabupaten = kab.id_kabupaten " +
                       "WHERE kab.nama_kabupaten = ? ORDER BY k.nama_kecamatan ASC";
        try (Connection conn = KoneksiDB.getKoneksi(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, namaKabupaten);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("nama_kecamatan"));
                }
            }
        }
        return list;
    }
}