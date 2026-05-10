package Role_Mitra;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Database.PesananDAO;

public class StrukTagihanPemesanan {

    private VBox kontenStruk;

    public void tampilkan(String mitra, String toko, String alamat, List<PesananDAO.KeranjangItem> items, String total) {
        Stage stage = new Stage();
        stage.setTitle("Nota Tagihan Digital - " + toko);

        kontenStruk = new VBox(8);
        kontenStruk.setPadding(new Insets(25));
        kontenStruk.setStyle("-fx-background-color: white; -fx-font-family: 'Monospaced';");

        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label lblLogo = new Label("GROSIRKU DIGITAL");
        lblLogo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        header.getChildren().addAll(
            lblLogo, 
            new Label("Waktu: " + dtf.format(LocalDateTime.now())), 
            new Label("==========================================")
        );

        GridPane gridInfo = new GridPane();
        gridInfo.setVgap(2);
        gridInfo.add(new Label("Mitra  : "), 0, 0); gridInfo.add(new Label(mitra), 1, 0);
        gridInfo.add(new Label("Toko   : "), 0, 1); gridInfo.add(new Label(toko), 1, 1);
        gridInfo.add(new Label("Alamat : "), 0, 2); 
        Label lblAlamat = new Label(alamat); lblAlamat.setWrapText(true); lblAlamat.setMaxWidth(220);
        gridInfo.add(lblAlamat, 1, 2);
        gridInfo.add(new Label("=========================================="), 0, 3, 2, 1);

        VBox table = new VBox(5);
        table.getChildren().add(new Label("Item            Qty      Subtotal"));
        table.getChildren().add(new Label("------------------------------------------"));
        for (PesananDAO.KeranjangItem item : items) {
            String nama = item.namaBarang.length() > 15 ? item.namaBarang.substring(0, 13) + ".." : item.namaBarang;
            String row = String.format("- %-14s %-7d Rp%-10.0f", nama, item.jumlah, item.subtotal);
            table.getChildren().add(new Label(row));
        }
        table.getChildren().add(new Label("------------------------------------------"));

        VBox footerTotal = new VBox(5);
        footerTotal.setAlignment(Pos.CENTER_RIGHT);
        Label lblTotal = new Label("TOTAL TAGIHAN: Rp " + total);
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        footerTotal.getChildren().add(lblTotal);

        VBox paymentInfo = new VBox(2);
        paymentInfo.setPadding(new Insets(10, 0, 0, 0));
        paymentInfo.getChildren().addAll(
            new Label("--- METODE PEMBAYARAN ---"),
            new Label("E-WALET:"),
            new Label("  DANA : 085607753219"),
            new Label("  SPAY : 085792668573"),
            new Label("BANK:"),
            new Label("  BNI  : 1934080874"),
            new Label("  SEA  : 901388724591"),
            new Label("-------------------------"),
            new Label("Status: PENDING (Menunggu Konfirmasi)")
        );

        kontenStruk.getChildren().addAll(header, gridInfo, table, footerTotal, paymentInfo);

        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(15));
        
        Button btnSaveImage = new Button("📸 Simpan Gambar (JPG)");
        btnSaveImage.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        Button btnClose = new Button("Tutup");
        btnClose.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        btnSaveImage.setOnAction(e -> simpanSebagaiGambar(stage));
        btnClose.setOnAction(e -> stage.close());
        
        actions.getChildren().addAll(btnSaveImage, btnClose);

        VBox layoutUtama = new VBox(kontenStruk, actions);
        layoutUtama.setStyle("-fx-background-color: #f0f0f0;");
        
        Scene scene = new Scene(layoutUtama);
        stage.setScene(scene);
        stage.show();
    }

    private void simpanSebagaiGambar(Stage stage) {
        WritableImage snapshot = kontenStruk.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Struk ke Galeri");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Image", "*.jpg"));
        fileChooser.setInitialFileName("Nota_Grosir_" + System.currentTimeMillis() + ".jpg");

        File destinationFile = fileChooser.showSaveDialog(stage);

        if (destinationFile != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "jpg", destinationFile);
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Berhasil");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Struk Digital berhasil disimpan sebagai JPG!");
                successAlert.showAndWait();
                
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Gagal menyimpan foto: " + ex.getMessage()).showAndWait();
            }
        }
    }
}