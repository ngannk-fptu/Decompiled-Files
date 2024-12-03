/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.cache.spi.ExtendedStatisticsSupport
 *  org.hibernate.cache.spi.Region
 *  org.hibernate.cache.spi.access.SoftLock
 */
package com.hazelcast.hibernate;

import org.hibernate.cache.spi.ExtendedStatisticsSupport;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.access.SoftLock;

public interface RegionCache
extends Region,
ExtendedStatisticsSupport {
    public void afterUpdate(Object var1, Object var2, Object var3);

    default public void clear() {
        this.evictData();
    }

    public boolean contains(Object var1);

    default public void destroy() {
    }

    public void evictData();

    public void evictData(Object var1);

    public Object get(Object var1, long var2);

    default public long getElementCountOnDisk() {
        return 0L;
    }

    default public long nextTimestamp() {
        return this.getRegionFactory().nextTimestamp();
    }

    public boolean put(Object var1, Object var2, long var3, Object var5);

    public void unlockItem(Object var1, SoftLock var2);
}

