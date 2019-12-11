package com.formalizationunit.amaz.informatory.host.logger;

import androidx.annotation.Nullable;

import com.formalizationunit.amaz.informatory.host.weather.BuildConfig;
import com.formalizationunit.amaz.informatory.common.logger.LoggerInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerImpl implements LoggerInterface {
    @Nullable
    private final FileOutputStream mFile;
    private final DateFormat mDateFormat = SimpleDateFormat.getDateTimeInstance();

    public LoggerImpl(String path) throws IOException {
        if (BuildConfig.DEBUG) {
            File file = new File(path + "/" + "amazinformatory.log");
            if (file.createNewFile()) {
                mFile = new FileOutputStream(file, true);
            } else {
                mFile = null;
            }
        } else {
            mFile = null;
        }
    }

    public void destroy() {
        if (mFile == null) {
            return;
        }

        try {
            mFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(String tag, String data) {
        android.util.Log.e(tag, data);

        if (mFile == null) {
            return;
        }

        try {
            mFile.write((mDateFormat.format(new Date()) + " [" + tag + "] " + data + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
