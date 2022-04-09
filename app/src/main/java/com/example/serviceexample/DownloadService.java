package com.example.serviceexample;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class DownloadService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private ArrayList<Ticker> tickers;
    private String token = "c8so24qad3ifkeaobkjg"; // put your own token

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            String result;
            String inputLine;

            for (int i = 0; i < tickers.size(); i++) {
                // url to get historical data
                String stringUrl = "https://finnhub.io/api/v1/stock/candle?symbol=" + tickers.get(i).getTicker()
                        + "&resolution=D&from=1625097601&to=1640995199&token=" + token;

                try {

                    // make GET requests

                    URL myUrl = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

                    connection.setRequestMethod(REQUEST_METHOD);
                    connection.setReadTimeout(READ_TIMEOUT);
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);

                    connection.connect();

                    // store json string from GET response

                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((inputLine = reader.readLine()) != null) {
                        stringBuilder.append(inputLine);
                    }

                    reader.close();
                    streamReader.close();

                    result = stringBuilder.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                    result = null;
                    Thread.currentThread().interrupt();
                }

                // parse the json string into 'open' and 'close' array

                JSONObject jsonObject = null;
                JSONArray jsonArrayOpen = null;
                JSONArray jsonArrayClose = null;

                try {
                    jsonObject = new JSONObject(result);
                    String s = jsonObject.getString("s");
                    if (s.equals("no_data")) {
                        tickers.get(i).setValid(false);
                        continue;
                    }
                    jsonArrayOpen = jsonObject.getJSONArray("o");
                    jsonArrayClose = jsonObject.getJSONArray("c");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("open", String.valueOf(jsonArrayOpen.length()));
                Log.v("close", String.valueOf(jsonArrayClose.length()));

                getContentResolver().delete(HistoricalDataProvider.CONTENT_URI, "ticker=?", new String[]{tickers.get(i).getTicker()});

                try {
                    for (int j = 0; j < jsonArrayClose.length(); j++) {
                        double open = jsonArrayOpen.getDouble(j);
                        double close = jsonArrayClose.getDouble(j);
                        // Log.v("data", ticker + ":, o: " + open  + " c: " + close);

                        ContentValues values = new ContentValues();
                        values.put(HistoricalDataProvider.TICKER, tickers.get(i).getTicker());
                        values.put(HistoricalDataProvider.OPEN, open);
                        values.put(HistoricalDataProvider.CLOSE, close);
                        getContentResolver().insert(HistoricalDataProvider.CONTENT_URI, values);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // broadcast message that download is complete
                Log.v("Download", "saved");
            }
            sendBroadcast();
            stopSelf(msg.arg1);
        }
    }

    private void sendBroadcast() {
        Intent intent = new Intent("download");
        intent.putParcelableArrayListExtra("tickers", tickers);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {

        HandlerThread thread = new HandlerThread("Service", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tickers = intent.getParcelableArrayListExtra("tickers");
        Toast.makeText(this, "download starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "download done", Toast.LENGTH_SHORT).show();
    }
}
