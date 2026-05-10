package Role_Admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import Database.MitraDAO;
import Database.UserDAO;
import Model.Pengguna;
import Model.CalonMitra;


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

        // --- TAB 1: PERSETUJUAN MITRA ---
        Tab tabPersetujuan = new Tab("Persetujuan Mitra");
        ScrollPane scrollPending = new ScrollPane(boxPending);
        scrollPending.setFitToWidth(true);
        scrollPending.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        tabPersetujuan.setContent(scrollPending);
        
        tabPersetujuan.setOnSelectionChanged(e -> {
            if (tabPersetujuan.isSelected()) refreshData();
        });

        // --- TAB 2: USER AKTIF ---
        Tab tabUserAktif = new Tab("User Aktif");
        ScrollPane scrollAktif = new ScrollPane(boxAktif);
        scrollAktif.setFitToWidth(true);
        scrollAktif.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        tabUserAktif.setContent(scrollAktif);
        
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
        try {
            List<CalonMitra> calonMitra = MitraDAO.getCalonMitraPending();
            if (calonMitra.isEmpty()) {
                Label lblKosong = new Label("Tidak ada user baru mendaftar");
                lblKosong.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D; -fx-font-style: italic;");
                boxPending.getChildren().add(lblKosong);
            } else {
                for (CalonMitra cm : calonMitra) {
                    boxPending.getChildren().add(buatCardCalonMitra(
                        String.valueOf(cm.getIdPendaftaran()), 
                        cm.getNamaLengkap(), 
                        cm.getNamaToko(), 
                        cm.getNoHp(), 
                        cm.getUsername()
                    ));
                }
            }
        } catch (Exception e) { 
            showErrorLabel(boxPending, "Gagal memuat data Pending", e);
        }

        try {
            List<Pengguna> userAktif = UserDAO.getAllUserAktif();
            if (userAktif.isEmpty()) {
                Label lblKosongAktif = new Label("Tidak ada user aktif selain Admin yang tercatat.");
                lblKosongAktif.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D; -fx-font-style: italic;");
                boxAktif.getChildren().add(lblKosongAktif);
            }else {
                for (Pengguna usr : userAktif) {
                    boxAktif.getChildren().add(buatCardUserAktif(
                            String.valueOf(usr.getId()), 
                            usr.getNama(), 
                            usr.getRole(), 
                            usr.getStatus()
                    ));
                }
            }
        } catch (Exception e) { 
            showErrorLabel(boxAktif, "Gagal memuat User Aktif", e);
        }
    }

    private HBox buatCardCalonMitra(String id, String nama, String toko, String hp, String usrLogin) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        VBox info = new VBox(5);
        Label lblToko = new Label("🏪 " + toko + " (Pemilik: " + nama + ")");
        lblToko.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C3E50;");
        Label lblDetail = new Label("📞 " + hp + " | 👤 Req Username: " + usrLogin);
        lblDetail.setStyle("-fx-text-fill: #7F8C8D;");
        info.getChildren().addAll(lblToko, lblDetail);

        Region spacer = new Region(); 
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- TOMBOL TERIMA (ACC) ---
        Button btnAcc = new Button("✅ Terima (ACC)");
        btnAcc.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        btnAcc.setOnAction(e -> {
            try { 
                MitraDAO.accMitra(id); // Memproses Transaksi di Database
                refreshData(); // Refresh list agar card yang sudah diproses hilang
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Persetujuan Berhasil");
                alert.setHeaderText(null);
                alert.setContentText("Mitra '" + toko + "' telah berhasil diaktifkan!");
                alert.showAndWait();
            } catch (Exception ex) { 
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Gagal ACC", "Terjadi kesalahan: " + ex.getMessage());
            }
        });

        // --- TOMBOL TOLAK ---
        Button btnTolak = new Button("❌ Tolak");
        btnTolak.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        btnTolak.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menolak pendaftaran " + toko + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    try { 
                        MitraDAO.tolakMitra(id); 
                        refreshData(); 
                        showAlert(Alert.AlertType.WARNING, "Ditolak", "Pendaftaran mitra telah ditolak.");
                    } catch (Exception ex) { 
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Gagal Tolak", ex.getMessage());
                    }
                }
            });
        });

        card.getChildren().addAll(info, spacer, btnAcc, btnTolak);
        return card;
    }

    private HBox buatCardUserAktif(String id, String username, String role, String status) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E0E0E0;");

        VBox info = new VBox(5);
        Label lblUser = new Label("👤 Username: " + username);
        lblUser.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblRole = new Label("Role / Status: " + role.toUpperCase());
        lblRole.setStyle("-fx-text-fill: #1F4A75; -fx-font-weight: bold;");
        info.getChildren().addAll(lblUser, lblRole);

        Region spacer = new Region(); 
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnHapus = new Button("🚫 Cabut Akses Login");
        btnHapus.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        if (status != null && status.equalsIgnoreCase("nonaktif")) {
            btnHapus.setText("Akses Dicabut");
            btnHapus.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold;");
            btnHapus.setDisable(true);
        }
        
        btnHapus.setOnAction(e -> {
            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, "Cabut akses login untuk " + username + " ("+role+")?", ButtonType.YES, ButtonType.NO);
            konfirmasi.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    try {
                        UserDAO.cabutAksesUser(id, role);
                        btnHapus.setText("Akses Dicabut");
                        btnHapus.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold;");
                        btnHapus.setDisable(true);
                        showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Akses Login untuk " + username + " berhasil dicabut dan dinonaktifkan!");
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Gagal mencabut akses", ex.getMessage());
                    }
                }
            });
        });

        card.getChildren().addAll(info, spacer, btnHapus);
        return card;
    }

    // --- HELPER METHODS ---
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorLabel(VBox target, String msg, Exception e) {
        e.printStackTrace();
        Label lblErr = new Label("❌ " + msg + ": " + e.getMessage());
        lblErr.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        target.getChildren().add(lblErr);
    }
}