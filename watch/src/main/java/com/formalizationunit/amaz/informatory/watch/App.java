package com.formalizationunit.amaz.informatory.watch;

import android.app.Application;

import com.formalizationunit.amaz.informatory.common.BuildConfig;
import com.formalizationunit.amaz.informatory.common.communicators.CommunicatorFactory;
import com.formalizationunit.amaz.informatory.common.logger.Logger;

public class App extends Application {

    {
        ensureLoggerCreated();

        if (BuildConfig.DEBUG) {
            CommunicatorFactory.setPreferLocalConnectionOnHost(true);
        }
    }

    public static void ensureLoggerCreated() {
        if (Logger.inited()) {
            return;
        }

        Logger.setLogger(new LoggerImpl());
    }
}
