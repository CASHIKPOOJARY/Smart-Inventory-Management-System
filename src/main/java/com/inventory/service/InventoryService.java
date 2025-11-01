package com.inventory.service;

import com.inventory.model.Product;

import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private final List<Product> products = new ArrayList<>();
    private int nextId = 1;

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public void addProduct(Product p) {
        p.setId(nextId++);
        products.add(p);
    }

    public void updateProduct(Product updated) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updated.getId()) {
                products.set(i, updated);
                return;
            }
        }
    }

    public void deleteProduct(int id) {
        products.removeIf(p -> p.getId() == id);
    }
}
