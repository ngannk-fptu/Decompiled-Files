/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import net.sf.ehcache.management.sampled.CacheSampler;

public interface SampledCacheMBean
extends CacheSampler {
    public static final String CACHE_ENABLED = "CacheEnabled";
    public static final String CACHE_CHANGED = "CacheChanged";
    public static final String CACHE_FLUSHED = "CacheFlushed";
    public static final String CACHE_CLEARED = "CacheCleared";
    public static final String CACHE_STATISTICS_ENABLED = "CacheStatisticsEnabled";
    public static final String CACHE_STATISTICS_RESET = "CacheStatisticsReset";

    @Deprecated
    public void setNodeCoherent(boolean var1);

    @Deprecated
    public boolean isClusterCoherent();

    @Deprecated
    public boolean isNodeCoherent();

    @Deprecated
    public int getMaxElementsInMemory();

    @Deprecated
    public void setMaxElementsInMemory(int var1);
}

