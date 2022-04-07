package com.example.serviceexample;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
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

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DownloadService.class);
                intent.putParcelableArrayListExtra("tickers", dataList);
                startService(intent);
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalculateService.class);
                intent.putParcelableArrayListExtra("tickers", dataList);
                startService(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get string from edit text
                String sText = editText.getText().toString().trim();
                if (!sText.equals("")) {
                    Ticker ticker = new Ticker(sText);

                    // clear
                    editText.setText("");

                    dataList.add(ticker);
                    tickerAdapter.notifyDataSetChanged();

                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                    //Setting message manually and performing action on button click
                    builder.setMessage("The text field must not be empty!!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("InvalidActionAlert");
                    alert.show();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(v.getContext());
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete all your todos?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dataList.clear();
                                tickerAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("ResetConfirmation");
                alert.show();

            }
        });
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Ticker> received = intent.getParcelableArrayListExtra("tickers");
            for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).setAnnualisedReturn(received.get(i).getAnnualisedReturn());
                dataList.get(i).setAnnualisedVolatility(received.get(i).getAnnualisedVolatility());
            }
            tickerAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("calculation"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }
}