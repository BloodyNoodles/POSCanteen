package com.example.poscanteen;

import java.util.List;

public class Product {
    private String id; // Product ID
    private String name;
    private String imageUrl;
    private String category;
    private String description;
    private String price;
    private List<String> addOns;
    private List<String> sizes;

    // No-argument constructor
    public Product() {}

    // Parameterized constructor
    public Product(String id, String productName, String imageUrl, String category,
                   String description, String price, List<String> addOns, List<String> sizes) {
        this.id = id;
        this.name = productName;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
        this.price = price;
        this.addOns = addOns;
        this.sizes = sizes;
    }

    // Getter for ID
    public String getId() {
        return id;
    }

    // Other getters and setters
    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public List<String> getAddOns() {
        return addOns;
    }

    public List<String> getSizes() {
        return sizes;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", addOns=" + addOns +
                ", sizes=" + sizes +
                '}';
    }
}
