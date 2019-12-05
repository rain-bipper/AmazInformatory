package com.formalizationunit.amaz.informatory.transport.logger;

public class Logger {
    private static LoggerInterface mLogger;

    public static void setLogger(LoggerInterface logger) {
        mLogger = logger;
    }

    static public void log(String tag, String data) {
        mLogger.log(tag, data);
    }
}
