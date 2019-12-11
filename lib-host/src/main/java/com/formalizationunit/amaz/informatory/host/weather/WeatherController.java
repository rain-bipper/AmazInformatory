package com.formalizationunit.amaz.informatory.host.weather;

import android.content.Context;
import android.location.Location;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorHost;

public class WeatherController {
    private static final String TAG = "<WeatherController>";
    private final Context mContext;
    private final WeatherProvider mWeather;
    private final CommunicatorHost mCommunicator;

    private boolean mDestroyed;

    public interface Callback {
        void onDone();
        void onError(String reason);
    }

    public WeatherController(Context context, CommunicatorHost communicator,
                             ApiKeyProvider apiKeyProvider) {
        mContext = context;
        mCommunicator = communicator;

        mWeather = new DarkskyWeatherProvider(context, apiKeyProvider);
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

    public void destroy() {
        mDestroyed = true;
    }

    private void onError(Callback callback, String reason) {
        Logger.log(TAG, reason);
        callback.onError(reason);
    }

    private void onWeatherReceived(String weatherJson, Callback callback) {
        if (!mDestroyed) {
            mCommunicator.sendWeather(weatherJson, callback::onDone);
        }
    }
}
