/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Metrics
 */
package com.atlassian.cache.impl.metrics;

import com.atlassian.util.profiling.Metrics;

public class CacheManagerMetricEmitter {
    private static final String CACHE_FLUSH_METRIC_NAME = "cacheManager.flushAll";

    public void emitCacheManagerFlushAll(String className) {
        try {
            Metrics.metric((String)CACHE_FLUSH_METRIC_NAME).withInvokerPluginKey().tag("className", className).withAnalytics().incrementCounter(Long.valueOf(1L));
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
    }
}

