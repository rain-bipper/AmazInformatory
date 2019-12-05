package com.formalizationunit.amaz.informatory.amaz.host.weather;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.amaz.host.weather.net.Net;
import com.formalizationunit.amaz.informatory.common.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

class GeocodingProvider {
    private static final String TAG = "<GeocodingProvider>";
    private static final String HOST = "https://geocode-maps.yandex.ru/1.x/?geocode=";
    private static final String PARAMS = "&kind=locality&format=json&results=1";

    private String mApiKey;

    public interface Callback {
        void onRequestDone(@Nullable String cityName);
        void onError(String reason);
    }

    GeocodingProvider(String apiKey) {
        mApiKey = apiKey;
    }

    void request(double lat, double lon, Callback callback) {
        String url = HOST + lon + "," + lat + PARAMS + "&apikey=" + mApiKey;
        Net.request(url, new Net.Callback() {
            @Override
            public void onDone(@Nullable String res) {
                parse(res, callback);
            }

            @Override
            public void onError(String reason) {
                callback.onError(reason);
            }
        });
    }

    private void onError(Callback callback, String reason) {
        Logger.log(TAG, reason);
        callback.onError(reason);
    }

    @Nullable
    private void parse(@Nullable String data, Callback callback) {
        if (data == null) {
            onError(callback, "No data received");
            return;
        }

        try {
            callback.onRequestDone(new JSONObject(data)
                    .getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                    .optJSONObject(0)
                    .getJSONObject("GeoObject")
                    .getString("name"));

        } catch (NullPointerException | JSONException e) {
            onError(callback, "Failed geocode: " + e.getMessage());
        }
    }
}
