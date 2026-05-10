package Login_Dan_Akses;

import Login_Dan_Akses.LoginForm;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.Optional;

public class FungsiLogout {

    public static void prosesLogout(Window windowSaatIni) {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Keluar");
        alert.setHeaderText("Anda yakin ingin keluar akun ini?");
        alert.setContentText("Pilih 'Iya' untuk kembali ke menu Login.");

        ButtonType btnIya = new ButtonType("Iya", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnTidak = new ButtonType("Tidak", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnIya, btnTidak);

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == btnIya) {
            Stage stageSaatIni = (Stage) windowSaatIni;
            stageSaatIni.close();
            try {
                LoginForm loginForm = new LoginForm(); 
                Stage stageLogin = new Stage();
                loginForm.start(stageLogin); 
                
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Gagal memuat halaman Login!").showAndWait();
            }
        }
    }
}