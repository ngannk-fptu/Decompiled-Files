/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.util.PluginKeyStack
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Metrics$Builder
 */
package com.atlassian.cache.impl.metrics;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.util.PluginKeyStack;
import com.atlassian.util.profiling.Metrics;

public class MetricEmitter {
    public static final String CLASS_NAME_KEY = "className";
    private static final String CACHE_REMOVE_ALL_METRIC_NAME = "cache.removeAll";
    private static final String CACHED_REFERENCE_RESET_METRIC_NAME = "cachedReference.reset";
    private static final String PLUGIN_KEY_AT_CREATION_TAG_KEY = "pluginKeyAtCreation";
    private final String creatorKey;
    private final String invokingClassName;

    private MetricEmitter(String creatorKey, String invokingClassName) {
        this.creatorKey = creatorKey;
        this.invokingClassName = invokingClassName;
    }

    public static MetricEmitter create(String invokingClassName) {
        return new MetricEmitter(MetricEmitter.getCreatorKey(), invokingClassName);
    }

    public void emitCachedReferenceReset() {
        try {
            this.tagWithCreatorKeyIfExists(Metrics.metric((String)CACHED_REFERENCE_RESET_METRIC_NAME)).tag(CLASS_NAME_KEY, this.invokingClassName).withAnalytics().incrementCounter(Long.valueOf(1L));
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
    }

    public void emitCacheRemoveAll() {
        try {
            this.tagWithCreatorKeyIfExists(Metrics.metric((String)CACHE_REMOVE_ALL_METRIC_NAME)).tag(CLASS_NAME_KEY, this.invokingClassName).withAnalytics().incrementCounter(Long.valueOf(1L));
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            // empty catch block
        }
    }

    private static String getCreatorKey() {
        try {
            return PluginKeyStack.getFirstPluginKey();
        }
        catch (NoClassDefFoundError ignored) {
            return null;
        }
    }

    private Metrics.Builder tagWithCreatorKeyIfExists(Metrics.Builder builder) {
        if (this.creatorKey == null) {
            return builder;
        }
        return builder.tag(PLUGIN_KEY_AT_CREATION_TAG_KEY, this.creatorKey);
    }

    @VisibleForTesting
    String getInvokingClassName() {
        return this.invokingClassName;
    }
}

