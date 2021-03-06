package com.cs205.g2t5.portfolio_management;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

/**
 * DownloadService is a Service that is ran on a separate thread. It downloads data over the
 * network from the Finnhub API and saves it to the SQLite database.
 * <p>
 * Refer to HistoricalDataProvider for more information on how the database is structured.
 * Upon completion, a broadcast will be sent to the Main Activity for the UI to be updated.
 */
public class DownloadService extends Service {
    private ServiceHandler serviceHandler;

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private ArrayList<Ticker> tickers;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            for (int i = 0; i < tickers.size(); i++) {
                String result;
                String inputLine;

                Cursor cursor = getContentResolver().query(HistoricalDataProvider.CONTENT_URI, new String[]{HistoricalDataProvider.OPEN, HistoricalDataProvider.CLOSE},
                        "ticker=?", new String[]{String.valueOf(tickers.get(i).getTicker())}, null);

                // Check if record exists and skip if there are 128 records
                if (cursor.getCount() == 128) {
                    continue;
                }

                // Deletes all records based on ticker name
                getContentResolver().delete(HistoricalDataProvider.CONTENT_URI, "ticker=?", new String[]{tickers.get(i).getTicker()});


                // url to get historical data, put your own token
                String token = getString(R.string.token);
                String stringUrl = getString(R.string.api_url, tickers.get(i).getTicker(), token);

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
                    Thread.currentThread().interrupt();
                    break;
                }

                // parse the json string into 'open' and 'close' array

                JSONObject jsonObject;
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

                if (jsonArrayClose == null) {
                    break;
                }

                Log.i("open", String.valueOf(jsonArrayOpen.length()));
                Log.v("close", String.valueOf(jsonArrayClose.length()));


                try {
                    for (int j = 0; j < jsonArrayClose.length(); j++) {
                        double open = jsonArrayOpen.getDouble(j);
                        double close = jsonArrayClose.getDouble(j);

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
        // Runs on a separate thread to prevent blocking UI thread
        HandlerThread thread = new HandlerThread("DownloadService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
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
