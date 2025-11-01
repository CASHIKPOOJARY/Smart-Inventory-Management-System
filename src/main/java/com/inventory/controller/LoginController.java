package com.inventory.controller;

import com.inventory.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    // Handle login button click
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userDAO.validateUser(username, password)) {
            loadDashboard(event);
        } else {
            showError("Login Failed", "Invalid username or password.");
        }
    }

    // Load dashboard.fxml after successful login
    private void loadDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/inventory/view/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Smart Inventory - Dashboard");
            stage.setScene(new Scene(dashboardRoot, 900, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation error", "Could not load dashboard.");
        }
    }

    // Helper method for error alerts
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
