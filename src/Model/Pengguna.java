package Model;

public class Pengguna {
    private int id;
    private String nama;
    private String role;
    private String status;

    public Pengguna(int id, String nama, String role, String status) {
        this.id = id;
        this.nama = nama;
        this.role = role;
        this.status = status;
    }
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}