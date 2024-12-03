/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.cache.ConfluenceMonitoringCache
 *  com.atlassian.confluence.util.profiling.ConfluenceMonitoring
 *  com.google.common.base.Preconditions
 *  net.sf.ehcache.config.CacheConfiguration
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cache.ConfluenceMonitoringCache;
import com.atlassian.confluence.cache.ehcache.ConfluenceEhCache;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.google.common.base.Preconditions;
import net.sf.ehcache.config.CacheConfiguration;

@Deprecated
@Internal
class MonitoringConfluenceEhCache<K, V>
extends ConfluenceMonitoringCache<K, V>
implements ConfluenceEhCache<K, V> {
    private final ConfluenceEhCache<K, V> ehCache;

    MonitoringConfluenceEhCache(ConfluenceEhCache<K, V> ehCache, ConfluenceMonitoring confluenceMonitoring) {
        super(ehCache, confluenceMonitoring);
        this.ehCache = (ConfluenceEhCache)Preconditions.checkNotNull(ehCache);
    }

    @Override
    public boolean updateMaxEntriesLocalHeap(long max) {
        return this.ehCache.updateMaxEntriesLocalHeap(max);
    }

    @Override
    public CacheConfiguration getEhCacheConfiguration() {
        return this.ehCache.getEhCacheConfiguration();
    }
}

