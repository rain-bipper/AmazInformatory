package com.formalizationunit.amaz.informatory.host;

import android.app.Application;

import com.formalizationunit.amaz.informatory.host.logger.LoggerImpl;

import java.io.IOException;

public class App extends Application {
    {
        try {
            com.formalizationunit.amaz.informatory.common.logger.Logger.setLogger(
                   new LoggerImpl("/sdcard"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        com.formalizationunit.amaz.informatory.common.logger.Logger.destroy();
    }
}
