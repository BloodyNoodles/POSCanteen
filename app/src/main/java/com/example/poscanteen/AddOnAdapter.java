package com.example.poscanteen;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddOnAdapter extends RecyclerView.Adapter<AddOnAdapter.AddOnViewHolder> {

    private final Context context;
    private final List<AddOn> addOns;

    public AddOnAdapter(Context context, List<AddOn> addOns) {
        this.context = context;
        this.addOns = addOns;
    }

    @NonNull
    @Override
    public AddOnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_adds_on_button, parent, false);
        return new AddOnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddOnViewHolder holder, int position) {
        AddOn addOn = addOns.get(position);
        holder.addOnButton.setText(addOn.getName());
        holder.addOnPrice.setText(addOn.getFormattedPrice()); // Display formatted price

        // Change background color based on selection state
        if (addOn.isSelected()) {
            holder.addOnButton.setBackgroundColor(Color.LTGRAY); // Change this to your desired selected color
        } else {
            holder.addOnButton.setBackgroundColor(Color.TRANSPARENT); // Default color
        }

        holder.addOnButton.setOnClickListener(v -> {
            // Toggle selection state
            addOn.setSelected(!addOn.isSelected());
            notifyItemChanged(position); // Update UI for this item
        });
    }

    @Override
    public int getItemCount() {
        return addOns.size();
    }

    public static class AddOnViewHolder extends RecyclerView.ViewHolder {
        Button addOnButton;
        TextView addOnPrice;

        public AddOnViewHolder(@NonNull View itemView) {
            super(itemView);
            addOnButton = itemView.findViewById(R.id.addon_button);
            addOnPrice = itemView.findViewById(R.id.addon_price);
        }
    }
}
