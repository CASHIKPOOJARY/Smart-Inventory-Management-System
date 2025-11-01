package com.inventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the login screen FXML
        Parent root = FXMLLoader.load(getClass().getResource("/com/inventory/view/login.fxml"));

        stage.setTitle("Smart Inventory - Login");
        stage.setScene(new Scene(root, 420, 300));
        stage.setResizable(false); // Optional: prevent resizing
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Start the JavaFX application
    }
}
