package Model;

public class Barang {
    private String id_barang, sku, nama, harga, stok_gudang, foto;


    public Barang(String id_barang, String sku, String nama, String harga, String stok_gudang, String foto) {
        this.id_barang = id_barang;
        this.sku = sku;
        this.nama = nama;
        this.harga = harga; 
        this.stok_gudang = stok_gudang;
        this.foto = foto;
    }
    
    public String getIdBarang() { return id_barang; }
    public String getSku() { return sku; } 
    public String getNama() { return nama; }
    public String getHarga() { return harga; }
    public String getStokGudang() { return stok_gudang; }
    public String getFoto() { return foto; }
}
