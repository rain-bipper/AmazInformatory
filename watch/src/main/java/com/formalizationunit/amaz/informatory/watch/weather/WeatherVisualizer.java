package com.formalizationunit.amaz.informatory.watch.weather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.common.models.WeatherModel;
import com.formalizationunit.amaz.informatory.common.models.WeatherModel.IconType;
import com.formalizationunit.amaz.informatory.watch.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

class WeatherVisualizer {
    private static final String TAG = "<WeatherVisualizer>";

    static void show(WeatherModel data, View container) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long currentHourMs = calendar.getTimeInMillis();
        long nextHourMs = currentHourMs + 1000 * 60 * 60;

        for (int i = 0, hoursInfoInd = 0, hour = calendar.get(Calendar.HOUR_OF_DAY); i < 12;
             ++i, currentHourMs = nextHourMs, nextHourMs += 1000 * 60 * 60, ++hour) {
            String temperature = "?";
            @IconType int iconType = IconType.UNKNOWN;

            for (; hoursInfoInd < data.hourInfos.size(); ++hoursInfoInd) {
                WeatherModel.HourInfo hourInfo = data.hourInfos.get(hoursInfoInd);
                if (hourInfo.time >= currentHourMs) {
                    if (hourInfo.time < nextHourMs) {
                        temperature = Math.round(hourInfo.temperature) + "°";
                                //+ "(" + Math.round(hourInfo.apparentTemperature) + "°)";
                        iconType = hourInfo.icon;

                        long tsTime = new Date(hourInfo.time).getHours();
                        if (tsTime != (hour % 24)) {
                            throw new AssertionError();
                        }
                    }
                    break;
                }
            }

            View dataView = container.findViewWithTag("data" + (hour % 12));
            TextView hourView = container.findViewWithTag("hour" + (hour % 12));
            TextView temperatureView = dataView.findViewById(R.id.temperature);
            ImageView imageView = dataView.findViewById(R.id.icon);

            hourView.setText(String.valueOf(hour % 24));
            temperatureView.setText(temperature);

            Drawable icon = loadIcon(container.getContext(), iconType);
            if (icon != null) {
                int gradient = 0xff * i / 12;
                int color = 0xffff0000 | (gradient << 8) | (gradient);
                icon.setTint(color);
            }
            imageView.setImageDrawable(icon);
        }

        ((TextView)container.findViewById(R.id.temperature)).setText(
                Math.round(data.current.temperature) + "°");

        if (data.current.apparentTemperature != null) {
            ((TextView)container.findViewById(R.id.apparent_temperature)).setText(
                    "(" + Math.round(data.current.apparentTemperature) + "°)");
        }
        Drawable icon = loadIcon(container.getContext(), data.current.icon);
        ((ImageView)container.findViewById(R.id.icon)).setImageDrawable(icon);

        ((TextView)container.findViewById(R.id.city)).setText(data.cityName);

        //String time = SimpleDateFormat.getTimeInstance().format(new Date());
        //((TextView)container.findViewById(R.id.time)).setText(time);

        //Logger.log(TAG, "Got weather at " + time);
    }

    @Nullable
    private static Drawable loadIcon(Context context, @IconType int summary) {
        String asset;

        switch (summary) {

            case IconType.BLOWING_SAND:
                asset = "blowing_sand.png";
                break;
            case IconType.CLOUDY:
                asset = "cloudy.png";
                break;
            case IconType.DUST:
                asset = "dust.png";
                break;
            case IconType.FOG:
                asset = "fog.png";
                break;
            case IconType.FREEZING_RAIN:
                asset = "freezing_rain.png";
                break;
            case IconType.HAIL:
                asset = "hail.png";
                break;
            case IconType.HEAVY_RAIN:
                asset = "heavy_rain.png";
                break;
            case IconType.HEAVY_SNOW:
                asset = "heavy_snow.png";
                break;
            case IconType.LIGHT_RAIN:
                asset = "light_rain.png";
                break;
            case IconType.LIGHT_SNOW:
                asset = "light_snow.png";
                break;
            case IconType.MODERATE_RAIN:
                asset = "moderate_rain.png";
                break;
            case IconType.MODERATE_SNOW:
                asset = "moderate_snow.png";
                break;
            case IconType.OVERCAST:
                asset = "overcast.png";
                break;
            case IconType.RAINSTORM:
                asset = "rainstorm.png";
                break;
            case IconType.SAND_STORM:
                asset = "sand_storm.png";
                break;
            case IconType.SHOWER:
                asset = "shower.png";
                break;
            case IconType.SLEET:
                asset = "sleet.png";
                break;
            case IconType.SMOG:
                asset = "smog.png";
                break;
            case IconType.SNOWSTORM:
                asset = "snowstorm.png";
                break;
            case IconType.SUNNY:
                asset = "sunny.png";
                break;
            case IconType.THUNDER_SHOWER:
                asset = "thunder_shower.png";
                break;
            case IconType.TORRENTIAL_RAIN:
                asset = "thunder_shower.png";
                break;
            case IconType.UNKNOWN:
                asset = "unknown.png";
                break;

            default:
                return null;
        }

        try (InputStream ims = context.getAssets().open("weather/" + asset)) {
            return Drawable.createFromStream(ims, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
