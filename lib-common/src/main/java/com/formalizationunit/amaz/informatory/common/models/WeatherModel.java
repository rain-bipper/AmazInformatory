package com.formalizationunit.amaz.informatory.common.models;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.common.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherModel {
    private static final String TAG = "<WeatherModel>";
    public final String cityName;
    public final HourInfo current;
    public final List<HourInfo> hourInfos;

    @IntDef(value = {
        IconType.SUNNY,
        IconType.CLOUDY,
        IconType.OVERCAST,
        IconType.FOG,
        IconType.SMOG,
        IconType.SHOWER,
        IconType.THUNDER_SHOWER,
        IconType.LIGHT_RAIN,
        IconType.MODERATE_RAIN,
        IconType.HEAVY_RAIN,
        IconType.RAINSTORM,
        IconType.TORRENTIAL_RAIN,
        IconType.SLEET,
        IconType.FREEZING_RAIN,
        IconType.HAIL,
        IconType.LIGHT_SNOW,
        IconType.MODERATE_SNOW,
        IconType.HEAVY_SNOW,
        IconType.SNOWSTORM,
        IconType.DUST,
        IconType.BLOWING_SAND,
        IconType.SAND_STORM,
        IconType.UNKNOWN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface IconType {
        int SUNNY = 0;
        int CLOUDY = 1;
        int OVERCAST = 2;
        int FOG = 3;
        int SMOG = 4;
        int SHOWER = 5;
        int THUNDER_SHOWER = 6;
        int LIGHT_RAIN = 7;
        int MODERATE_RAIN = 8;
        int HEAVY_RAIN = 9;
        int RAINSTORM = 10;
        int TORRENTIAL_RAIN = 11;
        int SLEET = 12;
        int FREEZING_RAIN = 13;
        int HAIL = 14;
        int LIGHT_SNOW = 15;
        int MODERATE_SNOW = 16;
        int HEAVY_SNOW = 17;
        int SNOWSTORM = 18;
        int DUST = 19;
        int BLOWING_SAND = 20;
        int SAND_STORM = 21;
        int UNKNOWN = 22;
    }

    private WeatherModel(String cityName, HourInfo current, List<HourInfo> hourInfos) {
        this.cityName = cityName;
        this.current = current;
        this.hourInfos = hourInfos;
    }

    public static class HourInfo {
        public final long time;
        public final float temperature;
        @Nullable
        public final Float apparentTemperature;
        @Nullable
        public final Float windSpeed;
        //@Nullable
        //public final Integer windDirection;
        @Nullable
        public final Float humidity;
        @Nullable
        public final Float uv;
        public final @IconType
        int icon;

        HourInfo(long time, float temperature, @Nullable Float apparentTemperature,
                        @Nullable Float windSpeed, //@Nullable Integer windDirection,
                        @Nullable Float humidity, @Nullable Float uv,
                        int icon) {
            this.time = time;
            this.temperature = temperature;
            this.apparentTemperature = apparentTemperature;
            this.windSpeed = windSpeed;
            this.humidity = humidity;
            this.uv = uv;
            //this.windDirection = windDirection;
            this.icon = icon;
        }

        @NonNull
        @Override
        public String toString() {
            DateFormat time = SimpleDateFormat.getTimeInstance();
            return "[" + time.format(new Date(this.time)) + ", " + temperature + "°(" +
                    apparentTemperature + "°), " + icon + "]";
        }

        private static HourInfo fromJson(JSONObject data) throws JSONException {
            return new HourInfo(
                    data.getLong("time"),
                    (float)data.getDouble("temp"),
                    getOptionalFloat(data,"apparentTemperature"),
                    getOptionalFloat(data, "windSpeed"),
                    //getOptionalInt(data, "windDirection"),
                    getOptionalFloat(data, "humidity"),
                    getOptionalFloat(data, "uvIndex"),
                    data.getInt("weatherCode")
            );
        }
    }

    @Nullable
    public static WeatherModel fromJson(String jsonData) {
        try {
            JSONObject json = new JSONObject(jsonData);
            JSONArray hourInfosData = json.getJSONArray("hourly");
            List<HourInfo> hourInfos = new ArrayList<>();

            for (int i = 0; i < hourInfosData.length(); ++i) {
                JSONObject hourInfoJson = hourInfosData.getJSONObject(i);
                hourInfos.add(HourInfo.fromJson(hourInfoJson));
            }

            return new WeatherModel(json.optString("city"),
                    HourInfo.fromJson(json), hourInfos);
        } catch (NullPointerException | JSONException e) {
            Logger.log(TAG, "Failed to parse weather data: " + e.getLocalizedMessage());
            return null;
        }
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[" + current + ": {");

        for (HourInfo info : hourInfos) {
            str.append(info);
            str.append(",");
        }

        str.append("}");
        return str.toString();
    }

    private static Float getOptionalFloat(JSONObject obj, String key) throws JSONException {
        if (!obj.has(key)) {
            return null;
        }

        return (float) obj.getDouble(key);
    }

    private static Integer getOptionalInt(JSONObject obj, String key) throws JSONException {
        if (!obj.has(key)) {
            return null;
        }

        return obj.getInt(key);
    }
}
