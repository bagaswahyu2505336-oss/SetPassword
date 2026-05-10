package Error_Exception;

public class GrosirException extends Exception {
    
    public GrosirException(String pesan) {
        super(pesan);
    }

    public GrosirException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}