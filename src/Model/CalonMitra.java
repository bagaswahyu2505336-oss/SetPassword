package Model;

public class CalonMitra {
    private int idPendaftaran;
    private String namaLengkap;
    private String namaToko;
    private String noHp;
    private String username;

    public CalonMitra(int idPendaftaran, String namaLengkap, String namaToko, String noHp, String username) {
        this.idPendaftaran = idPendaftaran;
        this.namaLengkap = namaLengkap;
        this.namaToko = namaToko;
        this.noHp = noHp;
        this.username = username;
    }

    public int getIdPendaftaran() { return idPendaftaran; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getNamaToko() { return namaToko; }
    public String getNoHp() { return noHp; }
    public String getUsername() { return username; }
}