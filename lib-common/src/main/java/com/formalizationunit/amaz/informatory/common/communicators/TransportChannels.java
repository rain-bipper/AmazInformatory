package com.formalizationunit.amaz.informatory.common.communicators;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({TransportChannels.MAIN_CHANNEL})
@interface TransportChannels {
    String MAIN_CHANNEL = "com.formalizationunit.amaz.informatory.transport.MAIN_CHANNEL";
}
