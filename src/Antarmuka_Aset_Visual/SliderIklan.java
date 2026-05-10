package Antarmuka_Aset_Visual;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow; 
import javafx.scene.paint.Color;       
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

/**
 * Class untuk mengelola papan iklan bergeser (Slider) pada Dashboard.
 */
public class SliderIklan {

    private StackPane container;
    private List<Pane> daftarIklan; 
    private int indexAktif = 0;
    private Timeline timer;

    public SliderIklan() {
        container = new StackPane();
        container.setPrefHeight(180); 
        daftarIklan = new ArrayList<>();

        // Menambahkan slide ke dalam daftar
        daftarIklan.add(buatSlideSapaan());
        daftarIklan.add(buatSlideRebahanDenganFoto());

        // Menampilkan slide pertama jika daftar tidak kosong
        if (!daftarIklan.isEmpty()) {
            container.getChildren().add(daftarIklan.get(0));
            mulaiAnimasi();
        }
    }

    /**
     * Desain Slide 1: Ucapan Selamat Datang (Gradasi Hijau-Biru)
     */
    private VBox buatSlideSapaan() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 0, 50));
        // Menggunakan gaya gradasi sesuai permintaan
        box.setStyle("-fx-background-color: linear-gradient(to right, #1F4A75, #28B463); " +
                     "-fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Label lblWelcome = new Label("Selamat Datang di Grosirku!");
        lblWelcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label lblSub = new Label("Kelola stok tokomu dengan mudah dan hemat waktu.");
        lblSub.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 15px;");
        
        box.getChildren().addAll(lblWelcome, lblSub);
        return box;
    }

    /**
     * Desain Slide 2: Iklan Rebahan (Gradasi Ungu-Orange + Foto)
     */
    private HBox buatSlideRebahanDenganFoto() {
        HBox rootRow = new HBox();
        rootRow.setAlignment(Pos.CENTER_LEFT);
        
        // Gradasi Ungu ke Orange Terang
        rootRow.setStyle("-fx-background-color: linear-gradient(to right, #6A11CB, #FF8C00); " +
                        "-fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        // --- KIRI: BAGIAN TEKS ---
        VBox boxTeks = new VBox(10);
        boxTeks.setAlignment(Pos.CENTER_LEFT);
        boxTeks.setPadding(new Insets(0, 20, 0, 50));
        HBox.setHgrow(boxTeks, Priority.ALWAYS); 

        Label lblJudul = new Label("SEKARANG BELANJA UNTUK KEBUTUHAN\nTOKO BISA DENGAN REBAHAN LOH!!!");
        lblJudul.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Segoe UI';");
        lblJudul.setWrapText(true);

        Label lblSub = new Label("Cari kebutuhan grosir toko tanpa repot, dari tempat santai mu");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-style: italic;");

        boxTeks.getChildren().addAll(lblJudul, lblSub);

        // --- KANAN: BAGIAN FOTO ---
        ImageView imgView = new ImageView();
        imgView.setFitHeight(180); 
        imgView.setPreserveRatio(true); 
        
        try {
            // Mencoba mengambil gambar menggunakan URL Resource
            // Path "/" berarti mencari dari root folder 'src' atau 'build/classes'
            URL imageUrl = getClass().getResource("/setpassword/iklan_rebahan.png");
            
            // Jika tidak ketemu dengan path awal, coba cari relatif terhadap class
            if (imageUrl == null) {
                imageUrl = getClass().getResource("iklan_rebahan.png");
            }
            
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm());
                imgView.setImage(image);
                
                // Efek bayangan pada gambar agar terlihat lebih menyatu
                DropShadow ds = new DropShadow();
                ds.setRadius(15);
                ds.setColor(Color.rgb(0, 0, 0, 0.4));
                imgView.setEffect(ds);
                
                System.out.println("LOG: Gambar berhasil dimuat dari: " + imageUrl);
            } else {
                // Log jika file benar-benar tidak ditemukan di folder build
                System.out.println("LOG: File /setpassword/iklan_rebahan.png TIDAK DITEMUKAN!");
            }
        } catch (Exception e) {
            System.out.println("Error saat memuat gambar: " + e.getMessage());
        }

        rootRow.getChildren().addAll(boxTeks, imgView);
        return rootRow;
    }

    /**
     * Menjalankan animasi perpindahan slide otomatis setiap 5 detik.
     */
    private void mulaiAnimasi() {
        timer = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            indexAktif = (indexAktif + 1) % daftarIklan.size();
            container.getChildren().clear();
            container.getChildren().add(daftarIklan.get(indexAktif));
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /**
     * Mengembalikan StackPane (container) untuk dipasang di DashboardMitra.
     */
    public StackPane getTampilan() {
        return container;
    }
}