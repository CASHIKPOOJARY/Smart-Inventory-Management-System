package com.inventory.controller;

import com.inventory.dao.TransactionDAO;
import com.inventory.model.Transaction;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// PDF
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

// File chooser
import javafx.stage.FileChooser;

public class ReportController {

    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TableView<Transaction> reportTable;
    @FXML private TableColumn<Transaction, String> colProduct;
    @FXML private TableColumn<Transaction, Integer> colQuantity;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Double> colUnitPrice;
    @FXML private TableColumn<Transaction, Timestamp> colDate;
    @FXML private TableColumn<Transaction, Double> colTotal;

    private final TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("ALL", "PURCHASE", "SALE"));
        typeComboBox.setValue("ALL");

        colProduct.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getProduct().getName()));
        colQuantity.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));
        colUnitPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getUnitPrice()).asObject());
        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getCreatedAt()));
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getQuantity() * c.getValue().getUnitPrice()).asObject());
    }

    @FXML
    private void generateReport() {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        String type = typeComboBox.getValue();

        List<Transaction> transactions = transactionDAO.getAllTransactions();

        // Filter by date
        if (from != null && to != null) {
            transactions = transactions.stream()
                    .filter(t -> {
                        LocalDate date = t.getCreatedAt().toLocalDateTime().toLocalDate();
                        return (date.isEqual(from) || date.isAfter(from)) &&
                                (date.isEqual(to) || date.isBefore(to));
                    })
                    .collect(Collectors.toList());
        }

        // Filter by type
        if (!"ALL".equals(type)) {
            transactions = transactions.stream()
                    .filter(t -> t.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        reportTable.setItems(FXCollections.observableArrayList(transactions));
    }

    // ✅ Export Excel
    @FXML
    private void exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Product");
            header.createCell(1).setCellValue("Quantity");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Unit Price");
            header.createCell(4).setCellValue("Date");
            header.createCell(5).setCellValue("Total Amount");

            // Data
            List<Transaction> transactions = reportTable.getItems();
            int rowNum = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getProduct().getName());
                row.createCell(1).setCellValue(t.getQuantity());
                row.createCell(2).setCellValue(t.getType());
                row.createCell(3).setCellValue(t.getUnitPrice());
                row.createCell(4).setCellValue(t.getCreatedAt().toString());
                row.createCell(5).setCellValue(t.getQuantity() * t.getUnitPrice());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Excel Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

            if (file != null) {
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                showAlert("Success", "Excel report saved at: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to export Excel: " + e.getMessage());
        }
    }

    // ✅ Export PDF
    @FXML
    private void exportPdf() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

            if (file != null) {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Title
                PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
                Paragraph title = new Paragraph("Inventory Report")
                        .setFont(boldFont)
                        .setFontSize(16);
                document.add(title);

                // Table
                float[] columnWidths = {150F, 80F, 80F, 80F, 120F, 100F};
                Table table = new Table(columnWidths);

                table.addHeaderCell("Product");
                table.addHeaderCell("Quantity");
                table.addHeaderCell("Type");
                table.addHeaderCell("Unit Price");
                table.addHeaderCell("Date");
                table.addHeaderCell("Total Amount");

                List<Transaction> transactions = reportTable.getItems();
                for (Transaction t : transactions) {
                    table.addCell(t.getProduct().getName());
                    table.addCell(String.valueOf(t.getQuantity()));
                    table.addCell(t.getType());
                    table.addCell(String.valueOf(t.getUnitPrice()));
                    table.addCell(t.getCreatedAt().toString());
                    table.addCell(String.valueOf(t.getQuantity() * t.getUnitPrice()));
                }

                document.add(table);
                document.close();

                showAlert("Success", "PDF report saved at: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to export PDF: " + e.getMessage());
        }
    }

    // ✅ FIXED Back button size issue
    @FXML
    private void backToDashboard() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/inventory/view/Dashboard.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) reportTable.getScene().getWindow();

            // Option 1: fixed size (always 800x600)
            stage.setScene(new javafx.scene.Scene(root, 800, 600));

            // Option 2: auto-size to FXML
            // stage.setScene(new javafx.scene.Scene(root));
            // stage.sizeToScene();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
