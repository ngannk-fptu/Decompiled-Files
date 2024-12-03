/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.serviceprovider;

import com.atlassian.oauth.serviceprovider.Clock;

public final class SystemClock
implements Clock {
    @Override
    public long timeInMilliseconds() {
        return System.currentTimeMillis();
    }
}

