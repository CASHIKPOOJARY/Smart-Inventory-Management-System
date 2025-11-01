package com.inventory.model;

import java.util.Objects;

public class Supplier {
    private int id;
    private String name;
    private String contact;
    private String phone;

    public Supplier() {}
    public Supplier(int id, String name, String contact, String phone) {
        this.id = id; this.name = name; this.contact = contact; this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override public String toString() { return name; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Supplier)) return false;
        Supplier that = (Supplier) o;
        return id == that.id;
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
