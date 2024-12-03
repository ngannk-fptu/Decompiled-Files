/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.spi;

import java.util.Locale;

public interface StreamsLocaleProvider {
    public Locale getApplicationLocale();

    public Locale getUserLocale();
}

