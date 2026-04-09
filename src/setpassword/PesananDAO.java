package setpassword;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PesananDAO {
    
    // FUNGSI UNTUK MENGHITUNG JUMLAH PESANAN (Untuk Card Dashboard)
    public static String hitungTotalPesanan() {
        String sql = "SELECT COUNT(*) AS total FROM pesanan";
        try (Connection conn = KoneksiDB.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total") + " Pesanan";
            }
        } catch (Exception e) {
            return "0 Pesanan";
        }
        return "0 Pesanan";
    }

    // FUNGSI UNTUK MENGHITUNG TOTAL MITRA (Untuk Card Dashboard)
    public static String hitungTotalMitra() {
        String sql = "SELECT COUNT(*) AS total FROM toko_mitra";
        try (Connection conn = KoneksiDB.getKoneksi();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total") + " Mitra";
            }
        } catch (Exception e) {
            return "0 Mitra";
        }
        return "0 Mitra";
    }

    // ... (Fungsi buatPesananBaru dan getSemuaPesananAdmin tetap sama seperti sebelumnya) ...
    public static void buatPesananBaru(int idUser, int idToko, String alamatKirim, List<DetailPesananTemp> daftarBarang) throws GrosirException {
        // ... kode transaksi yang sudah kita buat sebelumnya ...
    }

    public static List<Pesanan> getSemuaPesananAdmin() throws GrosirException {
        // ... kode JOIN yang sudah kita buat sebelumnya ...
        return new ArrayList<>(); // sesuaikan dengan isi sebelumnya
    }

    public static class DetailPesananTemp {
        public int idBarang; public int jumlah; public double harga;
        public DetailPesananTemp(int idBarang, int jumlah, double harga) {
            this.idBarang = idBarang; this.jumlah = jumlah; this.harga = harga;
        }
    }
}