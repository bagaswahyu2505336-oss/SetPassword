package setpassword;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class HalamanKelolaUser {

    private VBox boxPending = new VBox(15);
    private VBox boxAktif = new VBox(10);

    public VBox getTampilan() {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #F4F7F6; -fx-padding: 20;");

        Label lblJudul = new Label("👥 User Management");
        lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 22px;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabPersetujuan = new Tab("Persetujuan Mitra");
        ScrollPane scrollPending = new ScrollPane(boxPending);
        scrollPending.setFitToWidth(true);
        scrollPending.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        tabPersetujuan.setContent(scrollPending);
        
        // UPDATE REAL-TIME: Refresh data setiap kali Tab diklik
        tabPersetujuan.setOnSelectionChanged(e -> {
            if (tabPersetujuan.isSelected()) refreshData();
        });

        Tab tabUserAktif = new Tab("User Aktif");
        ScrollPane scrollAktif = new ScrollPane(boxAktif);
        scrollAktif.setFitToWidth(true);
        scrollAktif.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        tabUserAktif.setContent(scrollAktif);
        
        // UPDATE REAL-TIME: Refresh data setiap kali Tab diklik
        tabUserAktif.setOnSelectionChanged(e -> {
            if (tabUserAktif.isSelected()) refreshData();
        });

        tabPane.getTabs().addAll(tabPersetujuan, tabUserAktif);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        root.getChildren().addAll(lblJudul, tabPane);
        refreshData();
        return root;
    }

    private void refreshData() {
        boxPending.getChildren().clear();
        boxAktif.getChildren().clear();

        // LOAD DATA CALON MITRA (PENDING)
        try {
            List<String[]> calonMitra = FungsiDB.getCalonMitraPending();
            if (calonMitra.isEmpty()) {
                // ========================================================
                // BAGIAN YANG DIUBAH SESUAI REQUEST ANDA
                // ========================================================
                Label lblKosong = new Label("Tidak ada user baru mendaftar");
                lblKosong.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D; -fx-font-style: italic;");
                boxPending.getChildren().add(lblKosong);
            } else {
                for (String[] cm : calonMitra) {
                    boxPending.getChildren().add(buatCardCalonMitra(cm[0], cm[1], cm[2], cm[3], cm[4]));
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            Label lblErr = new Label("❌ Gagal memuat data Pending: " + e.getMessage());
            lblErr.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            boxPending.getChildren().add(lblErr);
        }

        // LOAD DATA USER AKTIF
        try {
            List<String[]> userAktif = FungsiDB.getAllUserAktif();
            if (userAktif.isEmpty()) {
                Label lblKosongAktif = new Label("Tidak ada user aktif selain Admin yang tercatat.");
                lblKosongAktif.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D; -fx-font-style: italic;");
                boxAktif.getChildren().add(lblKosongAktif);
            } else {
                for (String[] usr : userAktif) {
                    // usr[0] = id, usr[1] = username, usr[2] = role
                    boxAktif.getChildren().add(buatCardUserAktif(usr[0], usr[1], usr[2]));
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            Label lblErr = new Label("❌ Gagal memuat User Aktif:\n" + e.getMessage());
            lblErr.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            boxAktif.getChildren().add(lblErr);
        }
    }

    private HBox buatCardCalonMitra(String id, String nama, String toko, String hp, String usrLogin) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        VBox info = new VBox(5);
        Label lblToko = new Label("🏪 " + toko + " (Pemilik: " + nama + ")");
        lblToko.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblDetail = new Label("📞 " + hp + " | 👤 Req Username: " + usrLogin);
        lblDetail.setStyle("-fx-text-fill: gray;");
        info.getChildren().addAll(lblToko, lblDetail);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAcc = new Button("✅ Terima (ACC)");
        btnAcc.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAcc.setOnAction(e -> {
            try { 
                FungsiDB.accMitra(id); 
                refreshData(); 
                new Alert(Alert.AlertType.INFORMATION, "Akun Mitra disetujui!").showAndWait();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        Button btnTolak = new Button("❌ Tolak");
        btnTolak.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-cursor: hand;");
        btnTolak.setOnAction(e -> {
            try { FungsiDB.tolakMitra(id); refreshData(); } catch (Exception ex) { ex.printStackTrace(); }
        });

        card.getChildren().addAll(info, spacer, btnAcc, btnTolak);
        return card;
    }

    private HBox buatCardUserAktif(String id, String username, String role) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E0E0E0;");

        VBox info = new VBox(5);
        Label lblUser = new Label("👤 Username: " + username);
        lblUser.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblRole = new Label("Role / Status: " + role.toUpperCase());
        lblRole.setStyle("-fx-text-fill: #1F4A75;");
        info.getChildren().addAll(lblUser, lblRole);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnHapus = new Button("🚫 Cabut Akses Login");
        btnHapus.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand;");
        btnHapus.setOnAction(e -> {
            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, "Cabut akses login untuk " + username + " ("+role+")?", ButtonType.YES, ButtonType.NO);
            konfirmasi.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    try {
                        FungsiDB.cabutAksesUser(id, role);
                        refreshData();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, "Gagal mencabut akses: \n" + ex.getMessage()).showAndWait();
                    }
                }
            });
        });

        card.getChildren().addAll(info, spacer, btnHapus);
        return card;
    }
}