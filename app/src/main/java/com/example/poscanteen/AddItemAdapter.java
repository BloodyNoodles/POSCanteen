package com.example.poscanteen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddItemAdapter extends RecyclerView.Adapter<AddItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AddItem> addItemList;
    private OnItemChangeListener onItemChangeListener;

    // Define an interface to handle item changes
    public interface OnItemChangeListener {
        void onQuantityChange(int position, int newQuantity);
        void onDeleteItem(int position);
    }

    // Constructor
    public AddItemAdapter(Context context, ArrayList<AddItem> addItemList, OnItemChangeListener listener) {
        this.context = context;
        this.addItemList = addItemList;
        this.onItemChangeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_additem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddItem addItem = addItemList.get(position);
        holder.itemNameTextView.setText(addItem.getName());
        holder.itemPriceTextView.setText("₱ " + String.format("%.2f", addItem.getUnitPrice()));
        holder.itemQuantityTextView.setText(String.valueOf(addItem.getQuantity()));
        holder.totalPriceTextView.setText("₱ " + String.format("%.2f", addItem.getTotalPrice()));

        // Set up button click listeners for increase, decrease, and delete
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = addItem.getQuantity() + 1;
            addItem.setQuantity(newQuantity);
            holder.itemQuantityTextView.setText(String.valueOf(newQuantity));
            holder.totalPriceTextView.setText("₱ " + String.format("%.2f", addItem.getTotalPrice()));
            if (onItemChangeListener != null) {
                onItemChangeListener.onQuantityChange(position, newQuantity);
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (addItem.getQuantity() > 1) { // Prevent quantity from going below 1
                int newQuantity = addItem.getQuantity() - 1;
                addItem.setQuantity(newQuantity);
                holder.itemQuantityTextView.setText(String.valueOf(newQuantity));
                holder.totalPriceTextView.setText("₱ " + String.format("%.2f", addItem.getTotalPrice()));
                if (onItemChangeListener != null) {
                    onItemChangeListener.onQuantityChange(position, newQuantity);
                }
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onItemChangeListener != null) {
                onItemChangeListener.onDeleteItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        TextView itemQuantityTextView;
        TextView itemPriceTextView;
        TextView totalPriceTextView;
        ImageButton increaseButton;
        ImageButton decreaseButton;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name);
            itemQuantityTextView = itemView.findViewById(R.id.item_quantity);
            itemPriceTextView = itemView.findViewById(R.id.item_price);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
            increaseButton = itemView.findViewById(R.id.increase_button);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
