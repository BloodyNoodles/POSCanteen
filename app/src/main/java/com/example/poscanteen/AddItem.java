package com.example.poscanteen;

import java.io.Serializable;

public class AddItem implements Serializable {
    private String name;         // Name of the item
    private int quantity;        // Quantity of the item
    private double unitPrice;    // Price per unit of the item

    // Constructor
    public AddItem(String name, int quantity, double unitPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Method to calculate total price
    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}
