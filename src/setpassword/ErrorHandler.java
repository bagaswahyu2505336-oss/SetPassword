package setpassword;

import javafx.scene.control.Alert;
import javafx.application.Platform;

public class ErrorHandler {

    // Menangani Exception/Error Sistem (Menampilkan ke console & Alert)
    public static void handleException(String konteks, Exception e) {
        // 1. Log untuk Developer (Bisa diganti jadi log ke file TXT nantinya)
        System.err.println("[ERROR - " + konteks + "] " + e.getMessage());
        e.printStackTrace();

        // 2. Alert untuk User (Pastikan berjalan di UI Thread)
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sistem Grosir Error");
            alert.setHeaderText("Terjadi Kesalahan: " + konteks);
            alert.setContentText(e.getMessage() != null ? e.getMessage() : "Error tidak diketahui.");
            alert.showAndWait();
        });
    }

    // Menangani Peringatan Biasa (Validasi input kosong, dll)
    public static void tampilkanPeringatan(String judul, String pesan) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(judul);
            alert.setContentText(pesan);
            alert.showAndWait();
        });
    }

    // Menangani Informasi Sukses
    public static void tampilkanSukses(String judul, String pesan) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText(judul);
            alert.setContentText(pesan);
            alert.showAndWait();
        });
    }
}