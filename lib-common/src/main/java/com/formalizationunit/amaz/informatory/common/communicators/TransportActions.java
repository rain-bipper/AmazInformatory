package com.formalizationunit.amaz.informatory.common.communicators;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({TransportActions.SEND_WEATHER, TransportActions.REQUEST_WEATHER})
public @interface TransportActions {
    String SEND_WEATHER = "SEND_WEATHER";
    String REQUEST_WEATHER = "REQUEST_WEATHER";
}
