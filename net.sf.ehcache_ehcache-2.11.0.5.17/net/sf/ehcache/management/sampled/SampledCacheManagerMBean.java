/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import net.sf.ehcache.management.sampled.CacheManagerSampler;

public interface SampledCacheManagerMBean
extends CacheManagerSampler {
    public static final String CACHE_MANAGER_CHANGED = "CacheManagerChanged";
    public static final String CACHES_ENABLED = "CachesEnabled";
    public static final String CACHES_CLEARED = "CachesCleared";
    public static final String STATISTICS_RESET = "StatisticsReset";
    public static final String STATISTICS_ENABLED = "StatisticsEnabled";

    public String getMBeanRegisteredName();
}

