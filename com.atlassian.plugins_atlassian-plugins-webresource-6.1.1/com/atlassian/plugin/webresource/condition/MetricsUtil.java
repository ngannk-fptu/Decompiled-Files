/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.condition;

import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import javax.annotation.Nonnull;

public final class MetricsUtil {
    private static final String METRIC_KEY = "web.resource.condition";
    private static final String CONDITION_CLASS_NAME = "conditionClassName";

    private MetricsUtil() {
    }

    public static Ticker startWebConditionProfilingTimer(@Nonnull String pluginKey, @Nonnull String conditionClassName) {
        return Metrics.metric((String)METRIC_KEY).fromPluginKey(pluginKey).tag(CONDITION_CLASS_NAME, conditionClassName).withAnalytics().startTimer();
    }
}

