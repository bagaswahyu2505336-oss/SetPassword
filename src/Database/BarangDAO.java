package Database;

import Database.KoneksiDB;
import Model.Barang;
import Error_Exception.GrosirException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {
    
    public static List<Barang> getKatalogBarang() throws GrosirException {
        List<Barang> daftar = new ArrayList<>();
        String sql = "SELECT id_barang, sku, nama_barang, harga, stok_gudang, foto FROM barang";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(new Barang(
                    rs.getString("id_barang"), rs.getString("sku"), rs.getString("nama_barang"),
                    rs.getString("harga"), rs.getString("stok_gudang"), rs.getString("foto")
                ));
            }
        } catch (SQLException e) { 
            throw new GrosirException("Gagal muat katalog: " + e.getMessage()); 
        }
        return daftar;
    }

    public static void tambahBarangBaru(String sku, String nama, String harga, String stok, String foto) throws Exception {
        String sql = "INSERT INTO barang (sku, nama_barang, harga, stok_gudang, foto) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku); ps.setString(2, nama); ps.setString(3, harga);
            ps.setString(4, stok); ps.setString(5, foto);
            ps.executeUpdate();
        }
    }

    public static void updateBarang(String id_barang, String nama, String harga, String stok) throws Exception {
        String sql = "UPDATE barang SET nama_barang = ?, harga = ?, stok_gudang = ? WHERE id_barang = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama); ps.setString(2, harga); ps.setString(3, stok); ps.setString(4, id_barang);
            ps.executeUpdate();
        }
    }
    
    public static void hapusBarang(String id_barang) throws Exception {
        String sql = "{CALL HapusBarangProcedure(?)}"; 
        try (Connection conn = KoneksiDB.getKoneksi(); java.sql.CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, id_barang); 
            cs.execute();
        }
    }

    public static String hitungTotalStokFisik() {
        try (Connection conn = KoneksiDB.getKoneksi(); 
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT SUM(stok_gudang) FROM barang")) {
            if (rs.next()) return rs.getInt(1) + " Unit";
        } catch (Exception e) { e.printStackTrace(); }
        return "0 Unit";
    }

    public static List<Barang> getBarangBestseller() throws Exception {
        List<Barang> daftar = new ArrayList<>();
        String sql = "SELECT * FROM barang WHERE id_barang IN (" +
                     "SELECT id_barang FROM detail_pesanan GROUP BY id_barang " +
                     "ORDER BY SUM(jumlah) DESC) LIMIT 3";
        try (Connection conn = KoneksiDB.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(new Barang(
                    rs.getString("id_barang"), rs.getString("sku"), rs.getString("nama_barang"),
                    rs.getString("harga"), rs.getString("stok_gudang"), rs.getString("foto")
                ));
            }
        }
        return daftar;
    }
}
