/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import java.util.Collections;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public class CacheMonitoringUtils {
    private static final boolean cacheMonitoringEnabled = Boolean.getBoolean("cache.monitoring.enabled");
    private static final Split NOOP_SPLIT = new Split(){

        @Override
        public @NonNull Split stop() {
            return this;
        }
    };

    @Deprecated
    public static Split startSplit(ConfluenceMonitoring monitoring, String name, String ... optional) {
        if (cacheMonitoringEnabled) {
            return monitoring.startSplit(name, optional);
        }
        return NOOP_SPLIT;
    }

    public static Split startSplit(ConfluenceMonitoring monitoring, String name) {
        if (cacheMonitoringEnabled) {
            return monitoring.startSplit(name, Collections.emptyMap());
        }
        return NOOP_SPLIT;
    }

    public static Split startSplit(ConfluenceMonitoring monitoring, String name, Map<String, String> tags) {
        if (cacheMonitoringEnabled) {
            return monitoring.startSplit(name, tags);
        }
        return NOOP_SPLIT;
    }
}

