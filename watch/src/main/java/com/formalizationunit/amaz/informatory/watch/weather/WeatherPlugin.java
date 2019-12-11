package com.formalizationunit.amaz.informatory.watch.weather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.watch.App;
import com.formalizationunit.amaz.informatory.watch.R;
import com.formalizationunit.amaz.informatory.watch.CommunicatorHolder;

import clc.sliteplugin.flowboard.AbstractPlugin;
import clc.sliteplugin.flowboard.ISpringBoardHostStub;

public class WeatherPlugin extends AbstractPlugin {
    private static final String TAG = "<WeatherPlugin>";

    private WeatherWidget mWidget;
    private boolean mIsActive;

    //Much like a fragment, getView returns the content view of the page. You can set up your layout here
    @Override
    public View getView(Context context) {
        //App.ensureLoggerCreated();
        Logger.log(TAG, "WeatherPlugin.getView " + mWidget);
        return mWidget.getView(context);
    }

    //Return the icon for this page, used when the page is disabled in the app list. In this case, the launcher icon is used
    @Override
    public Bitmap getWidgetIcon(Context context) {
        Logger.log(TAG, "WeatherPlugin.getWidgetIcon");
        return ((BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
    }

    //Return the launcher intent for this page. This might be used for the launcher as well when the page is disabled?
    @Override
    public Intent getWidgetIntent() {
        Logger.log(TAG, "WeatherPlugin.getWidgetIntent");
        return new Intent();
    }

    //Return the title for this page, used when the page is disabled in the app list. In this case, the app name is used
    @Override
    public String getWidgetTitle(Context context) {
        Logger.log(TAG, "WeatherPlugin.getWidgetTitle");
        return context.getResources().getString(R.string.app_name);
    }

    //Called when the page is shown
    @Override
    public void onActive(Bundle bundle) {
        super.onActive(bundle);
        Logger.log(TAG, "WeatherPlugin.onActive: " + bundle + ". is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        setActive();
    }

    //Called when the page is loading and being bound to the host
    @Override
    public void onBindHost(ISpringBoardHostStub springBoardHostStub) {
        App.ensureLoggerCreated();
        Logger.log(TAG, "WeatherPlugin.onBindHost. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        Context context = springBoardHostStub.getHostWindow().getContext();
        CommunicatorHolder.ensureCreated(context);
        mWidget = new WeatherWidget(CommunicatorHolder.getCommunicator(),
                runnable -> springBoardHostStub.runTaskOnUI(WeatherPlugin.this, runnable));

        /*
        Logger.log("<plug>",
        "BOARD " + Build.BOARD +
        ", BOOTLOADER " + Build.BOOTLOADER +
        ", BRAND " + Build.BRAND +
        ", DEVICE " + Build.DEVICE +
        ", DISPLAY " + Build.DISPLAY +
        ", FINGERPRINT " + Build.FINGERPRINT +
        ", HARDWARE " + Build.HARDWARE +
        ", HOST " + Build.HOST +
        ", ID " + Build.ID +
        ", MANUFACTURER " + Build.MANUFACTURER +
        ", MODEL " + Build.MODEL +
        ", PRODUCT " + Build.PRODUCT +
        ", SERIAL " + Build.SERIAL +
        ", TAGS " + Build.TAGS +
        ", TYPE " + Build.TYPE +
        ", USER " + Build.USER);

         */
    }

    //Called when the page is destroyed completely (in app mode). Same as the onDestroy method of an activity
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.log(TAG, "WeatherPlugin.onBindHost. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        setInactive();
    }

    //Called when the page becomes inactive (the user has scrolled away)
    @Override
    public void onInactive(Bundle bundle) {
        super.onInactive(bundle);
        Logger.log(TAG, "WeatherPlugin.onInactive: " + bundle + ". is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        setInactive();
    }

    //Called when the page is paused (in app mode)
    @Override
    public void onPause() {
        super.onPause();
        Logger.log(TAG, "WeatherPlugin.onPause. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        setInactive();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.log(TAG, "WeatherPlugin.onResume. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        setActive();
    }

    //Not sure what this does, can't find it being used anywhere. Best leave it alone
    /*
    @Override
    public void onReceiveDataFromProvider(int paramInt, Bundle paramBundle) {
        super.onReceiveDataFromProvider(paramInt, paramBundle);
        Logger.log(TAG, "WeatherPlugin.onReceiveDataFromProvider paramInt " + paramInt + ", paramBundle " + paramBundle);
    }

     */

    @Override
    public void onStop() {
        super.onStop();
        Logger.log(TAG, "WeatherPlugin.onStop. is main thread " +
                (Looper.getMainLooper().getThread() == Thread.currentThread()));
        setInactive();
    }

    private void setActive() {
        if (mIsActive) {
            return;
        }

        mIsActive = true;
        mWidget.onActive();
    }

    private void setInactive() {
        if (!mIsActive) {
            return;
        }

        mIsActive = false;
        mWidget.onInactive();
    }
}
