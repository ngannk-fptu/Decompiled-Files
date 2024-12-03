/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.preprocessor.filter;

import com.atlassian.analytics.event.RawEvent;
import java.util.Map;

public interface AnalyticsFilter {
    public Map<String, Object> filter(RawEvent var1);

    public boolean canCollect(RawEvent var1);
}

