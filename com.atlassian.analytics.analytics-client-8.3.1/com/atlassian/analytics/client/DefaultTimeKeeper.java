/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client;

import com.atlassian.analytics.client.TimeKeeper;

public class DefaultTimeKeeper
implements TimeKeeper {
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}

