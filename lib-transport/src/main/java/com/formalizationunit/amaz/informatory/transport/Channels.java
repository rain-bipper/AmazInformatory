package com.formalizationunit.amaz.informatory.transport;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({Channels.MAIN_CHANNEL})
public @interface Channels {
    String MAIN_CHANNEL = "com.formalizationunit.amaz.informatory.transport.MAIN_CHANNEL";
}
