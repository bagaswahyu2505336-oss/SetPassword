package Model;

public class Pesanan {
    private int id;
    private String namaToko;
    private String alamat;
    private String tanggal;
    private double total;
    private String status;

    public Pesanan(int id, String namaToko, String alamat, String tanggal, double total, String status) {
        this.id = id;
        this.namaToko = namaToko;
        this.alamat = alamat;
        this.tanggal = tanggal;
        this.total = total;
        this.status = status;
    }

    public int getId() { return id; }
    public String getNamaToko() { return namaToko; }
    public String getAlamat() { return alamat; }
    public String getTanggal() { return tanggal; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
}