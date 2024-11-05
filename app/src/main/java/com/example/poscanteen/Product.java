package com.example.poscanteen;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

public class Product implements Parcelable {
    private String id; // Product ID
    private String name;
    private String imageUrl;
    private String category;
    private String description;
    private Double price; // Changed from String to Double for consistency
    private List<AddOn> addOns; // List of AddOn objects
    private List<Size> sizes; // List of Size objects

    // New fields for quantity and pricing logic
    private int quantity; // Quantity of the product, default is 1
    private Double unitPrice; // Parsed price, assuming it's a numeric value

    // New fields for selected add-ons and size
    private Size selectedSize; // The selected size
    private List<AddOn> selectedAddOns; // The selected add-ons

    // No-argument constructor
    public Product() {
        this.quantity = 1; // Default quantity is 1
    }

    // Parameterized constructor
    public Product(String id, String name, String imageUrl, String category,
                   String description, Double price, List<AddOn> addOns, List<Size> sizes) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
        this.price = (price != null) ? price : 0.0; // Set default price if null
        this.addOns = addOns;
        this.sizes = sizes;
        this.quantity = 1; // Default quantity is 1
        this.unitPrice = this.price;
    }

    // Method to calculate the unit price based on selected size and add-ons
    public void calculateUnitPrice() {
        double basePrice = (this.price != null) ? this.price : 0.0;

        if (selectedSize != null) {
            basePrice += selectedSize.getPrice();
        }

        if (selectedAddOns != null && !selectedAddOns.isEmpty()) {
            for (AddOn addOn : selectedAddOns) {
                basePrice += addOn.getPrice();
            }
        }

        this.unitPrice = basePrice;
    }

    // Getters
    public String getId() {
        return id;
    }

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

    public Double getPrice() {
        return price;
    }

    public List<AddOn> getAddOns() {
        return addOns;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Double getTotalPrice() {
        return unitPrice * quantity;
    }

    public Size getSelectedSize() {
        return selectedSize;
    }

    public List<AddOn> getSelectedAddOns() {
        return selectedAddOns;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price != null ? price : 0.0;
        this.unitPrice = this.price; // Update unitPrice based on new price
    }

    public void setAddOns(List<AddOn> addOns) {
        this.addOns = addOns;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSelectedSize(Size selectedSize) {
        this.selectedSize = selectedSize;
        calculateUnitPrice(); // Recalculate unit price after setting the size
    }

    public void setSelectedAddOns(List<AddOn> selectedAddOns) {
        this.selectedAddOns = selectedAddOns;
        calculateUnitPrice(); // Recalculate unit price after setting add-ons
    }

    // Parcelable implementation
    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        category = in.readString();
        description = in.readString();
        price = (Double) in.readValue(Double.class.getClassLoader());
        addOns = in.createTypedArrayList(AddOn.CREATOR);
        sizes = in.createTypedArrayList(Size.CREATOR);
        quantity = in.readInt();
        unitPrice = (Double) in.readValue(Double.class.getClassLoader());
        selectedSize = in.readParcelable(Size.class.getClassLoader());
        selectedAddOns = in.createTypedArrayList(AddOn.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeValue(price);
        dest.writeTypedList(addOns);
        dest.writeTypedList(sizes);
        dest.writeInt(quantity);
        dest.writeValue(unitPrice);
        dest.writeParcelable(selectedSize, flags);
        dest.writeTypedList(selectedAddOns);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", addOns=" + addOns +
                ", sizes=" + sizes +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", selectedSize=" + selectedSize +
                ", selectedAddOns=" + selectedAddOns +
                '}';
    }
}
