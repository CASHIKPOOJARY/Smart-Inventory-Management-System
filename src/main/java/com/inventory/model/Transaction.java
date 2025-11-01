package com.inventory.model;

import java.sql.Timestamp;

public class Transaction {
    private int id;
    private Product product;
    private int quantity;
    private String type;
    private double unitPrice;
    private double total;
    private Timestamp createdAt;

    // 👇 No-arg constructor (needed for DAO)
    public Transaction() {}

    // Constructor for loading from DB
    public Transaction(int id, Product product, int quantity, String type, double unitPrice, double total, Timestamp createdAt) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.type = type;
        this.unitPrice = unitPrice;
        this.total = total;
        this.createdAt = createdAt;
    }

    // Constructor for creating new transactions
    public Transaction(Product product, int quantity, String type, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.type = type;
        this.unitPrice = unitPrice;
        this.total = unitPrice * quantity;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
