package com.example.poscanteen;

import android.content.Intent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Get all items in the cart
    public List<Product> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    // Add an item to the cart
    public void addItemToCart(Product product) {
        cartItems.add(product);
    }

    // Remove an item from the cart
    public void removeItemFromCart(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    // Clear the entire cart
    public void clearCart() {
        cartItems.clear();
    }

    // Update the cart item quantity
    public void updateProductQuantity(Product product, int quantity) {
        int index = cartItems.indexOf(product);
        if (index != -1) {
            cartItems.get(index).setQuantity(quantity);
        }
    }

    // Save product list to intent
    public void saveProductsToIntent(Intent intent, List<Product> products) {
        intent.putExtra("PRODUCT_LIST", (Serializable) products);
    }

    // Get product list from intent
    public List<Product> getProductsFromIntent(Intent intent) {
        return (List<Product>) intent.getSerializableExtra("PRODUCT_LIST");
    }
}
