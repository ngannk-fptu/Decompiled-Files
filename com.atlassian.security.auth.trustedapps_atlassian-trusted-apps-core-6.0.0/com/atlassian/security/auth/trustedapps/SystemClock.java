/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Clock;

public class SystemClock
implements Clock {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}

