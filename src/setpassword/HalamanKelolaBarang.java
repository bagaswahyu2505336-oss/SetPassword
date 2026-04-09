package setpassword;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class HalamanKelolaBarang {
    
    private FlowPane containerBarang = new FlowPane();
    private String pathFotoTerpilih = "";

    public VBox getTampilan() {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #F4F7F6; -fx-padding: 20;");

        Label lblJudul = new Label("📦 Management Inventaris Barang");
        lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: #1F4A75;");

        // --- SECTION 1: FORM TAMBAH BARANG ---
        GridPane formTambah = new GridPane();
        formTambah.setHgap(15); formTambah.setVgap(10);
        formTambah.setPadding(new Insets(20));
        formTambah.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        TextField txtSku = new TextField(); txtSku.setPromptText("Contoh: TEL-001");
        TextField txtNama = new TextField(); txtNama.setPromptText("Nama Barang");
        TextField txtHarga = new TextField(); txtHarga.setPromptText("Harga");
        TextField txtStok = new TextField(); txtStok.setPromptText("Jumlah Stok");
        TextField txtPathFoto = new TextField(); txtPathFoto.setEditable(false); txtPathFoto.setPromptText("Pilih Gambar...");

        Button btnPilihFoto = new Button("📁 Pilih Foto");
        btnPilihFoto.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(null);
            if (file != null) {
                pathFotoTerpilih = file.toURI().toString();
                txtPathFoto.setText(file.getName());
            }
        });

        Button btnSimpan = new Button("➕ Tambah Barang");
        btnSimpan.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");

        formTambah.add(new Label("Kode (SKU):"), 0, 0); formTambah.add(txtSku, 1, 0);
        formTambah.add(new Label("Nama:"), 2, 0); formTambah.add(txtNama, 3, 0);
        formTambah.add(new Label("Harga:"), 0, 1); formTambah.add(txtHarga, 1, 1);
        formTambah.add(new Label("Stok:"), 2, 1); formTambah.add(txtStok, 3, 1);
        formTambah.add(new Label("Foto:"), 0, 2); 
        formTambah.add(new HBox(5, txtPathFoto, btnPilihFoto), 1, 2);
        formTambah.add(btnSimpan, 3, 2);

        btnSimpan.setOnAction(e -> {
            try {
                if(txtNama.getText().isEmpty() || txtSku.getText().isEmpty() || pathFotoTerpilih.isEmpty()) {
                    ErrorHandler.tampilkanPeringatan("Input Error", "SKU, Nama, dan Foto wajib diisi!");
                    return;
                }
                FungsiDB.tambahBarangBaru(txtSku.getText(), txtNama.getText(), txtHarga.getText(), txtStok.getText(), pathFotoTerpilih);
                ErrorHandler.tampilkanSukses("Berhasil", "Barang berhasil ditambahkan!");
                
                txtSku.clear(); txtNama.clear(); txtHarga.clear(); txtStok.clear(); txtPathFoto.clear();
                pathFotoTerpilih = "";
                refreshData();
            } catch (Exception ex) {
                ErrorHandler.handleException("Simpan Barang", new GrosirException(ex.getMessage()));
            }
        });

        // --- SECTION 2: CARD VIEW ---
        containerBarang.setHgap(20);
        containerBarang.setVgap(20);
        containerBarang.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(containerBarang);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        refreshData(); 

        root.getChildren().addAll(lblJudul, formTambah, scrollPane);
        return root;
    }

    private void refreshData() {
        containerBarang.getChildren().clear();
        try {
            System.out.println("[DEBUG] 1. Memulai proses tarik data...");
            List<Barang> daftarBarang = FungsiDB.getKatalogBarang();
            System.out.println("[DEBUG] 2. Berhasil tarik data. Jumlah: " + daftarBarang.size());
            
            if (daftarBarang.isEmpty()) {
                Label lblKosong = new Label("Tabel barang di database kosong (0 baris).");
                lblKosong.setStyle("-fx-font-size: 16px; -fx-text-fill: #E74C3C; -fx-font-weight: bold;");
                containerBarang.getChildren().add(lblKosong);
            } else {
                for (Barang b : daftarBarang) {
                    containerBarang.getChildren().add(buatCard(b));
                }
                System.out.println("[DEBUG] 3. Semua Card berhasil digambar di layar!");
            }
            
        } catch (Throwable e) { 
            // Tangkap semua error dan paksa muncul di layar
            System.err.println("\n[ERROR DITEMUKAN]: " + e.getMessage());
            e.printStackTrace(); 
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Load Database");
            alert.setHeaderText("Program Berhenti Karena Kesalahan!");
            alert.setContentText("Alasan Error:\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    private VBox buatCard(Barang b) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        ImageView iv = new ImageView();
        try { 
            iv.setImage(new Image(b.getFoto())); 
        } catch (Exception e) { 
            iv.setImage(new Image("https://via.placeholder.com/150")); 
        }
        iv.setFitHeight(120);
        iv.setPreserveRatio(true);

        Label name = new Label(b.getNama());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label skuLabel = new Label("SKU: " + b.getSku());
        skuLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        Label price = new Label("Rp " + b.getHarga());
        price.setStyle("-fx-text-fill: #1F4A75; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label stock = new Label("Stok Gudang: " + b.getStokGudang() + " Unit");
        stock.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

        Button btnEdit = new Button("Edit");
        Button btnHapus = new Button("Hapus");
        btnEdit.setPrefWidth(80);
        btnHapus.setPrefWidth(80);
        btnHapus.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");

        btnHapus.setOnAction(e -> {
            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, "Hapus " + b.getNama() + "?", ButtonType.YES, ButtonType.NO);
            konfirmasi.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        FungsiDB.hapusBarang(b.getIdBarang());
                        refreshData();
                    } catch (Exception ex) { 
                        ex.printStackTrace();
                    }
                }
            });
        });

        btnEdit.setOnAction(e -> tampilkanDialogEdit(b));

        HBox aksi = new HBox(10, btnEdit, btnHapus);
        aksi.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iv, name, skuLabel, price, stock, aksi);
        return card;
    }

    private void tampilkanDialogEdit(Barang b) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edit Barang: " + b.getNama());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        
        TextField tNama = new TextField(b.getNama());
        TextField tHarga = new TextField(b.getHarga());
        TextField tStok = new TextField(b.getStokGudang());
        Button btnUpdate = new Button("Update Data");
        btnUpdate.setStyle("-fx-background-color: #1F4A75; -fx-text-fill: white; -fx-font-weight: bold;");

        btnUpdate.setOnAction(e -> {
            try {
                FungsiDB.updateBarang(b.getIdBarang(), tNama.getText(), tHarga.getText(), tStok.getText());
                stage.close();
                ErrorHandler.tampilkanSukses("Berhasil", "Data barang telah diperbarui.");
                refreshData();
            } catch (Exception ex) { 
                ErrorHandler.handleException("Update Gagal", new GrosirException(ex.getMessage()));
            }
        });

        layout.getChildren().addAll(
            new Label("Nama Barang:"), tNama, 
            new Label("Harga:"), tHarga, 
            new Label("Stok Gudang:"), tStok, 
            btnUpdate
        );

        stage.setScene(new Scene(layout, 300, 350));
        stage.show();
    }
}