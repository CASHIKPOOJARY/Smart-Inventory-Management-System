package com.inventory.model;

public class Product {
    private int id;
    private String name;
    private int quantity;
    private int reorderLevel;
    private double price;
    private Supplier supplier;
    private ProductCategory category; // ✅ New field

    // 👇 No-arg constructor (required for DAO mapping)
    public Product() {}

    // Full Constructor
    public Product(int id, String name, int quantity, int reorderLevel, double price, Supplier supplier, ProductCategory category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.price = price;
        this.supplier = supplier;
        this.category = category;
    }

    // Constructor without ID
    public Product(String name, int quantity, int reorderLevel, double price, Supplier supplier, ProductCategory category) {
        this.name = name;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.price = price;
        this.supplier = supplier;
        this.category = category;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public String getSupplierName() {
        return (supplier != null) ? supplier.getName() : "N/A";
    }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    @Override
    public String toString() {
        return name + " (Qty: " + quantity + ", Category: " + (category != null ? category : "N/A") + ")";
    }
}
