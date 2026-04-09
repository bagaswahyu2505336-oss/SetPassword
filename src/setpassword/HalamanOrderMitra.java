package setpassword;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HalamanOrderMitra {
    private int idUser;
    private int idToko;
    
    private List<FungsiDB.KeranjangItem> keranjang = new ArrayList<>();
    private VBox boxListKeranjang = new VBox(10);
    private Label lblTotalHarga = new Label("Total Tagihan: Rp 0");
    private FlowPane katalogPane = new FlowPane(15, 15);
    private List<Barang> semuaBarang = new ArrayList<>();

    public HalamanOrderMitra(int idUser, int idToko) {
        this.idUser = idUser;
        this.idToko = idToko;
    }

    public HBox getTampilan() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F7F6;");

        // --- SISI KIRI: KATALOG PRODUK ---
        VBox boxKatalog = new VBox(15);
        HBox.setHgrow(boxKatalog, Priority.ALWAYS);
        
        Label lblKatalog = new Label("🛍️ Katalog Grosir Modern");
        lblKatalog.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1F4A75;");

        TextField txtCari = new TextField();
        txtCari.setPromptText("🔍 Cari produk yang Anda inginkan...");
        txtCari.setStyle("-fx-padding: 12; -fx-background-radius: 25; -fx-border-color: #DCDCDC; -fx-border-radius: 25;");
        txtCari.textProperty().addListener((obs, oldV, newV) -> filterKatalog(newV));

        ScrollPane scrollKatalog = new ScrollPane(katalogPane);
        scrollKatalog.setFitToWidth(true);
        scrollKatalog.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollKatalog, Priority.ALWAYS);
        
        boxKatalog.getChildren().addAll(lblKatalog, txtCari, scrollKatalog);

        // --- SISI KANAN: KERANJANG & CHECKOUT ---
        VBox boxKeranjang = new VBox(15);
        boxKeranjang.setPrefWidth(400);
        boxKeranjang.setPadding(new Insets(20));
        boxKeranjang.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblKeranjangHeader = new Label("🛒 Keranjang Belanja");
        lblKeranjangHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1F4A75;");

        HBox headerTabel = new HBox(10);
        Label hNama = new Label("Produk"); hNama.setPrefWidth(140); hNama.setStyle("-fx-font-weight: bold;");
        Label hQty = new Label("Qty"); hQty.setPrefWidth(40); hQty.setStyle("-fx-font-weight: bold;");
        Label hSub = new Label("Subtotal"); hSub.setPrefWidth(100); hSub.setStyle("-fx-font-weight: bold;");
        headerTabel.getChildren().addAll(hNama, hQty, hSub);

        ScrollPane scrollKeranjang = new ScrollPane(boxListKeranjang);
        scrollKeranjang.setFitToWidth(true);
        scrollKeranjang.setPrefHeight(400);
        scrollKeranjang.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scrollKeranjang, Priority.ALWAYS);

        lblTotalHarga.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");

        Button btnCheckout = new Button("💳 Checkout & Terbitkan Nota");
        btnCheckout.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; " +
                             "-fx-font-size: 15px; -fx-padding: 12; -fx-background-radius: 10; -fx-cursor: hand;");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setOnAction(e -> prosesCheckout());

        boxKeranjang.getChildren().addAll(lblKeranjangHeader, new Separator(), headerTabel, scrollKeranjang, new Separator(), lblTotalHarga, btnCheckout);

        root.getChildren().addAll(boxKatalog, boxKeranjang);
        
        loadDataKatalog();
        return root;
    }

    private void loadDataKatalog() {
        try {
            semuaBarang = FungsiDB.getKatalogBarang();
            filterKatalog("");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filterKatalog(String keyword) {
        katalogPane.getChildren().clear();
        for (Barang b : semuaBarang) {
            if (Integer.parseInt(b.getStokGudang()) > 0 && b.getNama().toLowerCase().contains(keyword.toLowerCase())) {
                katalogPane.getChildren().add(buatCardBarang(b));
            }
        }
    }

    private VBox buatCardBarang(Barang b) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(210);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                     "-fx-border-color: #F0F0F0; -fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        ImageView imgView = new ImageView();
        imgView.setFitWidth(130); 
        imgView.setFitHeight(130); 
        imgView.setPreserveRatio(true);
        
        try {
            String path = b.getFoto();
            if (path != null && !path.isEmpty()) {
                String cleanPath = path.replace("file:/", "").replace("%20", " ");
                File f = new File(cleanPath);
                if (f.exists()) imgView.setImage(new Image(f.toURI().toString()));
            }
        } catch (Exception e) { System.out.println("Gagal muat gambar: " + b.getNama()); }

        Label lblNama = new Label(b.getNama());
        lblNama.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblNama.setWrapText(true);
        lblNama.setAlignment(Pos.CENTER);
        lblNama.setPrefHeight(40);

        Label lblHarga = new Label("Rp " + b.getHarga());
        lblHarga.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold; -fx-font-size: 15px;");

        // --- REVISI: MENU PEMILIH JUMLAH (Qty Selector) ---
        HBox qtySelector = new HBox(8);
        qtySelector.setAlignment(Pos.CENTER);
        
        Button btnMin = new Button("-");
        btnMin.setPrefSize(30, 30);
        btnMin.setStyle("-fx-background-radius: 5; -fx-cursor: hand;");
        
        TextField txtQty = new TextField("1");
        txtQty.setPrefWidth(45);
        txtQty.setAlignment(Pos.CENTER);
        txtQty.setStyle("-fx-background-radius: 5;");
        
        Button btnPlus = new Button("+");
        btnPlus.setPrefSize(30, 30);
        btnPlus.setStyle("-fx-background-radius: 5; -fx-cursor: hand;");

        // Logika Interaksi Selector
        btnMin.setOnAction(e -> {
            int current = Integer.parseInt(txtQty.getText());
            if (current > 1) txtQty.setText(String.valueOf(current - 1));
        });

        btnPlus.setOnAction(e -> {
            int current = Integer.parseInt(txtQty.getText());
            int stok = Integer.parseInt(b.getStokGudang());
            if (current < stok) {
                txtQty.setText(String.valueOf(current + 1));
            } else {
                new Alert(Alert.AlertType.WARNING, "Stok tidak mencukupi!").show();
            }
        });

        qtySelector.getChildren().addAll(btnMin, txtQty, btnPlus);

        // --- TOMBOL TAMBAH KE KERANJANG ---
        Button btnAdd = new Button("Tambah 🛒");
        btnAdd.setStyle("-fx-background-color: #1F4A75; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> {
            int jumlahInput = Integer.parseInt(txtQty.getText());
            tambahKeKeranjang(b, jumlahInput);
            txtQty.setText("1"); // Reset ke 1 setelah ditambah
        });

        // Pastikan qtySelector dimasukkan ke dalam card
        card.getChildren().addAll(imgView, lblNama, lblHarga, qtySelector, btnAdd);
        return card;
    }

    private void tambahKeKeranjang(Barang b, int qty) {
        boolean ada = false;
        for (FungsiDB.KeranjangItem i : keranjang) {
            if (i.idBarang.equals(b.getIdBarang())) {
                i.jumlah += qty; 
                i.subtotal = i.jumlah * i.hargaSatuan;
                ada = true; 
                break;
            }
        }
        if (!ada) {
            keranjang.add(new FungsiDB.KeranjangItem(b.getIdBarang(), b.getNama(), qty, Double.parseDouble(b.getHarga())));
        }
        refreshKeranjang();
    }

    private void refreshKeranjang() {
        boxListKeranjang.getChildren().clear();
        double total = 0;
        for (FungsiDB.KeranjangItem item : keranjang) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(5));
            row.setStyle("-fx-border-color: #F0F0F0; -fx-border-width: 0 0 1 0;");

            Label n = new Label(item.namaBarang); n.setPrefWidth(140);
            Label q = new Label("x" + item.jumlah); q.setPrefWidth(40);
            Label s = new Label("Rp" + (int)item.subtotal); s.setPrefWidth(100); s.setStyle("-fx-font-weight: bold;");

            Button btnHapus = new Button("❌");
            btnHapus.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand;");
            btnHapus.setOnAction(e -> {
                keranjang.remove(item);
                refreshKeranjang();
            });

            row.getChildren().addAll(n, q, s, btnHapus);
            boxListKeranjang.getChildren().add(row);
            total += item.subtotal;
        }
        lblTotalHarga.setText("Total Tagihan: Rp " + (int)total);
    }

    private void prosesCheckout() {
        if (keranjang.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Keranjang belanja kosong!").showAndWait();
            return;
        }
        
        try {
            FungsiDB.checkoutKeranjang(idToko, keranjang);
            StrukTagihanPemesanan struk = new StrukTagihanPemesanan();
            
            String namaMitra = "User Mitra ID: " + idUser; 
            String namaToko = "Toko Cabang ID: " + idToko;
            String alamatToko = "Alamat terdaftar di database";
            String totalTxt = lblTotalHarga.getText().replace("Total Tagihan: Rp ", "");

            struk.tampilkan(namaMitra, namaToko, alamatToko, keranjang, totalTxt);

            keranjang.clear();
            refreshKeranjang();
            loadDataKatalog();
            
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal memproses checkout: " + e.getMessage()).showAndWait();
        }
    }
}