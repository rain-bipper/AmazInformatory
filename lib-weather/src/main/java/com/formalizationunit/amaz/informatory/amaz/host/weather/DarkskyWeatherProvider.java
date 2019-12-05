package com.formalizationunit.amaz.informatory.amaz.host.weather;

import android.content.Context;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.amaz.host.weather.net.Net;
import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.common.models.WeatherModel.IconType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Integer.min;

public class DarkskyWeatherProvider implements WeatherProvider {
    private static final String TAG = "<DarkskyWeatherProvider>";
    private static final String HOST = "https://api.darksky.net/forecast/";
    //private static final String PARAMS = "?exclude=minutely,daily,alerts,flags&lang=ru&units=auto";
    private static final String PARAMS = "?exclude=minutely,alerts,flags&lang=ru&units=ca";

    private final GeocodingProvider mGeocodingProvider;
    private final String mApiKey;
    private final String mSpeedUnit;

    public DarkskyWeatherProvider(Context context, String weatherApiKey, String geocoderApiKey) {
        mApiKey = weatherApiKey;
        mGeocodingProvider = new GeocodingProvider(geocoderApiKey);

        mSpeedUnit = context.getResources().getString(R.string.speed_unit);
    }

    public void request(double lat, double lon, Callback callback) {
        String url = HOST + mApiKey + "/" + lat + "," + lon + PARAMS;
        mGeocodingProvider.request(lat, lon, new GeocodingProvider.Callback() {
            @Override
            public void onRequestDone(@Nullable String cityName) {
                Net.request(url, new Net.Callback() {
                    @Override
                    public void onDone(@Nullable String res) {
                        parse(res, cityName, callback);
                    }

                    @Override
                    public void onError(String reason) {
                        callback.onError(reason);
                    }
                });
            }

            @Override
            public void onError(String reason) {
                callback.onError(reason);
            }
        });
    }

    private void parse(@Nullable String data, @Nullable String cityName, Callback callback) {
        if (data == null) {
            onError(callback, "Received empty data");
            return;
        }

        JSONObject out = new JSONObject();

        try {
            JSONObject root = new JSONObject(data);
            JSONObject currently = root.getJSONObject("currently");

            if (!currently.has("time")) {
                onError(callback, "No timestamp received");
                return;
            }

            // UNIX time to Java time.
            out.put("time", currently.getLong("time") * 1000);

            // Unknown and static values.
            out.put("v", 1);
            out.put("weatherCode", 0);
            out.put("aqi", -1);
            out.put("aqiLevel", 0);
            out.put("pm25", -1);
            out.put("weather", 0);
            out.put("isAlert", false);
            out.put("isNotification", false);

            if (cityName != null) {
                out.put("city", cityName);
            }

            if (currently.has("temperature")) {
                String val = currently.getString("temperature");
                out.put("tempFormatted", val + "°C");
                out.put("tempUnit", "C");
            }

            hourParse(currently, out);

            try {
                JSONObject daily = root.optJSONObject("daily");
                JSONArray days = daily != null ? daily.getJSONArray("data") : null;

                if (days != null && days.length() > 0) {
                    JSONArray outDays = new JSONArray();
                    int daysCount = min(days.length(), 5);
                    int dayId = 1;

                    for (int i = 0; i < daysCount; ++i) {
                        JSONObject outDay = new JSONObject();
                        JSONObject day = days.getJSONObject(i);

                        copyOptionalJsonFloatRounding(day, outDay, "temperatureHigh",
                                "tempMax");
                        copyOptionalJsonFloatRounding(day, outDay, "temperatureLow", "tempMin");

                        int weatherCode = translateWeatherIcon(day.optString("icon"));
                        outDay.put("weatherCodeFrom", weatherCode);
                        outDay.put("weatherCodeTo", weatherCode);
                        outDay.put("day", dayId);
                        ++dayId;

                        outDays.put(outDay);
                    }

                    out.put("forecasts", outDays);
                }
            } catch (JSONException e) {
                Logger.log(TAG, "Failed to parse daily section: " + e.getLocalizedMessage());

                // Non-critical error, continue parsing.
            }

            try {
                JSONObject hourly = root.optJSONObject("hourly");
                JSONArray hours = hourly != null ? hourly.getJSONArray("data") : null;

                if (hours != null && hours.length() > 0) {
                    JSONArray outHours = new JSONArray();
                    int hoursCount = min(hours.length(), 24);

                    for (int i = 0; i < hoursCount; ++i) {
                        JSONObject outHour = new JSONObject();
                        JSONObject hour = hours.getJSONObject(i);

                        hourParse(hour, outHour);

                        outHours.put(outHour);
                    }
                    out.put("hourly", outHours);
                }
            } catch (JSONException e) {
                Logger.log(TAG, "Failed to parse hourly section: " + e.getLocalizedMessage());

                // Non-critical error, continue parsing.
            }

        } catch (JSONException e) {
            // Critical error. Return here.
            onError(callback, "Failed to form system data from weather: " +
                    e.getLocalizedMessage());
            return;
        }

        callback.onRequestDone(out.toString());
    }

    private void onError(Callback callback, String reason) {
        Logger.log(TAG, reason);
        callback.onError(reason);
    }

