package setpassword;

public class Pengguna {
    // VARIABEL PRIVATE: Melindungi data pengguna
    private String id;
    private String nama;
    private String role;
    private String status;

    public Pengguna(String id, String nama, String role, String status) {
        this.id = id;
        this.nama = nama;
        this.role = role;
        this.status = status;
    }

    // GETTER: Wajib ada agar kolom tabel di DashboardAdmin bisa membaca datanya
    public String getId() { 
        return id; 
    }
    
    public String getNama() { 
        return nama; 
    }
    
    public String getRole() { 
        return role; 
    }
    
    public String getStatus() { 
        return status; 
    }
}