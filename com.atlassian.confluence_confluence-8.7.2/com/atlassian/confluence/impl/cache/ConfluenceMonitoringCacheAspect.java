/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.ConfluenceCache
 *  org.aspectj.lang.annotation.Aspect
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.confluence.cache.ConfluenceCache;
import com.atlassian.confluence.cache.ConfluenceMonitoringCache;
import com.atlassian.confluence.impl.cache.AbstractConfluenceCacheAspect;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import java.util.Objects;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public final class ConfluenceMonitoringCacheAspect
extends AbstractConfluenceCacheAspect {
    private final ConfluenceMonitoring monitoring;
    private boolean enabled = Boolean.getBoolean("cache.monitoring.enabled");

    ConfluenceMonitoringCacheAspect(ConfluenceMonitoring monitoring) {
        this.monitoring = Objects.requireNonNull(monitoring);
    }

    @Override
    protected <K, V> ConfluenceCache<K, V> wrapCache(ConfluenceCache<K, V> cache) {
        return new ConfluenceMonitoringCache<K, V>(cache, this.monitoring);
    }

    @Override
    protected boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

