package com.example.poscanteen;

import android.os.Parcel;
import android.os.Parcelable;

public class Size implements Parcelable {
    private String name;
    private double price;
    private boolean isSelected; // Field to track selection state

    // Default constructor
    public Size() {
        this.name = "";
        this.price = 0.0;
        this.isSelected = false; // Default to not selected
    }

    public Size(String name, double price) {
        this.name = name;
        this.price = price;
        this.isSelected = false; // Default to not selected
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSelected() {
        return isSelected; // Getter for selection state
    }

    public void setSelected(boolean selected) {
        isSelected = selected; // Setter for selection state
    }

    public String getFormattedPrice() {
        return String.format("₱%.2f", price); // Format with pesos sign and 2 decimal places
    }

    // Parcelable implementation
    protected Size(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        isSelected = in.readByte() != 0; // Convert byte to boolean
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeByte((byte) (isSelected ? 1 : 0)); // Convert boolean to byte
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Size> CREATOR = new Creator<Size>() {
        @Override
        public Size createFromParcel(Parcel in) {
            return new Size(in);
        }

        @Override
        public Size[] newArray(int size) {
            return new Size[size];
        }
    };
}
