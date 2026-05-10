package Error_Exception;

import javafx.scene.control.Alert;
import javafx.application.Platform;

public class ErrorHandler {

    
    public static void handleException(String konteks, Exception e) {
        System.err.println("[ERROR - " + konteks + "] " + e.getMessage());
        e.printStackTrace();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sistem Grosir Error");
            alert.setHeaderText("Terjadi Kesalahan: " + konteks);
            alert.setContentText(e.getMessage() != null ? e.getMessage() : "Error tidak diketahui.");
            alert.showAndWait();
        });
    }

    public static void tampilkanPeringatan(String judul, String pesan) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText(judul);
            alert.setContentText(pesan);
            alert.showAndWait();
        });
    }

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