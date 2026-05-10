package Model;

public class ModelData {
    public static class JadwalKurir {
        private String idPengiriman;
        private String namaToko;
        private String alamat;
        private String status;
        private String namaPemesan;
        private String noHp;

        public JadwalKurir(String idPengiriman, String namaToko, String alamat, String status, String namaPemesan, String noHp) {
            this.idPengiriman = idPengiriman;
            this.namaToko = namaToko;
            this.alamat = alamat;
            this.status = status;
            this.namaPemesan = namaPemesan; 
            this.noHp = noHp;               
        }

        public String getIdPengiriman() { return idPengiriman; }
        public String getNamaToko() { return namaToko; }
        public String getAlamat() { return alamat; }
        public String getStatus() { return status; }
        public String getNamaPemesan() { return namaPemesan; }
        public String getNoHp() { return noHp; }
    }
}