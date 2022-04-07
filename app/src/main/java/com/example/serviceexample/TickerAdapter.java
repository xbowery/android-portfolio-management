package com.example.serviceexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TickerAdapter extends RecyclerView.Adapter<TickerAdapter.ViewHolder> {
    //initialize variables

    private List<Ticker> dataList;
    private Activity context;

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

        //set text in textview
        holder.textViewTicker.setText(data.getTicker());
        holder.textViewReturns.setText(String.valueOf(data.getAnnualisedReturn()));
        holder.textViewVolatility.setText(String.valueOf(data.getAnnualisedVolatility()));

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init main data
                Ticker d = dataList.get(holder.getAdapterPosition());

                // create dialog
                Dialog dialog = new Dialog(context);

                // set content view
                dialog.setContentView(R.layout.dialog_update);

                // init width
                int width = WindowManager.LayoutParams.MATCH_PARENT;

                // int height
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                // set layout
                dialog.getWindow().setLayout(width, height);

                // show dialog
                dialog.show();

                // init and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                Button btUpdate = dialog.findViewById(R.id.btn_update);

                // set text on edit text
                editText.setText(d.getTicker());

                btUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // dismiss dialog
                        dialog.dismiss();

                        //get update text from edit text
                        String newTicker = editText.getText().toString().trim();
                        d.setTicker(newTicker);

                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(v.getContext());
                // Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete this task?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int position = holder.getAdapterPosition();
                                dataList.remove(position);

                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, dataList.size());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                // Creating dialog box
                AlertDialog alert = builder.create();
                // Setting the title manually
                alert.setTitle("DeleteConfirmation");
                alert.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTicker, textViewReturns, textViewVolatility;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assign variable
            textViewTicker = itemView.findViewById(R.id.text_view_ticker);
            textViewReturns = itemView.findViewById(R.id.text_view_returns);
            textViewVolatility = itemView.findViewById(R.id.text_view_volatility);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
