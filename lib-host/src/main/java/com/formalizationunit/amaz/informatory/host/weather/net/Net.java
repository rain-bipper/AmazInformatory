package com.formalizationunit.amaz.informatory.host.weather.net;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.common.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

public class Net {
    private static final String TAG = "<Net>";
    public interface Callback {
        void onDone(@Nullable String res);
        void onError(String reason);
    }

    public static void request(String url, Callback callback) {
        new AsyncTask<String, Void, String>() {
            private String error;

            @Override
            protected String doInBackground(String... voids) {
                try (InputStream is = new URL(url).openConnection().getInputStream();
                     BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    return br.lines().collect(Collectors.joining(System.lineSeparator()));
                } catch (IOException e) {
                    error = "Net request to " + url + " failed: " + e.getMessage();
                    Logger.log(TAG, error);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable String res) {
                super.onPostExecute(res);

                if (res == null && error != null) {
                    callback.onError(error);
                } else {
                    callback.onDone(res);
                }
            }
        }.execute(url);
    }
}
