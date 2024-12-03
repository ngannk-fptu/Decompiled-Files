/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.spi.StreamsLocaleProvider;
import java.util.Locale;

public class DefaultStreamsLocaleProvider
implements StreamsLocaleProvider {
    @Override
    public Locale getApplicationLocale() {
        return Locale.getDefault();
    }

    @Override
    public Locale getUserLocale() {
        return Locale.getDefault();
    }
}

