package com.example.serviceexample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private final Handler handler;

    public MyBroadcastReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("DOWNLOAD_COMPLETE")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Uri CONTENT_URI = Uri.parse(HistoricalDataProvider.URL);
                    TextView result = (TextView) ((Activity) context).findViewById(R.id.textview_result);
                    EditText ticker = (EditText) ((Activity) context).findViewById(R.id.edit_ticker);
                    result.setText("Calculating...");

                    double sum_growth = 0.0;
                    double sum_growth_square = 0.0;
                    List<Double> rateList = new ArrayList<>();

                    Cursor cursor = context.getContentResolver().query(CONTENT_URI, new String[]{HistoricalDataProvider.OPEN, HistoricalDataProvider.CLOSE},
                            "ticker=?", new String[]{String.valueOf(ticker.getText())}, null);

                    int rows = cursor.getCount();

                    if (cursor.moveToFirst()) {
                        double close = cursor.getDouble(cursor.getColumnIndexOrThrow("close"));
                        double volume = cursor.getDouble(cursor.getColumnIndexOrThrow("volume"));
                        sum_price += close * volume;
                        sum_volume += volume;
                        while (!cursor.isAfterLast()) {
                            int id = cursor.getColumnIndex("id");
                            close = cursor.getDouble(cursor.getColumnIndexOrThrow("close"));
                            volume = cursor.getDouble(cursor.getColumnIndexOrThrow("volume"));
                            sum_price += close * volume;
                            sum_volume += volume;
                            cursor.moveToNext();
                            Log.v("data", close + "");
                        }
                    } else {
                        result.setText("No Records Found");
                    }

                    double vwap = sum_price / sum_volume;
                    result.setText(String.format("%.2f", vwap));

                }
            });
        }
    }
}
