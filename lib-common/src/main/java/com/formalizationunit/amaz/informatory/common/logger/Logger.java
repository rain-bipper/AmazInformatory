package com.formalizationunit.amaz.informatory.common.logger;

import androidx.annotation.Nullable;

public class Logger {
    @Nullable
    private static LoggerInterface mLogger;

    public static void setLogger(LoggerInterface logger) {
        mLogger = logger;
        com.formalizationunit.amaz.informatory.transport.logger.Logger.setLogger(Logger::log);
    }

    static public void log(String tag, String data) {
        if (mLogger != null) {
            mLogger.log(tag, data);
        }
    }

    static public void destroy() {
        if (mLogger != null) {
            mLogger.destroy();
        }
    }

    static public boolean inited() {
        return mLogger != null;
    }
}
