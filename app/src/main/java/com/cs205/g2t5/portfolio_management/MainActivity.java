package com.cs205.g2t5.portfolio_management;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button btnAdd, btnReset, btnDownload, btnCalculate;
    RecyclerView recyclerView;

    ArrayList<Ticker> dataList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    TickerAdapter tickerAdapter;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        btnAdd = findViewById(R.id.btn_add);
        btnReset = findViewById(R.id.btn_reset);
        btnDownload = findViewById(R.id.btn_download);
        btnCalculate = findViewById(R.id.btn_calculate);
        recyclerView = findViewById(R.id.recycler_view);

        // init linear layout manager
        linearLayoutManager = new LinearLayoutManager(this);

        // set layout manager
        recyclerView.setLayoutManager(linearLayoutManager);

        // init adapter
        tickerAdapter = new TickerAdapter(dataList, MainActivity.this);

        // set adapter
        recyclerView.setAdapter(tickerAdapter);

        btnDownload.setOnClickListener(v -> {
            btnReset.setEnabled(false);
            btnDownload.setEnabled(false);
            btnCalculate.setEnabled(false);
            Intent intent = new Intent(getApplicationContext(), DownloadService.class);
            intent.putParcelableArrayListExtra("tickers", dataList);
            startService(intent);
        });

        btnCalculate.setOnClickListener(v -> {
            btnDownload.setEnabled(false);
            btnCalculate.setEnabled(false);
            Intent intent = new Intent(getApplicationContext(), CalculateService.class);
            intent.putParcelableArrayListExtra("tickers", dataList);
            startService(intent);
        });

        btnAdd.setOnClickListener(v -> {
            // get string from edit text
            String sText = editText.getText().toString().trim();
            if (!sText.equals("")) {
                Ticker ticker = new Ticker(sText);

                // Clear the text field
                editText.setText("");

                // Add ticker if it does not exists
                if (!dataList.contains(ticker)) {
                    dataList.add(ticker);
                    tickerAdapter.notifyDataSetChanged();
                }
                // Display alert if ticker already exists
                else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                    // Setting message manually and performing action on button click
                    builder.setMessage("Ticker " + sText + " already exists!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", (dialog, id) -> dialog.cancel());
                    // Creating dialog box
                    AlertDialog alert = builder.create();
                    // Setting the title manually
                    alert.setTitle("Duplicate");
                    alert.show();
                }

            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
                // Setting message manually and performing action on button click
                builder.setMessage("The text field must not be empty!!")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialog, id) -> dialog.cancel());
                // Creating dialog box
                AlertDialog alert = builder.create();
                // Setting the title manually
                alert.setTitle("InvalidActionAlert");
                alert.show();
            }

            // Disable add button if maximum size of list reached
            if (dataList.size() == 5) {
                btnAdd.setEnabled(false);
            }
        });

        btnReset.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(v.getContext());
            // Setting message manually and performing action on button click
            builder.setMessage("Are you sure you want to delete all your tickers?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        dataList.clear();
                        tickerAdapter.notifyDataSetChanged();

                        // Enable add button since list in empty
                        btnAdd.setEnabled(true);
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    });
            // Creating dialog box
            AlertDialog alert = builder.create();
            // Setting the title manually
            alert.setTitle("Reset Confirmation");
            alert.show();
        });
    }

    private final BroadcastReceiver calcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Ticker> received = intent.getParcelableArrayListExtra("tickers");
            for (Ticker ticker : received) {
                int index = dataList.indexOf(ticker);
                if (index == -1) {
                    continue;
                }
                dataList.get(index).setAnnualisedReturn(ticker.getAnnualisedReturn());
                dataList.get(index).setAnnualisedVolatility(ticker.getAnnualisedVolatility());
                dataList.get(index).setCalculated(ticker.isCalculated());
            }
            tickerAdapter.notifyDataSetChanged();
            btnDownload.setEnabled(true);
            btnCalculate.setEnabled(true);
            btnReset.setEnabled(true);
        }
    };

    private final BroadcastReceiver deleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btnAdd.setEnabled(true);
        }
    };

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Ticker> received = intent.getParcelableArrayListExtra("tickers");
            for (Ticker ticker : received) {
                int index = dataList.indexOf(ticker);
                Log.v("index", String.valueOf(index));
                if (index == -1) {
                    continue;
                }
                Log.v("valid", String.valueOf(ticker.isValid()));
                dataList.get(index).setValid(ticker.isValid());
            }
            tickerAdapter.notifyDataSetChanged();
            btnDownload.setEnabled(true);
            btnCalculate.setEnabled(true);
            btnReset.setEnabled(true);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(calcReceiver, new IntentFilter("calculation"));
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteReceiver, new IntentFilter("delete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadReceiver, new IntentFilter("download"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(calcReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadReceiver);
    }
}