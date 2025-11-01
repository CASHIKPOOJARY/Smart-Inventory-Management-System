package com.inventory.controller;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.SupplierDAO;
import com.inventory.model.Product;
import com.inventory.model.ProductCategory;
import com.inventory.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProductController {

    // Table & Columns
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colReorder;
    @FXML private TableColumn<Product, String> colSupplier;
    @FXML private TableColumn<Product, String> colCategory;

    // Input fields
    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TextField reorderField;
    @FXML private ComboBox<Supplier> supplierComboBox;
    @FXML private ComboBox<ProductCategory> categoryComboBox;

    // Search filters
    @FXML private TextField searchField;
    @FXML private ComboBox<ProductCategory> searchCategoryBox;

    private final ProductDAO productDAO = new ProductDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private ObservableList<Product> productList;
    private ObservableList<Supplier> supplierList;

    @FXML
    public void initialize() {
        // Table column bindings
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colQuantity.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colReorder.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getReorderLevel()).asObject());
        colSupplier.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getSupplierName() != null ? data.getValue().getSupplierName() : "N/A"
        ));
        colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCategory() != null ? data.getValue().getCategory().name() : "N/A"
        ));

        // Load suppliers
        supplierList = FXCollections.observableArrayList(supplierDAO.findAll());
        supplierComboBox.setItems(supplierList);

        // Fill category combo boxes
        categoryComboBox.setItems(FXCollections.observableArrayList(ProductCategory.values()));
        searchCategoryBox.setItems(FXCollections.observableArrayList(ProductCategory.values()));

        refreshTable();

        // Load selected row into form
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                nameField.setText(newSel.getName());
                quantityField.setText(String.valueOf(newSel.getQuantity()));
                priceField.setText(String.valueOf(newSel.getPrice()));
                reorderField.setText(String.valueOf(newSel.getReorderLevel()));
                supplierComboBox.setValue(newSel.getSupplier());
                categoryComboBox.setValue(newSel.getCategory());
            }
        });

        // Search filters listeners
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        searchCategoryBox.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
    }

    @FXML
    public void refreshTable() {
        productList = FXCollections.observableArrayList(productDAO.getAllProductsWithSupplier());
        productTable.setItems(productList);
    }

    private void applyFilters() {
        String keyword = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        ProductCategory categoryFilter = searchCategoryBox.getValue();

        ObservableList<Product> filtered = productList.filtered(p ->
                (keyword.isEmpty() || p.getName().toLowerCase().contains(keyword)) &&
                        (categoryFilter == null || p.getCategory() == categoryFilter)
        );
        productTable.setItems(filtered);
    }

    @FXML
    public void addProduct(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Product name cannot be empty.");
                return;
            }

            int qty = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            int reorder = Integer.parseInt(reorderField.getText());
            Supplier selectedSupplier = supplierComboBox.getValue();
            ProductCategory category = categoryComboBox.getValue();

            if (qty < 0 || price < 0 || reorder < 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Quantity, Price, and Reorder Level must be non-negative.");
                return;
            }
            if (selectedSupplier == null || category == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select supplier and category.");
                return;
            }

            Product newProduct = new Product(0, name, qty, reorder, price, selectedSupplier, category);
            productDAO.addProduct(newProduct);

            refreshTable();
            clearFields();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity, Price, and Reorder Level must be valid numbers.");
        }
    }

    @FXML
    public void updateProduct(ActionEvent e) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to update.");
            return;
        }

        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Product name cannot be empty.");
                return;
            }

            int qty = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            int reorder = Integer.parseInt(reorderField.getText());
            Supplier selectedSupplier = supplierComboBox.getValue();
            ProductCategory category = categoryComboBox.getValue();

            if (qty < 0 || price < 0 || reorder < 0 || selectedSupplier == null || category == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter valid values.");
                return;
            }

            selected.setName(name);
            selected.setQuantity(qty);
            selected.setPrice(price);
            selected.setReorderLevel(reorder);
            selected.setSupplier(selectedSupplier);
            selected.setCategory(category);

            productDAO.updateProduct(selected);
            refreshTable();
            clearFields();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity, Price, and Reorder Level must be valid numbers.");
        }
    }

    @FXML
    public void deleteProduct(ActionEvent e) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to delete.");
            return;
        }
        productDAO.deleteProduct(selected.getId());
        refreshTable();
        clearFields();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        quantityField.clear();
        priceField.clear();
        reorderField.clear();
        supplierComboBox.setValue(null);
        categoryComboBox.setValue(null);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/inventory/view/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Smart Inventory - Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Optional: called from "Search" button explicitly
    @FXML
    public void searchProducts(ActionEvent e) {
        applyFilters();
    }
}
