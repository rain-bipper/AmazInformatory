package com.formalizationunit.amaz.informatory.watch.weather;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.common.models.CommunicatorModel;
import com.formalizationunit.amaz.informatory.common.models.WeatherModel;
import com.formalizationunit.amaz.informatory.common.util.Device;
import com.formalizationunit.amaz.informatory.watch.App;
import com.formalizationunit.amaz.informatory.watch.R;
import com.formalizationunit.amaz.informatory.common.util.UiRunner;
import com.formalizationunit.amaz.informatory.watch.Widget;
import com.formalizationunit.amaz.informatory.watch.system.SystemSettings;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.min;
import static java.lang.Math.sin;

public class WeatherWidget implements Widget {
    private static final String TAG = "<WeatherWidget>";
    private static final int HOURS_COUNT = 12;
    private static final double QUARTER = 2 * PI / 4;
    private static final double DOZEN = 2 * PI / 12.0f;

    private static final long  MIN_UPDATE_TIME_MS = 15 * 60 * 1000;  // 15 minutes.
    private static final int UPDATE_DELAY_MS = 3 * 1000;  // 3 seconds.

    private final Context mContext;
    private final UiRunner mUiRunner;
    private final CommunicatorModel mCommunicator;

    private PeriodicWeatherVisualizer mPeriodicWeatherVisualizer;
    private View mView;
    private WeatherModel mPendingData;
    private long mLastUpdateRequestTime;
    private Handler mDelayedTaskHandler = new Handler();
    private boolean mActive;
    private Long mLastUpdateTime;

    public WeatherWidget(Context context, CommunicatorModel communicator, UiRunner uiRunner) {
        mContext = context;
        mCommunicator = communicator;
        mUiRunner = uiRunner;

        App.ensureLoggerCreated();
        communicator.registerSentWeatherHandler(this::onReceiveWeather);
    }


    @Override
    public View getView(Context context) {
        Logger.log(TAG, "WeatherWidget.getView " + mView);

        if (mView != null) {
            return mView;
        }

        mView = LayoutInflater.from(context).inflate(R.layout.weather, null);
        FrameLayout container = mView.findViewById(R.id.container);
        /*
        container.setOnLongClickListener(view -> {
            Toast.makeText(context, "Update requested", Toast.LENGTH_SHORT).show();
            requestUpdate();
            return true;
        });
         */

        container.setOnTouchListener(new View.OnTouchListener() {
            private Long mDownTimeStamp;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Logger.log(TAG, "onTouch event " + motionEvent);

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownTimeStamp = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (mDownTimeStamp != null && System.currentTimeMillis() -
                                mDownTimeStamp >= ViewConfiguration.getLongPressTimeout()) {
                            Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();
                            requestUpdate();
                        }
                        break;

                    default:
                        mDownTimeStamp = null;
                }
                return true;
            }
        });

        float hourTextSize = context.getResources().getDimensionPixelSize(R.dimen.hour_text_size);
        int iconSize = context.getResources().getDimensionPixelSize(R.dimen.icon_size) + 1;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int radius = min(size.x - iconSize * 2, size.y - iconSize * 2) / 2;

        for (int i = 0; i < HOURS_COUNT; ++i) {
            int hourName = (i + HOURS_COUNT / 4 - 1) % HOURS_COUNT + 1;

            TextView hourView = new TextView(context);
            hourView.setText(String.valueOf(hourName));
            hourView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hourTextSize);
            hourView.setTag("hour" + (hourName % 12));
            hourView.setTextColor(0xffffffff);
            setViewPos(i, radius * 70 / 100, true, container, hourView);


            View temperatureView = LayoutInflater.from(context).inflate(
                    R.layout.weather_item, null);
            temperatureView.setTag("data" + (hourName % 12));
            setViewPos(i, radius, false, container, temperatureView);
        }
        activate();
        return mView;
    }

    @Override
    public void onActive() {
        mActive = true;

        Logger.log(TAG, "WeatherWidget.onActive. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        if (mView != null) {
            activate();
        }
    }

    @Override
    public void onInactive() {
        mActive = false;

        Logger.log(TAG, "WeatherWidget.onStop. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        if (mPeriodicWeatherVisualizer != null) {
            mPeriodicWeatherVisualizer.destroy();
            mPeriodicWeatherVisualizer = null;
        }

        mDelayedTaskHandler.removeCallbacksAndMessages(null);
    }

    private void activate() {
        if (mPeriodicWeatherVisualizer == null) {
            mPeriodicWeatherVisualizer = new PeriodicWeatherVisualizer(mUiRunner, mLastUpdateTime,
                    mView.findViewById(R.id.hour_hand),
                    mView.findViewById(R.id.minute_hand),
                    mView.findViewById(R.id.time));
        }

        if (mPendingData != null) {
            WeatherVisualizer.show(mPendingData, mView);
            mPendingData = null;
            mView.invalidate();
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastUpdateRequestTime > MIN_UPDATE_TIME_MS) {
            mDelayedTaskHandler.postDelayed(this::requestUpdate, UPDATE_DELAY_MS);
        }
    }

    private void requestUpdate() {
        mLastUpdateRequestTime = System.currentTimeMillis();
        mCommunicator.requestWeather(() -> {});
    }

    private void setViewPos(int i, double radius, boolean rotate, ViewGroup container, View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        double rotation = DOZEN * i;
        double x = radius * cos(rotation);
        double y = radius * sin(rotation);

        if (rotate) {
            /*
            if (i > 0 && i < 6) {
                rotation += (QUARTER * 2);
            }
             */
            view.setRotation((float) ((rotation + QUARTER) * 180 / PI));
        }

        params.setMargins((int) x, (int) y, 0, 0);
        container.addView(view, params);
    }


    @WorkerThread
    private void onReceiveWeather(@Nullable String jsonData) {
        Logger.log(TAG, "WeatherWidget.onReceiveWeather. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));

        if (jsonData == null) {
            return;
        }

        WeatherModel weather = WeatherModel.fromJson(jsonData);

        if (weather == null) {
            return;
        }

        mLastUpdateTime = System.currentTimeMillis();

        if (Device.isWatch()) {
            SystemSettings.set(mContext, "WeatherInfo", jsonData);
        }

        if (!mActive) {
            mPendingData = weather;
            return;
        }

        mUiRunner.runOnUi(() -> {
            Logger.log(TAG, "WeatherWidget.onReceiveWeather on UI");

            if (!mActive) {
                mPendingData = weather;
                return;
            }

            WeatherVisualizer.show(weather, mView);
            mPeriodicWeatherVisualizer.updateTime(mLastUpdateTime);
            mView.invalidate();
        });
    }
}
