/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

public interface CacheStatisticsMBean {
    public long getCacheHits();

    public long getInMemoryHits();

    public long getOffHeapHits();

    public long getOnDiskHits();

    public long getCacheMisses();

    public long getInMemoryMisses();

    public long getOffHeapMisses();

    public long getOnDiskMisses();

    public long getObjectCount();

    public long getMemoryStoreObjectCount();

    public long getOffHeapStoreObjectCount();

    public long getDiskStoreObjectCount();

    public String getAssociatedCacheName();

    public double getCacheHitPercentage();

    public double getCacheMissPercentage();

    public double getInMemoryHitPercentage();

    public double getOffHeapHitPercentage();

    public double getOnDiskHitPercentage();

    public long getWriterQueueLength();

    public int getWriterMaxQueueSize();
}

