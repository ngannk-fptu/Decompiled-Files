/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Metrics
 */
package com.atlassian.plugin.manager;

import com.atlassian.util.profiling.Metrics;

public final class PluginStateChangeCountEmitter {
    private static final String PLUGIN_ENABLED_METRIC_KEY = "plugin.enabled.counter";
    private static final String PLUGIN_DISABLED_METRIC_KEY = "plugin.disabled.counter";

    private PluginStateChangeCountEmitter() {
    }

    public static void emitPluginEnabledCounter() {
        PluginStateChangeCountEmitter.emitPluginCounter(PLUGIN_ENABLED_METRIC_KEY);
    }

    public static void emitPluginDisabledCounter() {
        PluginStateChangeCountEmitter.emitPluginCounter(PLUGIN_DISABLED_METRIC_KEY);
    }

    private static void emitPluginCounter(String metricKey) {
        Metrics.metric((String)metricKey).withAnalytics().incrementCounter(Long.valueOf(1L));
    }
}

