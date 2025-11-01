package com.inventory.controller;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.TransactionDAO;
import com.inventory.model.Product;
import com.inventory.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Timestamp;

public class TransactionController {

    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> colProduct;
    @FXML private TableColumn<Transaction, Integer> colQuantity;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Timestamp> colDate;

    private final ProductDAO productDAO = new ProductDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    public void initialize() {
        // Load products into dropdown
        productComboBox.setItems(FXCollections.observableArrayList(productDAO.getAllProductsWithSupplier()));

        // Transaction type
        typeComboBox.setItems(FXCollections.observableArrayList("PURCHASE", "SALE"));

        // Load table
        loadTransactions();
    }

    private void loadTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList(transactionDAO.getAllTransactions());
        transactionTable.setItems(transactions);

        colProduct.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProduct().getName()));
        colQuantity.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        colType.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        colDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCreatedAt()));
    }

    @FXML
    private void handleAddTransaction() {
        Product product = productComboBox.getValue();
        String type = typeComboBox.getValue();

        if (product == null || type == null || quantityField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields!");
            return;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText());
            if (qty <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be greater than 0.");
                return;
            }

            // Stock validation
            if (type.equals("SALE") && qty > product.getQuantity()) {
                showAlert(Alert.AlertType.ERROR, "Stock Error", "Not enough stock available for this sale.");
                return;
            }

            // Save transaction (use product price if available)
            double unitPrice = product.getPrice(); // assuming Product has getPrice()
            Transaction transaction = new Transaction(product, qty, type, unitPrice);
            transactionDAO.addTransaction(transaction);

            // Update product stock
            if (type.equals("PURCHASE")) {
                product.setQuantity(product.getQuantity() + qty);
            } else if (type.equals("SALE")) {
                product.setQuantity(product.getQuantity() - qty);
            }
            productDAO.updateProduct(product);

            // Refresh table + product dropdown
            loadTransactions();
            productComboBox.setItems(FXCollections.observableArrayList(productDAO.getAllProductsWithSupplier()));

            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully.");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity must be a number.");
        }
    }

    private void clearFields() {
        productComboBox.setValue(null);
        quantityField.clear();
        typeComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ✅ Renamed method so it matches FXML
    @FXML
    public void handleBackToDashboard(javafx.event.ActionEvent event) {
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
}
