package com.formalizationunit.amaz.informatory.watch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.watch.weather.WeatherWidget;

public class MainActivity extends Activity {
    private static final String TAG = "<MainActivity>";
    private WeatherWidget mWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(TAG, "MainActivity.onCreate");

        Handler handler = new Handler(Looper.getMainLooper());
        CommunicatorHolder.ensureCreated(this);
        mWidget = new WeatherWidget(CommunicatorHolder.getCommunicator(), handler::post);
        setContentView(mWidget.getView(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.log(TAG, "MainActivity.onPause");
        mWidget.onInactive();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.log(TAG, "MainActivity.onResume");
        mWidget.onActive();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.log(TAG, "MainActivity.onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.log(TAG, "MainActivity.onStop");
    }
}
