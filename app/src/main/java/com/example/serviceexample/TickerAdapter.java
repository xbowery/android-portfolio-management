package com.example.serviceexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TickerAdapter extends RecyclerView.Adapter<TickerAdapter.ViewHolder> {
    //initialize variables

    private final List<Ticker> dataList;
    private final Activity context;

    AlertDialog.Builder builder;

    //create constructor
    public TickerAdapter(List<Ticker> dataList, Activity context) {
        this.dataList = dataList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialise view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TickerAdapter.ViewHolder holder, int position) {
        Ticker data = dataList.get(position);

        // set text in textview
        if (data.isValid()) {
            holder.textViewTicker.setText(data.getTicker());
            holder.textViewTicker.setTextColor(Color.BLACK);
        } else {
            String invalidText = data.getTicker() + " (Invalid)";
            holder.textViewTicker.setText(invalidText);
            holder.textViewTicker.setTextColor(Color.RED);
        }

        holder.textViewReturns.setText(data.isCalculated() ? context.getString(R.string.percentage_format, data.getAnnualisedReturn()) : "-");
        holder.textViewVolatility.setText(data.isCalculated() ? context.getString(R.string.percentage_format, data.getAnnualisedVolatility()) : "-");

        holder.btnDelete.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(v.getContext());
            // Setting message manually and performing action on button click
            builder.setMessage("Are you sure you want to delete this ticker?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        int position1 = holder.getAdapterPosition();
                        dataList.remove(position1);

                        notifyItemRemoved(position1);
                        notifyItemRangeChanged(position1, dataList.size());
                        Intent intent = new Intent("delete");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    });
            // Creating dialog box
            AlertDialog alert = builder.create();
            // Setting the title manually
            alert.setTitle("Delete Confirmation");
            alert.show();
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTicker, textViewReturns, textViewVolatility;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assign variable

            textViewTicker = itemView.findViewById(R.id.text_view_ticker);
            textViewReturns = itemView.findViewById(R.id.text_view_returns);
            textViewVolatility = itemView.findViewById(R.id.text_view_volatility);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
