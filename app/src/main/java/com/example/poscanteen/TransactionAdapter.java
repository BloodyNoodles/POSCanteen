package com.example.poscanteen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private final TransactionClickListener clickListener;

    public TransactionAdapter(List<Transaction> transactionList, TransactionClickListener clickListener) {
        this.transactionList = transactionList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateList(List<Transaction> newTransactionList) {
        // Update the transaction list with the filtered data
        transactionList.clear();
        transactionList.addAll(newTransactionList);
        // Notify adapter that the data set has changed to refresh the view
        notifyDataSetChanged();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionName;
        TextView transactionDate;
        TextView transactionOrderNumber;
        TextView transactionAmount;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.transactionName);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            transactionOrderNumber = itemView.findViewById(R.id.transactionOrderNumber);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);

            itemView.setOnClickListener(v -> clickListener.onTransactionClick(transactionList.get(getAdapterPosition())));
        }

        void bind(Transaction transaction) {
            transactionName.setText(transaction.getProductName());

            // Assuming the transaction date and other fields may be null
            transactionDate.setText(transaction.getDate() != null ? transaction.getDate() : "No Date");

            // Assuming order number is an integer and cannot be null
            transactionOrderNumber.setText(String.format("Order #: %s", transaction.getOrderNumber()));

            // If total price is a Double, handle potential null values
            double totalPrice = transaction.getTotalPrice();
            transactionAmount.setText(String.format("₱ %.2f", totalPrice));
        }
    }

    public interface TransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }
}
