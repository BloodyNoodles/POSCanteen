package com.example.poscanteen;

public class CheckoutItem {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public CheckoutItem(String productName, int quantity, double unitPrice, double totalPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters for all fields
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }

    // Setters to update quantity and total price
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = this.unitPrice * this.quantity;
    }
}
