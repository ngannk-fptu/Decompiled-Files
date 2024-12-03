/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.confluence.cache.CacheMonitoringUtils
 *  com.atlassian.confluence.util.profiling.ConfluenceMonitoring
 *  com.atlassian.confluence.util.profiling.Split
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cache.hazelcast.monitoring;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheLoader;
import com.atlassian.confluence.cache.CacheMonitoringUtils;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(forRemoval=true)
@Internal
public class ConfluenceMonitoringLoader<K, V>
implements CacheLoader<K, V> {
    private final ConfluenceMonitoring confluenceMonitoring;
    private final String cacheName;
    private final CacheLoader<K, V> cacheLoader;

    public ConfluenceMonitoringLoader(ConfluenceMonitoring confluenceMonitoring, String cacheName, CacheLoader<K, V> cacheLoader) {
        this.confluenceMonitoring = (ConfluenceMonitoring)Preconditions.checkNotNull((Object)confluenceMonitoring);
        this.cacheName = (String)Preconditions.checkNotNull((Object)cacheName);
        this.cacheLoader = (CacheLoader)Preconditions.checkNotNull(cacheLoader);
    }

    public @NonNull V load(@NonNull K key) {
        try (Split ignored = this.createLoadSplit();){
            Object object = this.cacheLoader.load(key);
            return (V)object;
        }
    }

    protected Split createLoadSplit() {
        return CacheMonitoringUtils.startSplit((ConfluenceMonitoring)this.confluenceMonitoring, (String)"CACHE", (Map)ImmutableMap.of((Object)"cacheName", (Object)this.cacheName, (Object)"cacheOperation", (Object)"LOAD"));
    }
}

