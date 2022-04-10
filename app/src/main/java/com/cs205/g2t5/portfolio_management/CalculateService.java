package com.cs205.g2t5.portfolio_management;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class CalculateService extends Service {
    private CalculationHandler calculationHandler;
    private ArrayList<Ticker> tickers;
    private final ArrayList<Ticker> returnList = new ArrayList<>();

    private final class CalculationHandler extends Handler {
        public CalculationHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Uri CONTENT_URI = Uri.parse(HistoricalDataProvider.URL);
            for (Ticker ticker : tickers) {
                if (!ticker.isValid()) {
                    continue;
                }

                Log.v("Retrieving", ticker.getTicker());
                double sum_growth = 0.0;
                List<Double> rateList = new ArrayList<>();

                Cursor cursor = getContentResolver().query(CONTENT_URI, new String[]{HistoricalDataProvider.OPEN, HistoricalDataProvider.CLOSE},
                        "ticker=?", new String[]{String.valueOf(ticker.getTicker())}, null);

                if (cursor == null) {
                    Log.v("Cursor Err", ticker.getTicker());
                    continue;
                }
                int rows = cursor.getCount();
                if (rows == 0) {
                    continue;
                }

                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        double open = cursor.getDouble(cursor.getColumnIndexOrThrow(HistoricalDataProvider.OPEN));
                        double close = cursor.getDouble(cursor.getColumnIndexOrThrow(HistoricalDataProvider.CLOSE));

                        double rate = (close - open) / open;
                        sum_growth += rate;
                        rateList.add(rate);

                        cursor.moveToNext();
                    }
                } else {
                    continue;
                }

                double average = sum_growth / rows;

                double sum = 0.0;
                for (double rate : rateList) {
                    sum += Math.pow(rate - average, 2);
                }
                double sd = Math.sqrt(sum / (rows - 1));

                double annualisedReturn = average * 252;
                double annualisedVolatility = sd * Math.sqrt(252);

                Log.i("Annualised Growth", String.valueOf(annualisedReturn * 100));
                Log.i("Annualised Volatility", String.valueOf(annualisedVolatility * 100));

                ticker.setAnnualisedReturn(annualisedReturn * 100);
                ticker.setAnnualisedVolatility(annualisedVolatility * 100);
                ticker.setCalculated(true);

                returnList.add(ticker);
                cursor.close();
            }
            sendBroadcast();
            stopSelf(msg.arg1);
        }
    }

    private void sendBroadcast() {
        Intent intent = new Intent("calculation");
        intent.putParcelableArrayListExtra("tickers", returnList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("CalculateService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
        calculationHandler = new CalculationHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tickers = intent.getParcelableArrayListExtra("tickers");
        Toast.makeText(this, "calculation starting", Toast.LENGTH_SHORT).show();

        Message msg = calculationHandler.obtainMessage();
        msg.arg1 = startId;
        calculationHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "calculation done", Toast.LENGTH_SHORT).show();
    }
}
