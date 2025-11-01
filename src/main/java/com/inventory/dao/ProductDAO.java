package com.inventory.dao;

import com.inventory.model.Product;
import com.inventory.model.ProductCategory;
import com.inventory.model.Supplier;
import com.inventory.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Add new product
    public void addProduct(Product product) {
        String sql = "INSERT INTO products (name, quantity, reorder_level, price, supplier_id, category) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setInt(3, product.getReorderLevel());
            ps.setDouble(4, product.getPrice());

            if (product.getSupplier() != null) {
                ps.setInt(5, product.getSupplier().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (product.getCategory() != null) {
                ps.setString(6, product.getCategory().name());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fetch all products with supplier details
    public List<Product> getAllProductsWithSupplier() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.id as sid, s.name as sname, s.contact, s.phone " +
                "FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id";

        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Supplier supplier = null;
                if (rs.getInt("sid") != 0) {
                    supplier = new Supplier(
                            rs.getInt("sid"),
                            rs.getString("sname"),
                            rs.getString("contact"),
                            rs.getString("phone")
                    );
                }

                ProductCategory category = null;
                String categoryStr = rs.getString("category");
                if (categoryStr != null) {
                    category = ProductCategory.valueOf(categoryStr);
                }

                list.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_level"),
                        rs.getDouble("price"),
                        supplier,
                        category
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update product
    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, quantity=?, reorder_level=?, price=?, supplier_id=?, category=? WHERE id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setInt(3, product.getReorderLevel());
            ps.setDouble(4, product.getPrice());

            if (product.getSupplier() != null) {
                ps.setInt(5, product.getSupplier().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (product.getCategory() != null) {
                ps.setString(6, product.getCategory().name());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            ps.setInt(7, product.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete product
    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Search products by name or category
    public List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.id as sid, s.name as sname, s.contact, s.phone " +
                "FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "WHERE LOWER(p.name) LIKE ? OR LOWER(p.category) LIKE ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String like = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Supplier supplier = null;
                if (rs.getInt("sid") != 0) {
                    supplier = new Supplier(
                            rs.getInt("sid"),
                            rs.getString("sname"),
                            rs.getString("contact"),
                            rs.getString("phone")
                    );
                }

                ProductCategory category = null;
                String categoryStr = rs.getString("category");
                if (categoryStr != null) {
                    category = ProductCategory.valueOf(categoryStr);
                }

                list.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_level"),
                        rs.getDouble("price"),
                        supplier,
                        category
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get products that are low in stock
    public List<Product> getLowStockProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.id as sid, s.name as sname, s.contact, s.phone " +
                "FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                "WHERE p.quantity <= p.reorder_level";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Supplier supplier = null;
                if (rs.getInt("sid") != 0) {
                    supplier = new Supplier(
                            rs.getInt("sid"),
                            rs.getString("sname"),
                            rs.getString("contact"),
                            rs.getString("phone")
                    );
                }

                ProductCategory category = null;
                String categoryStr = rs.getString("category");
                if (categoryStr != null) {
                    category = ProductCategory.valueOf(categoryStr);
                }

                list.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_level"),
                        rs.getDouble("price"),
                        supplier,
                        category
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
