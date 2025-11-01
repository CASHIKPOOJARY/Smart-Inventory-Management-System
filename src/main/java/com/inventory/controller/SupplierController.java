package com.inventory.controller;

import com.inventory.dao.SupplierDAO;
import com.inventory.model.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class SupplierController {

    @FXML private TextField nameField;
    @FXML private TextField contactField; // Email
    @FXML private TextField phoneField;

    @FXML private TableView<Supplier> table;
    @FXML private TableColumn<Supplier, Integer> colId;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colContact;
    @FXML private TableColumn<Supplier, String> colPhone;

    private final SupplierDAO dao = new SupplierDAO();
    private final ObservableList<Supplier> data = FXCollections.observableArrayList();
    private Supplier selected;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("\\d{10}");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colContact.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getContact()));
        colPhone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhone()));
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selected = n;
            if (n != null) {
                nameField.setText(n.getName());
                contactField.setText(n.getContact());
                phoneField.setText(n.getPhone());
            }
        });
        refresh();
    }

    private void refresh() {
        data.setAll(dao.findAll());
    }

    public void saveSupplier(ActionEvent e) {
        if (!validateInputs()) return;

        Supplier s = new Supplier(0, nameField.getText().trim(),
                contactField.getText().trim(), phoneField.getText().trim());
        dao.add(s);
        clearForm(null);
        refresh();
        alert(Alert.AlertType.INFORMATION, "Supplier saved");
    }

    public void updateSupplier(ActionEvent e) {
        if (selected == null) { alert(Alert.AlertType.ERROR, "Select a supplier"); return; }
        if (!validateInputs()) return;

        selected.setName(nameField.getText().trim());
        selected.setContact(contactField.getText().trim());
        selected.setPhone(phoneField.getText().trim());
        dao.update(selected);
        refresh();
        alert(Alert.AlertType.INFORMATION, "Supplier updated");
    }

    public void deleteSelected(ActionEvent e) {
        Supplier s = table.getSelectionModel().getSelectedItem();
        if (s == null) { alert(Alert.AlertType.ERROR, "Select a supplier"); return; }
        if (confirm("Delete supplier '" + s.getName() + "'?")) {
            dao.delete(s.getId());
            refresh();
            clearForm(null);
        }
    }

    public void clearForm(ActionEvent e) {
        selected = null;
        nameField.clear();
        contactField.clear();
        phoneField.clear();
        table.getSelectionModel().clearSelection();
    }

    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/inventory/view/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            alert(Alert.AlertType.ERROR, "Name is required");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(contactField.getText().trim()).matches()) {
            alert(Alert.AlertType.ERROR, "Enter a valid email address");
            return false;
        }
        if (!PHONE_PATTERN.matcher(phoneField.getText().trim()).matches()) {
            alert(Alert.AlertType.ERROR, "Phone must be exactly 10 digits");
            return false;
        }
        return true;
    }

    private void alert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