    private void copyOptionalJsonString(JSONObject in, JSONObject out, String key)
            throws JSONException {
        copyOptionalJsonString(in, out, key, key);
    }

    private void copyOptionalJsonString(JSONObject in, JSONObject out, String inKey, String outKey)
            throws JSONException {
        if (in.has(inKey)) {
            out.put(outKey, in.getString(inKey));
        }
    }

    private void copyOptionalJsonFloatRounding(JSONObject in, JSONObject out, String key)
            throws JSONException {
        copyOptionalJsonFloatRounding(in, out, key, key);
    }

    private void copyOptionalJsonFloatRounding(JSONObject in, JSONObject out, String inKey,
                                               String outKey)
            throws JSONException {
        copyOptionalJsonFloatRounding(in, out, inKey, outKey, null);
    }

    private void copyOptionalJsonFloatRounding(JSONObject in, JSONObject out, String inKey,
                                               String outKey, String suffix)
            throws JSONException {
        if (in.has(inKey)) {
            String val = String.valueOf(Math.round(in.getDouble(inKey)));
            if (suffix != null) {
                val += suffix;
            }
            out.put(outKey, val);
        }
    }

    private void hourParse(JSONObject in, JSONObject out) throws JSONException {
        out.put("time", in.getLong("time") * 1000);
        out.put("temp", Math.round(Float.valueOf(in.getString("temperature"))));
        //out.put("temp", "171");
        copyOptionalJsonFloatRounding(in, out, "apparentTemperature");
        copyOptionalJsonFloatRounding(in, out, "windSpeed", "windStrength", mSpeedUnit);
        copyOptionalJsonString(in, out, "uv");
        out.put("weatherCode", translateWeatherIcon(in.optString("icon")));

        if (in.has("windBearing")) {
            out.put("windDirection", getDirectionAsAbbreviation(in.getInt("windBearing")));
        }
        if (in.has("humidity")) {
            out.put("sd", (int)(in.getDouble("humidity") * 100) + "%");
        }
    }

    private String getDirectionAsAbbreviation(int degrees) {
        if (degrees > 359) {
            return "";
        }

        String[] abbreviations = {
            "N",
            //"NNE",
            "NE",
            //"ENE",
            "E",
            //"ESE",
            "SE",
            //"SSE",
            "S",
            //"SSW",
            "SW",
            //"WSW",
            "W",
            //"WNW",
            "NW",
            //"NNW"
        };

        final float degreesPerItem = 360f / abbreviations.length;
        final float halfDegreesPerItem = degreesPerItem / 2f;

        float shiftedDegrees = ((float)degrees) + halfDegreesPerItem;

        if (shiftedDegrees > 360) {
            shiftedDegrees -= 360;
        }

        int ind = (int)(shiftedDegrees / degreesPerItem);
        return abbreviations[ind];
    }

    private int translateWeatherIcon(String icon) {
        switch(icon) {
            case "clear-day":
            case "clear-night":
                return IconType.SUNNY;
            case "rain":
                return IconType.MODERATE_RAIN;
            case "snow":
                return IconType.MODERATE_SNOW;
            case "sleet":
                return IconType.SLEET;
            case "wind":
                return IconType.BLOWING_SAND;
            case "fog":
                return IconType.FOG;
            case "cloudy":
                return IconType.OVERCAST;
            case "partly-cloudy-day":
            case "partly-cloudy-night":
                return IconType.CLOUDY;
        }

        return IconType.UNKNOWN;
    }
}

// Make JSON in watch format:
/*
{
   "isAlert":true,
   "isNotification":true,
   "tempFormatted":"28ºC",
   "tempUnit":"C",
   "v":1,
   "weatherCode":0,
   "aqi":-1,
   "aqiLevel":0,
   "city":"Somewhere",
   "forecasts":[
      {
         "tempFormatted":"31ºC/21ºC",
         "tempMax":31,
         "tempMin":21,
         "weatherCodeFrom":0,
         "weatherCodeTo":0,
         "day":1,
         "weatherFrom":0,
         "weatherTo":0
      },
      {
         "tempFormatted":"33ºC/23ºC",
         "tempMax":33,
         "tempMin":23,
         "weatherCodeFrom":0,
         "weatherCodeTo":0,
         "day":2,
         "weatherFrom":0,
         "weatherTo":0
      },
      {
         "tempFormatted":"34ºC/24ºC",
         "tempMax":34,
         "tempMin":24,
         "weatherCodeFrom":0,
         "weatherCodeTo":0,
         "day":3,
         "weatherFrom":0,
         "weatherTo":0
      },
      {
         "tempFormatted":"34ºC/23ºC",
         "tempMax":34,
         "tempMin":23,
         "weatherCodeFrom":0,
         "weatherCodeTo":0,
         "day":4,
         "weatherFrom":0,
         "weatherTo":0
      },
      {
         "tempFormatted":"32ºC/22ºC",
         "tempMax":32,
         "tempMin":22,
         "weatherCodeFrom":0,
         "weatherCodeTo":0,
         "day":5,
         "weatherFrom":0,
         "weatherTo":0
      }
   ],
   "pm25":-1,
   "sd":"50%",
   "temp":28,
   "time":1531292274457,
   "uv":"Strong",
   "weather":0,
   "windDirection":"NW",
   "windStrength":"7.4km/h"
}
*/