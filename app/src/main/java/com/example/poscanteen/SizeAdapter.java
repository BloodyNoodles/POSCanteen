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

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    private final Context context;
    private final List<Size> sizes;

    public SizeAdapter(Context context, List<Size> sizes) {
        this.context = context;
        this.sizes = sizes;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_sizes_button, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        Size size = sizes.get(position);
        holder.sizeButton.setText(size.getName());
        holder.sizePrice.setText(size.getFormattedPrice()); // Use formatted price method

        // Change background color based on selection state
        if (size.isSelected()) {
            holder.sizeButton.setBackgroundColor(Color.LTGRAY); // Change this to your desired selected color
        } else {
            holder.sizeButton.setBackgroundColor(Color.TRANSPARENT); // Default color
        }

        holder.sizeButton.setOnClickListener(v -> {
            // Toggle selection state
            for (Size s : sizes) {
                s.setSelected(false); // Deselect all sizes
            }
            size.setSelected(true); // Select the current size
            notifyDataSetChanged(); // Notify that data has changed
        });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    public static class SizeViewHolder extends RecyclerView.ViewHolder {
        Button sizeButton;
        TextView sizePrice;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            sizeButton = itemView.findViewById(R.id.size_button);
            sizePrice = itemView.findViewById(R.id.size_price);
        }
    }
}
