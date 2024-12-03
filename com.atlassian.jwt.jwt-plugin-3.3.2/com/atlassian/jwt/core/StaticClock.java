/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.core.Clock;
import java.util.Date;

public class StaticClock
implements Clock {
    private final Date now;

    public StaticClock(long nowMs) {
        this.now = new Date(nowMs);
    }

    public static StaticClock at(Date nowDate) {
        return new StaticClock(nowDate.getTime());
    }

    @Override
    public Date now() {
        return this.now;
    }
}

