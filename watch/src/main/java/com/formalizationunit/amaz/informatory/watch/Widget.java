package com.formalizationunit.amaz.informatory.watch;

import android.content.Context;
import android.view.View;

public interface Widget {
    void onActive();
    void onInactive();
    View getView(Context paramContext);
}
