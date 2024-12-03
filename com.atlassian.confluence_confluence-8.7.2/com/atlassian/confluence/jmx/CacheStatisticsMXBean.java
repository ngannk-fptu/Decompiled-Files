/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.CacheStatistics
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.cache.CacheStatistics;
import java.util.Map;

public interface CacheStatisticsMXBean {
    public CacheStatistics[] getCacheStatisticsAsArray();

    public Map<String, CacheStatistics> getCacheStatisticsAsMap();
}

