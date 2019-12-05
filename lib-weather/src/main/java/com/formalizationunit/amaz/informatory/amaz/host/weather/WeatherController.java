package com.formalizationunit.amaz.informatory.amaz.host.weather;

import android.content.Context;
import android.location.Location;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorModel;

public class WeatherController {
    private static final String TAG = "<WeatherController>";
    private final Context mContext;
    private final WeatherProvider mWeather;
    private final CommunicatorModel mCommunicator;

    public interface Callback {
        void onDone();
        void onError(String reason);
    }

    public WeatherController(Context context, CommunicatorModel communicator, String weatherApiKey,
                             String geocoderApiKey) {
        mContext = context;
        mCommunicator = communicator;

        mWeather = new DarkskyWeatherProvider(context, weatherApiKey, geocoderApiKey);
    }

    public void run(Callback callback) {
        Location location = LocationProvider.getLocation(mContext);

        if (location == null) {
            onError(callback, "Failed to get location");
            return;
        }

        mWeather.request(location.getLatitude(), location.getLongitude(),
                new WeatherProvider.Callback() {
            @Override
            public void onRequestDone(String weatherJson) {
                onWeatherReceived(weatherJson, callback);
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

    private void onWeatherReceived(String weatherJson, Callback callback) {
        mCommunicator.sendWeather(weatherJson, callback::onDone);
    }
}
