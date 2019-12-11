package com.formalizationunit.amaz.informatory.common.models;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

public interface CommunicatorActionHandler {
    @WorkerThread
    void onAction(@Nullable String data);
}
