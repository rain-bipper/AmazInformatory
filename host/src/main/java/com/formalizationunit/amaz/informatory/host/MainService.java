package com.formalizationunit.amaz.informatory.host;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.common.logger.Logger;
import com.formalizationunit.amaz.informatory.host.weather.ApiKeyProvider;

public class MainService extends Service {
    private static final String TAG = "<MainService>";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "main";

    private Facade mFacade;

    Facade.Callback mProcessCallback = new Facade.Callback() {
        @Override
        public void onDone() {
            Logger.log(TAG, "done");
        }

        @Override
        public void onError(String reason) {
            Logger.log(TAG, "error: " + reason);
        }
    };

    static public void start(Context context) {
        Intent intent = new Intent(context, MainService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification());
        }

        // The user must get its own API keys for weather and geocoding providers. For current
        // implementations of the providers, API keys one can get:
        // DarkSky weather provider:
        //   1. register at https://darksky.net/dev
        //   2. get API key at https://darksky.net/account
        //
        // Yandex geocoder:
        //   1. register at https://yandex.com
        //   2. get API key at https://developer.tech.yandex.com/ , select HTTP geocoder.
        mFacade = new Facade(this, CommunicatorFactory.create(this), new ApiKeyProvider() {
            @Override
            public String weatherApiKey() {
                return <enter your key here>;
            }

            @Override
            public String geocoderApiKey() {
                return <enter your key here>;
            }
        }, mProcessCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFacade.destroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.log(TAG, "manual process");

        mFacade.process(mProcessCallback);

        return START_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.O)
    Notification createNotification() {
        String name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        return new Notification.Builder(this, CHANNEL_ID).build();
    }
}
