package setpassword;

// File: Pesanan.java
// Class ini sekarang berdiri sendiri secara public sehingga bisa dibaca oleh TableView JavaFX

public class Pesanan {
    private String id;
    private String nama;
    private String tanggal;
    private String total;
    private String status;

    // Constructor
    public Pesanan(String id, String nama, String tanggal, String total, String status) {
        this.id = id;
        this.nama = nama;
        this.tanggal = tanggal;
        this.total = total;
        this.status = status;
    }

    // Getter methods (Wajib ada agar PropertyValueFactory bisa membaca datanya)
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}
