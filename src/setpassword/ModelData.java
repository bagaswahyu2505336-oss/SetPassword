package setpassword;

public class ModelData {

    // Model untuk Keranjang Belanja Mitra
    public static class KeranjangBelanja {
        private String idBarang, namaBarang;
        private int jumlah;
        private double harga, subtotal;

        public KeranjangBelanja(String idBarang, String namaBarang, int jumlah, double harga) {
            this.idBarang = idBarang; this.namaBarang = namaBarang;
            this.jumlah = jumlah; this.harga = harga;
            this.subtotal = harga * jumlah;
        }
        public String getIdBarang() { return idBarang; }
        public String getNamaBarang() { return namaBarang; }
        public int getJumlah() { return jumlah; }
        public double getHarga() { return harga; }
        public double getSubtotal() { return subtotal; }
    }

    // Model untuk Jadwal Kurir
    public static class JadwalKurir {
        private String idPengiriman, namaToko, alamat, status;

        public JadwalKurir(String idPengiriman, String namaToko, String alamat, String status) {
            this.idPengiriman = idPengiriman; this.namaToko = namaToko; 
            this.alamat = alamat; this.status = status;
        }
        public String getIdPengiriman() { return idPengiriman; }
        public String getNamaToko() { return namaToko; }
        public String getAlamat() { return alamat; }
        public String getStatus() { return status; }
    }
}