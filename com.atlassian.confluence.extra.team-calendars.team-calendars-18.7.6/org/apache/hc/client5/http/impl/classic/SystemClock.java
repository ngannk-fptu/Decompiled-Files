/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.classic;

import org.apache.hc.client5.http.impl.classic.Clock;

class SystemClock
implements Clock {
    SystemClock() {
    }

    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}

