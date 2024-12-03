/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.event.logging;

import com.atlassian.analytics.event.AnalyticsEvent;
import java.io.IOException;

public interface LogEventFormatter {
    public String formatEventLocal(AnalyticsEvent var1) throws IOException;

    public String formatEvent(AnalyticsEvent var1) throws IOException;
}

