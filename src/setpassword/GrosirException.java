package setpassword;

// Custom Exception khusus untuk aplikasi Grosirku
public class GrosirException extends Exception {
    
    public GrosirException(String pesan) {
        super(pesan);
    }

    public GrosirException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}