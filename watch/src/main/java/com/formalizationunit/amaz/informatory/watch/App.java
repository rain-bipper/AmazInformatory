package com.formalizationunit.amaz.informatory.watch;

import android.app.Application;

import com.formalizationunit.amaz.informatory.common.logger.Logger;

public class App extends Application {

    {
        ensureLoggerCreated();
    }

    public static void ensureLoggerCreated() {
        if (Logger.inited()) {
            return;
        }

        Logger.setLogger(new LoggerImpl());
    }
}
