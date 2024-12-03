/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.pipeline.preprocessor.filter;

import com.atlassian.analytics.client.pipeline.preprocessor.filter.AnalyticsFilter;
import com.atlassian.analytics.event.RawEvent;
import java.util.Map;

public class NoOpAnalyticFilter
implements AnalyticsFilter {
    @Override
    public Map<String, Object> filter(RawEvent event) {
        return event.getProperties();
    }

    @Override
    public boolean canCollect(RawEvent event) {
        return true;
    }
}

