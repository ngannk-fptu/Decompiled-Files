/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.util;

import java.util.concurrent.TimeUnit;

public class TimeSpan {
    private final long time;
    private final TimeUnit units;

    public TimeSpan(long time, TimeUnit units) {
        this.time = time;
        this.units = units;
    }

    public long getTime() {
        return this.time;
    }

    public TimeUnit getUnits() {
        return this.units;
    }

    public long toMillis() {
        return this.units.toMillis(this.time);
    }
}

