package com.example.poscanteen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private final Context context;
    private List<Product> productList;  // List of Product items for the RecyclerView
    private final OnQuantityChangeListener onQuantityChangeListener;

    // Constructor for the adapter
    public CheckoutAdapter(Context context, List<Product> productList, OnQuantityChangeListener onQuantityChangeListener) {
        this.context = context;
        this.productList = productList;
        this.onQuantityChangeListener = onQuantityChangeListener;
    }

    // Inflates the layout for each item in the RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_additem, parent, false);
        return new ViewHolder(view);
    }

    // Binds the data to each item in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set the item number (position + 1, as positions are zero-indexed)
        holder.itemNumber.setText(String.valueOf(position + 1));

        // Set the values for each item view from the Product object
        holder.itemName.setText(product.getName());
        holder.itemQuantity.setText(String.valueOf(product.getQuantity()));
        holder.itemPrice.setText(String.format("₱%.2f", product.getUnitPrice()));
        holder.totalPrice.setText(String.format("₱%.2f", product.getTotalPrice()));

        // Set click listeners for the increase, decrease, and delete buttons
        holder.increaseButton.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            holder.itemQuantity.setText(String.valueOf(product.getQuantity()));
            holder.totalPrice.setText(String.format("₱%.2f", product.getTotalPrice()));
            notifyItemChanged(position);
            onQuantityChangeListener.onQuantityChanged(); // Notify the total price needs recalculating
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
                holder.itemQuantity.setText(String.valueOf(product.getQuantity()));
                holder.totalPrice.setText(String.format("₱%.2f", product.getTotalPrice()));
                notifyItemChanged(position);
                onQuantityChangeListener.onQuantityChanged(); // Notify the total price needs recalculating
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            productList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productList.size());
            onQuantityChangeListener.onQuantityChanged(); // Notify the total price needs recalculating
        });
    }

    // Returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to update the product list
    public void updateProductList(List<Product> updatedProductList) {
        productList.clear();
        productList.addAll(updatedProductList);
        notifyDataSetChanged();
    }

    // ViewHolder class to hold item views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNumber, itemName, itemQuantity, itemPrice, totalPrice;
        ImageButton increaseButton, decreaseButton, deleteButton;

        // Constructor for ViewHolder, initializing all views in the item layout
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.item_number);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemPrice = itemView.findViewById(R.id.item_price);
            totalPrice = itemView.findViewById(R.id.total_price);
            increaseButton = itemView.findViewById(R.id.increase_button);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    // Interface to notify when quantity changes
    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }
}
