package com.formalizationunit.amaz.informatory.transport;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({Actions.SEND_WEATHER, Actions.REQUEST_WEATHER})
public @interface Actions {
    String SEND_WEATHER = "SEND_WEATHER";
    String REQUEST_WEATHER = "REQUEST_WEATHER";
}
