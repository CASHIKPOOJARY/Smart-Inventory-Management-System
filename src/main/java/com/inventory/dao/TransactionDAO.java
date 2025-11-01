package com.inventory.dao;

import com.inventory.model.Product;
import com.inventory.model.Transaction;
import com.inventory.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Add transaction and update product stock
    public void addTransaction(Transaction transaction) {
        String insertSQL = "INSERT INTO transactions (product_id, qty, type, unit_price, total) VALUES (?, ?, ?, ?, ?)";
        String updateStock;

        // If purchase, add stock. If sale, subtract stock.
        if (transaction.getType().equalsIgnoreCase("PURCHASE")) {
            updateStock = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
        } else {
            updateStock = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
        }

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            // Insert transaction
            try (PreparedStatement ps = con.prepareStatement(insertSQL)) {
                ps.setInt(1, transaction.getProduct().getId());
                ps.setInt(2, transaction.getQuantity());
                ps.setString(3, transaction.getType());
                ps.setDouble(4, transaction.getUnitPrice());
                ps.setDouble(5, transaction.getTotal());
                ps.executeUpdate();
            }

            // Update product stock
            try (PreparedStatement ps2 = con.prepareStatement(updateStock)) {
                ps2.setInt(1, transaction.getQuantity());
                ps2.setInt(2, transaction.getProduct().getId());
                ps2.executeUpdate();
            }

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch all transactions with product details
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT t.id, t.product_id, p.name AS product_name, " +
                "t.qty, t.type, t.unit_price, t.total, t.created_at " +
                "FROM transactions t " +
                "JOIN products p ON t.product_id = p.id " +
                "ORDER BY t.created_at DESC";

        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Product info
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("product_name"));

                // Transaction info
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setProduct(product);
                transaction.setQuantity(rs.getInt("qty"));
                transaction.setType(rs.getString("type"));
                transaction.setUnitPrice(rs.getDouble("unit_price"));
                transaction.setTotal(rs.getDouble("total"));
                transaction.setCreatedAt(rs.getTimestamp("created_at"));

                list.add(transaction);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
