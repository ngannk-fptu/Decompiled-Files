/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics;

public interface CacheUsageListener {
    public void notifyCacheHitInMemory();

    public void notifyCacheHitOffHeap();

    public void notifyCacheHitOnDisk();

    public void notifyCacheElementPut();

    public void notifyCacheElementUpdated();

    public void notifyCacheMissedWithNotFound();

    public void notifyCacheMissInMemory();

    public void notifyCacheMissOffHeap();

    public void notifyCacheMissOnDisk();

    public void notifyCacheMissedWithExpired();

    @Deprecated
    public void notifyTimeTakenForGet(long var1);

    public void notifyGetTimeNanos(long var1);

    public void notifyCacheElementEvicted();

    public void notifyCacheElementExpired();

    public void notifyCacheElementRemoved();

    public void notifyRemoveAll();

    public void notifyStatisticsAccuracyChanged(int var1);

    public void dispose();

    public void notifyCacheSearch(long var1);

    public void notifyXaCommit();

    public void notifyXaRollback();
}

