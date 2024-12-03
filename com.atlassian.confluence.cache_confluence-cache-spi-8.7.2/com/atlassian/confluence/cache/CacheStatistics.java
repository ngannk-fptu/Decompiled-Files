/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cache;

@Deprecated
public interface CacheStatistics {
    public long getHitCount();

    public long getExpiredCount();

    public long getAccessCount();

    public long getMissCount();

    public int getHitPercent();

    public long getSize();

    public long getMaxSize();

    public String getName();

    public int getUsagePercent();

    public String getNiceName();

    public boolean hasContents();

    public String getFormattedSizeInMegabytes();

    public long getSizeInBytes();

    public boolean isNearCache();
}

