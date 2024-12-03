/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Instrument;

public class InstrumentToStringBuilder {
    public static String toString(Instrument instrument) {
        return instrument.getName() + ":" + instrument.getValue();
    }
}

