/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics;

import com.atlassian.upm.core.analytics.AnalyticsEvent;
import com.atlassian.upm.core.analytics.AnalyticsPublisher;

public interface AnalyticsLogger {
    public void register(AnalyticsPublisher var1);

    public void unregister(AnalyticsPublisher var1);

    public void log(AnalyticsEvent var1);
}

