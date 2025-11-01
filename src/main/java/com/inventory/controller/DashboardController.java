package com.inventory.controller;

import com.inventory.dao.ProductDAO;
import com.inventory.model.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.util.List;

public class DashboardController {

    private final ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        checkLowStock();
    }

    private void checkLowStock() {
        List<Product> lowStock = productDAO.getLowStockProducts();
        if (!lowStock.isEmpty()) {
            StringBuilder msg = new StringBuilder("⚠ Low Stock Alert:\n\n");
            for (Product p : lowStock) {
                msg.append(p.getName())
                        .append(" - Qty: ").append(p.getQuantity())
                        .append(" (Reorder at: ").append(p.getReorderLevel()).append(")\n");
            }
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Low Stock Warning");
            alert.setHeaderText("Some products are running low!");
            alert.setContentText(msg.toString());
            alert.showAndWait();
        }
    }

    public void onManageTransactions(ActionEvent event) {
        loadScene("/com/inventory/view/transaction.fxml", "Smart Inventory - Transactions", event);
    }

    public void onManageProducts(ActionEvent event) {
        loadScene("/com/inventory/view/product.fxml", "Manage Products", event);
    }

    public void onManageSuppliers(ActionEvent event) {
        loadScene("/com/inventory/view/supplier.fxml", "Manage Suppliers", event);
    }

    // ✅ NEW METHOD for Report button
    public void onManageReports(ActionEvent event) {
        loadScene("/com/inventory/view/Report.fxml", "Inventory Reports", event);
    }

    public void handleLogout(ActionEvent event) {
        loadScene("/com/inventory/view/login.fxml", "Login", event, 400, 300);
    }

    // Helper to avoid repeating code
    private void loadScene(String fxml, String title, ActionEvent event) {
        loadScene(fxml, title, event, 900, 600);
    }

    private void loadScene(String fxml, String title, ActionEvent event, int width, int height) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
