package Database;

import Database.KoneksiDB;
import Model.Pesanan;
import Error_Exception.GrosirException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PesananDAO {

    // --- CLASS & METHOD DARI PESANANDAO LAMA ---
    public static class DetailPesananTemp {
        public int idBarang; public int jumlah; public double harga;
        public DetailPesananTemp(int idBarang, int jumlah, double harga) {
            this.idBarang = idBarang; this.jumlah = jumlah; this.harga = harga;
        }
    }

    public static String hitungTotalPesanan() {
        String sql = "SELECT COUNT(*) AS total FROM pesanan";
        try (Connection conn = KoneksiDB.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total") + " Pesanan";
            }
        } catch (Exception e) { return "0 Pesanan"; }
        return "0 Pesanan";
    }

    public static void buatPesananBaru(int idUser, int idToko, String alamatKirim, List<DetailPesananTemp> daftarBarang) throws GrosirException {
    }

    public static List<Pesanan> getSemuaPesananAdmin() throws GrosirException {
        return new ArrayList<>();
    }

    // --- CLASS & METHOD DARI FUNGSI DB ---
    public static class KeranjangItem {
        public String idBarang, namaBarang;
        public int jumlah;
        public double hargaSatuan, subtotal;

        public KeranjangItem(String id, String nama, int jml, double harga) {
            this.idBarang = id; this.namaBarang = nama;
            this.jumlah = jml; this.hargaSatuan = harga;
            this.subtotal = jml * harga;
        }
    }

    public static void checkoutKeranjang(int idToko, int idUser, List<KeranjangItem> keranjang) throws Exception {
        Connection conn = null;
        try {
            conn = KoneksiDB.getKoneksi();
            conn.setAutoCommit(false); 

            String alamatKirim = "-";
            String sqlInfoToko = "SELECT alamat FROM toko_mitra WHERE id_toko = ?";
            try (PreparedStatement psInfo = conn.prepareStatement(sqlInfoToko)) {
                psInfo.setInt(1, idToko);
                ResultSet rsInfo = psInfo.executeQuery();
                if (rsInfo.next()) {
                    alamatKirim = rsInfo.getString("alamat");
                }
            }

            double totalHarga = 0;
            for (KeranjangItem item : keranjang) {
                totalHarga += item.subtotal;
            }

            String sqlPesanan = "INSERT INTO pesanan (id_toko, id_user, tanggal_pesan, alamat_kirim, total_harga, status) " +
                                "VALUES (?, ?, NOW(), ?, ?, 'pending')";
            PreparedStatement ps1 = conn.prepareStatement(sqlPesanan, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, idToko); ps1.setInt(2, idUser); ps1.setString(3, alamatKirim); ps1.setDouble(4, totalHarga);
            ps1.executeUpdate();
            
            ResultSet rs = ps1.getGeneratedKeys();
            rs.next();
            int idPesananBaru = rs.getInt(1);

            String sqlDetail = "INSERT INTO detail_pesanan (id_pesanan, id_barang, jumlah, subtotal) VALUES (?, ?, ?, ?)";
            String sqlStok = "UPDATE barang SET stok_gudang = stok_gudang - ? WHERE id_barang = ?";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            PreparedStatement psStok = conn.prepareStatement(sqlStok);
            
            for (KeranjangItem item : keranjang) {
                psDetail.setInt(1, idPesananBaru); psDetail.setString(2, item.idBarang);
                psDetail.setInt(3, item.jumlah); psDetail.setDouble(4, item.subtotal);
                psDetail.executeUpdate();
                
                psStok.setInt(1, item.jumlah); psStok.setString(2, item.idBarang);
                psStok.executeUpdate();
            }
            conn.commit(); 
        } catch(Exception e) {
            if(conn != null) conn.rollback(); 
            throw new Exception("Gagal Checkout: " + e.getMessage());
        } finally {
            if(conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }

    public static int getPesananSelesaiCount(int idToko) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM pesanan WHERE id_toko = ? AND LOWER(status) = 'selesai'";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idToko);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) count = rs.getInt(1);
            }
        } catch (Exception e) { System.out.println("Error getPesananSelesaiCount: " + e.getMessage()); }
        return count;
    }

    public static double getTotalTagihan(int idToko) {
        double total = 0;
        String sql = "SELECT SUM(total_harga) AS tagihan FROM pesanan WHERE id_toko = ? AND status = 'pending'";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idToko);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) total = rs.getDouble("tagihan");
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    public static List<String> getAktivitasTerakhir(int idToko) {
        List<String> aktivitas = new ArrayList<>();
        String query = "SELECT status, tanggal_pesan FROM pesanan WHERE id_toko = ? ORDER BY tanggal_pesan DESC LIMIT 5";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idToko);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    String tanggal = rs.getString("tanggal_pesan");
                    aktivitas.add("Pesanan " + status + " pada " + tanggal);
                }
            }
        } catch (Exception e) { System.out.println("Error getAktivitasTerakhir: " + e.getMessage()); }
        return aktivitas;
    }

    public static List<Pesanan> getRiwayatPesananMitra(int idToko) throws Exception {
        List<Pesanan> riwayat = new ArrayList<>();
        String sql = "SELECT id_pesanan, tanggal_pesan, total_harga, status FROM pesanan WHERE id_toko = ? ORDER BY tanggal_pesan DESC";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idToko);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Membungkus ke dalam Objek Pesanan
            riwayat.add(new Pesanan(
                rs.getInt("id_pesanan"),
                "", 
                "", 
                rs.getString("tanggal_pesan"),
                rs.getDouble("total_harga"),
                rs.getString("status")
            ));
        }
    }
    return riwayat;
}
    
    public static List<Pesanan> getAllPesananAdmin() throws Exception {
    List<Pesanan> list = new ArrayList<>();
    String sql = "SELECT * FROM ViewPesananAdmin ORDER BY CASE WHEN status = 'pending' THEN 1 ELSE 2 END, tanggal_pesan DESC";
    
    try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            // Membungkus data ke dalam Objek Pesanan
            list.add(new Pesanan(
                rs.getInt("id_pesanan"),
                rs.getString("nama_toko"),
                rs.getString("alamat_kirim"), 
                rs.getString("tanggal_pesan"), 
                rs.getDouble("total_harga"), // Mengambil data harga sebagai tipe Double
                rs.getString("status")
            ));
        }
    }
    return list;
}

    public static java.util.Map<String, Integer> getVolumePesananPerHari() {
        java.util.Map<String, Integer> dataMap = new java.util.LinkedHashMap<>();
        String query = "SELECT DATE(tanggal_pesan) as tgl, COUNT(*) as total FROM pesanan GROUP BY DATE(tanggal_pesan) ORDER BY DATE(tanggal_pesan) ASC LIMIT 7";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dataMap.put(rs.getString("tgl"), rs.getInt("total"));
            }
        } catch (Exception e) { System.out.println("Error getVolumePesananPerHari: " + e.getMessage()); }
        return dataMap;
    }

    public static void accPesananAdmin(String idPesanan, String idKurir) throws Exception {
        Connection conn = null;
        try {
            conn = KoneksiDB.getKoneksi();
            conn.setAutoCommit(false); 
            String sqlUpdatePesanan = "UPDATE pesanan SET status = 'diproses' WHERE id_pesanan = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(sqlUpdatePesanan)) {
                ps1.setString(1, idPesanan);
                ps1.executeUpdate();
            }
            String sqlInsertPengiriman = "INSERT INTO pengiriman (id_pesanan, id_kurir, status) VALUES (?, ?, 'pending')";
            try (PreparedStatement ps2 = conn.prepareStatement(sqlInsertPengiriman)) {
                ps2.setString(1, idPesanan);
                ps2.setString(2, idKurir);
                ps2.executeUpdate();
            }
            conn.commit(); 
        } catch (Exception e) {
            if (conn != null) conn.rollback(); 
            throw new Exception("Gagal ACC & Assign Kurir: " + e.getMessage());
        } finally {
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
    }

    public static void konfirmasiMitraSelesai(String id) throws Exception {
        String sql = "UPDATE pesanan SET status = 'selesai' WHERE id_pesanan = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }
}