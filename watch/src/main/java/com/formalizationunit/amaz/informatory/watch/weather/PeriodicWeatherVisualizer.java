package com.formalizationunit.amaz.informatory.watch.weather;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.MainThread;

import com.formalizationunit.amaz.informatory.common.util.PeriodicProcessor;
import com.formalizationunit.amaz.informatory.watch.R;
import com.formalizationunit.amaz.informatory.common.util.UiRunner;

import java.util.Calendar;

@MainThread
class PeriodicWeatherVisualizer {
    private static final int PERIOD_MS = 6000;

    private final View mHourHand;
    private final View mMinuteHand;
    private final TextView mElapsedTimeFromUpdateView;

    private Long mLastUpdateTime;
    private long mLastSetMinutesAgo = -1;

    private final PeriodicProcessor mPeriodicProcessor;

    @MainThread
    PeriodicWeatherVisualizer(UiRunner uiRunner, Long lastUpdateTime, View hourHand, View minuteHand,
                              TextView updateView) {
        mLastUpdateTime = lastUpdateTime;
        mHourHand = hourHand;
        mMinuteHand = minuteHand;
        mElapsedTimeFromUpdateView = updateView;

        mPeriodicProcessor = new PeriodicProcessor(uiRunner, () -> {
            Calendar calendar = Calendar.getInstance();
            long millis = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            double millisFromMidnight = (millis - calendar.getTimeInMillis());
            float hour = (float) ((millisFromMidnight / 3600000) % 12);
            float minute = (float) ((millisFromMidnight % 3600000) / 60000);

            float minuteDegree = minute * 6 - 90;
            float hourDegree = hour * 30 - 90;

            mHourHand.setRotation(hourDegree);
            mMinuteHand.setRotation(minuteDegree);

            updateElapsedTimeWithMillisIfNeeded(millis);
        }, PERIOD_MS);
    }

    @MainThread
    void destroy() {
        mPeriodicProcessor.destroy();
    }

    @MainThread
    void updateTime(Long lastUpdateTime) {
        mLastUpdateTime = lastUpdateTime;
        updateElapsedTimeWithMinutesIfNeeded(0);
    }

    private void updateElapsedTimeWithMillisIfNeeded(long currentMillis) {
        if (mLastUpdateTime != null) {
            long minutes = (currentMillis - mLastUpdateTime) / (60000);

            updateElapsedTimeWithMinutesIfNeeded(minutes);
        }
    }

    private void updateElapsedTimeWithMinutesIfNeeded(long minutesAgo) {
        if (mLastSetMinutesAgo == minutesAgo) {
            return;
        }
        mElapsedTimeFromUpdateView.setText(mElapsedTimeFromUpdateView.getContext().getString(
                R.string.minutes_ago, minutesAgo));
        mLastSetMinutesAgo = minutesAgo;
    }
}
