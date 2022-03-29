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
                    while (!cursor.isAfterLast()) {
                        double open = cursor.getDouble(cursor.getColumnIndexOrThrow(HistoricalDataProvider.OPEN));
                        double close = cursor.getDouble(cursor.getColumnIndexOrThrow(HistoricalDataProvider.CLOSE));

                        double rate = (close - open) / open;
                        sum_growth += rate;
                        rateList.add(rate);
                        sum_growth_square += Math.pow(rate, 2);

                        cursor.moveToNext();
                    }
                } else {
                    result.setText("No Records Found");
                    return;
                }

                    double annualisedReturn = sum_price / sum_volume;
                    result.setText(String.format("%.2f", annualisedReturn));

                double annualisedGrowth = average * rows;
                double annualisedVolatility = sd * Math.sqrt(rows);

                Log.i("Annualised Growth", String.valueOf(annualisedGrowth));
                Log.i("Annualised Volatility", String.valueOf(annualisedVolatility));

                result.setText(String.format("%.2f %%", annualisedGrowth * 100));
            }
        });
    }
}
