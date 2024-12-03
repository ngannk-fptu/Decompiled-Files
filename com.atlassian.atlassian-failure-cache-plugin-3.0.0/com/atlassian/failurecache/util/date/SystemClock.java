/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache.util.date;

import com.atlassian.failurecache.util.date.Clock;
import java.util.Date;

public class SystemClock
implements Clock {
    private static final SystemClock INSTANCE = new SystemClock();

    @Override
    public Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    public static SystemClock getInstance() {
        return INSTANCE;
    }
}

