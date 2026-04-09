package setpassword;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FungsiDB {

    // ==========================================
    // SECTION: METHOD BARU UNTUK DASHBOARD (REAL-TIME)
    // ==========================================
    public static String getStatusToko(int idToko) {
        String status = "Aktif";
        String sql = "SELECT status_kerjasama FROM toko_mitra WHERE id_toko = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idToko);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) status = rs.getString("status_kerjasama");
        } catch (Exception e) { e.printStackTrace(); }
        return status;
    }

    public static int getPesananSelesaiCount(int idToko) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM pesanan WHERE id_toko = ? AND status = 'success'";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idToko);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt("total");
        } catch (Exception e) { e.printStackTrace(); }
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
        String sql = "SELECT t.keterangan FROM tracking_pengiriman t " +
                     "JOIN pengiriman p ON t.id_pengiriman = p.id_pengiriman " +
                     "JOIN pesanan ps ON p.id_pesanan = ps.id_pesanan " +
                     "WHERE ps.id_toko = ? ORDER BY t.waktu DESC LIMIT 3";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idToko);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                aktivitas.add(rs.getString("keterangan"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        if (aktivitas.isEmpty()) aktivitas.add("Belum ada aktivitas tracking.");
        return aktivitas;
    }

    // ==========================================
    // SECTION: METHOD LAMA (TIDAK DIUBAH AGAR TIDAK EROR)
    // ==========================================

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
        String sql = "DELETE FROM barang WHERE id_barang = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id_barang); ps.executeUpdate();
        }
    }

    public static List<ModelData.JadwalKurir> getJadwalKurir(int idKurir) throws GrosirException {
        List<ModelData.JadwalKurir> daftar = new ArrayList<>();
        String sql = "SELECT p.id_pengiriman, t.nama_toko, t.alamat, p.status " +
                 "FROM pengiriman p " +
                 "JOIN pesanan ps ON p.id_pesanan = ps.id_pesanan " + // Sambungkan pengiriman ke pesanan
                 "JOIN toko_mitra t ON ps.id_toko = t.id_toko " +     // Sambungkan pesanan ke toko
                 "WHERE p.id_kurir = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKurir);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                daftar.add(new ModelData.JadwalKurir(
                    rs.getString("id_pengiriman"), rs.getString("nama_toko"),
                    rs.getString("alamat"), rs.getString("status")
                ));
            }
        } catch (SQLException e) { 
            throw new GrosirException("Gagal muat jadwal: " + e.getMessage()); 
        }
        return daftar;
    }

    public static String hitungTotalStokFisik() {
        try (Connection conn = KoneksiDB.getKoneksi(); 
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT SUM(stok_gudang) FROM barang")) {
            if (rs.next()) return rs.getInt(1) + " Unit";
        } catch (Exception e) { e.printStackTrace(); }
        return "0 Unit";
    }

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

    public static void checkoutKeranjang(int idToko, List<KeranjangItem> keranjang) throws Exception {
        Connection conn = null;
        try {
            conn = KoneksiDB.getKoneksi();
            conn.setAutoCommit(false); 
            double totalHarga = 0;
            for (KeranjangItem item : keranjang) totalHarga += item.subtotal;

            String sqlPesanan = "INSERT INTO pesanan (id_toko, total_harga, status) VALUES (?, ?, 'pending')";
            PreparedStatement ps1 = conn.prepareStatement(sqlPesanan, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, idToko); ps1.setDouble(2, totalHarga);
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

    public static List<String[]> getRiwayatPesananMitra(int idToko) throws Exception {
        List<String[]> riwayat = new ArrayList<>();
        String sql = "SELECT id_pesanan, tanggal_pesan, total_harga, status FROM pesanan WHERE id_toko = ? ORDER BY tanggal_pesan DESC";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idToko);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                riwayat.add(new String[]{
                    rs.getString("id_pesanan"), rs.getString("tanggal_pesan"),
                    rs.getString("total_harga"), rs.getString("status")
                });
            }
        }
        return riwayat;
    }

    // ==========================================
    // SECTION: METHOD TAMBAHAN UNTUK SINKRONISASI UI (AGAR TIDAK ERROR)
    // ==========================================

    public static List<String> getDaftarKabupaten() throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT nama_kabupaten FROM wilayah_kabupaten";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("nama_kabupaten"));
        }
        return list;
    }

    public static List<String> getDaftarKecamatan(String kab) throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT nama_kecamatan FROM wilayah_kecamatan WHERE kabupaten = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kab);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("nama_kecamatan"));
        }
        return list;
    }
    public static void ajukanPendaftaranMitra(String nama, String hp, String dummy, String toko, String alamat, String user, String pass) throws Exception {
        String sql = "INSERT INTO calon_mitra (nama_pemilik, no_hp, nama_toko, alamat, username, password, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama); 
            ps.setString(2, hp); 
            ps.setString(3, toko);
            ps.setString(4, alamat); 
            ps.setString(5, user); 
            ps.setString(6, pass);
            ps.executeUpdate();
        }
    }

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
    
    public static List<String[]> getCalonMitraPending() throws Exception {
        List<String[]> list = new ArrayList<>();
        // Pastikan di sini tertulis id_pendaftaran dan nama_lengkap
        String sql = "SELECT id_pendaftaran, nama_lengkap, nama_toko, no_hp, username FROM calon_mitra WHERE status = 'pending'";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id_pendaftaran"), 
                    rs.getString("nama_lengkap"), 
                    rs.getString("nama_toko"), 
                    rs.getString("no_hp"), 
                    rs.getString("username")
                });
            }
        }
        return list;
    }
    
    public static List<String[]> getAllUserAktif() throws Exception {
        List<String[]> list = new ArrayList<>();
        Connection conn = KoneksiDB.getKoneksi();
        
        // 1. Ambil data Mitra yang sudah di-ACC (Status = aktif)
        try {
            String sqlMitra = "SELECT id_calon, username, 'Mitra' as role FROM calon_mitra WHERE status = 'aktif'";
            PreparedStatement ps1 = conn.prepareStatement(sqlMitra);
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                list.add(new String[]{rs1.getString("id_calon"), rs1.getString("username"), rs1.getString("role")});
            }
        } catch (Exception e) { 
            System.out.println("Abaikan jika tidak ada tabel Mitra: " + e.getMessage()); 
        }

        // 2. Ambil data Kurir (Asumsi nama tabelnya 'kurir')
        try {
            String sqlKurir = "SELECT id_kurir, username, 'Kurir' as role FROM kurir";
            PreparedStatement ps2 = conn.prepareStatement(sqlKurir);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                list.add(new String[]{rs2.getString("id_kurir"), rs2.getString("username"), rs2.getString("role")});
            }
        } catch (Exception e) { 
            System.out.println("Abaikan jika tidak ada tabel Kurir: " + e.getMessage()); 
        }
        
        // 3. Fallback jika Anda ternyata menggunakan tabel 'users'
        try {
            String sqlUsers = "SELECT id_user, username, role FROM users WHERE role != 'admin'";
            PreparedStatement ps3 = conn.prepareStatement(sqlUsers);
            ResultSet rs3 = ps3.executeQuery();
            while (rs3.next()) {
                list.add(new String[]{rs3.getString("id_user"), rs3.getString("username"), rs3.getString("role")});
            }
        } catch (Exception e) {}

        conn.close();
        return list;
    }

    // Fungsi baru untuk Cabut Akses (Menggantikan hapusUser)
    public static void cabutAksesUser(String id, String role) throws Exception {
        try (Connection conn = KoneksiDB.getKoneksi()) {
            if (role.equalsIgnoreCase("Mitra")) {
                // Untuk Mitra, kita tidak menghapus datanya, tapi mengubah statusnya jadi 'dicabut' agar tidak bisa login
                String sql = "UPDATE calon_mitra SET status = 'dicabut' WHERE id_calon = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                ps.executeUpdate();
            } else if (role.equalsIgnoreCase("Kurir")) {
                // Jika Kurir, kita hapus akunnya dari database
                String sql = "DELETE FROM kurir WHERE id_kurir = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                ps.executeUpdate();
            } else {
                // Default fallback ke tabel users
                String sql = "DELETE FROM users WHERE id_user = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                ps.executeUpdate();
            }
        }
    }

    public static void accMitra(String id) throws Exception {
        String sql = "UPDATE calon_mitra SET status = 'aktif' WHERE id_calon = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }

    public static void tolakMitra(String id) throws Exception {
        String sql = "UPDATE calon_mitra SET status = 'ditolak' WHERE id_calon = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }

    public static List<String[]> getDaftarPesananByStatus(String status) throws Exception {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT p.id_pesanan, t.nama_toko, t.alamat, p.tanggal_pesan, p.total_harga FROM pesanan p JOIN toko_mitra t ON p.id_toko = t.id_toko WHERE p.status = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id_pesanan"), rs.getString("nama_toko"), rs.getString("alamat"),
                    rs.getString("tanggal_pesan"), rs.getString("total_harga")
                });
            }
        }
        return list;
    }

    public static void accPesananAdmin(String id) throws Exception {
        String sql = "UPDATE pesanan SET status = 'diproses' WHERE id_pesanan = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }

    public static void kurirKirimBarang(String id) throws Exception {
        String sql = "UPDATE pesanan SET status = 'dikirim' WHERE id_pesanan = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }

    public static void kurirSampaiTujuan(String id) throws Exception {
        String sql = "UPDATE pesanan SET status = 'sampai_tujuan' WHERE id_pesanan = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }

    public static void konfirmasiMitraSelesai(String id) throws Exception {
        String sql = "UPDATE pesanan SET status = 'selesai' WHERE id_pesanan = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id); ps.executeUpdate();
        }
    }

    public static boolean updateStatusPengiriman(String id, String status) throws GrosirException {
        String sql = "UPDATE pengiriman SET status = ? WHERE id_pengiriman = ?";
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new GrosirException("Gagal update status pengiriman: " + e.getMessage());
        }
    }
    
    public static List<String[]> getAllPesananAdmin() throws Exception {
        List<String[]> list = new ArrayList<>();
        // Query ini akan mengurutkan status 'pending' di paling atas agar mudah di ACC Admin
        String sql = "SELECT p.id_pesanan, t.nama_toko, t.alamat, p.tanggal_pesan, p.total_harga, p.status " +
                     "FROM pesanan p JOIN toko_mitra t ON p.id_toko = t.id_toko " +
                     "ORDER BY CASE WHEN p.status = 'pending' THEN 1 ELSE 2 END, p.tanggal_pesan DESC";
        
        try (Connection conn = KoneksiDB.getKoneksi(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id_pesanan"), 
                    rs.getString("nama_toko"), 
                    rs.getString("alamat"),
                    rs.getString("tanggal_pesan"), 
                    rs.getString("total_harga"), 
                    rs.getString("status") // Kita butuh data status untuk Real-Time checking
                });
            }
        }
        return list;
    }
}