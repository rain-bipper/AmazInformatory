package com.formalizationunit.amaz.informatory.amaz.host.weather;

public interface WeatherProvider {
    interface Callback {
        void onRequestDone(String weatherJson);
        void onError(String reason);
    }

    void request(double lat, double lon, Callback callback);
}
