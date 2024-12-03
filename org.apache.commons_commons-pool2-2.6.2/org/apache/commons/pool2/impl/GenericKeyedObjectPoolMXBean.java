/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.util.List;
import java.util.Map;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;

public interface GenericKeyedObjectPoolMXBean<K> {
    public boolean getBlockWhenExhausted();

    public boolean getFairness();

    public boolean getLifo();

    public int getMaxIdlePerKey();

    public int getMaxTotal();

    public int getMaxTotalPerKey();

    public long getMaxWaitMillis();

    public long getMinEvictableIdleTimeMillis();

    public int getMinIdlePerKey();

    public int getNumActive();

    public int getNumIdle();

    public int getNumTestsPerEvictionRun();

    public boolean getTestOnCreate();

    public boolean getTestOnBorrow();

    public boolean getTestOnReturn();

    public boolean getTestWhileIdle();

    public long getTimeBetweenEvictionRunsMillis();

    public boolean isClosed();

    public Map<String, Integer> getNumActivePerKey();

    public long getBorrowedCount();

    public long getReturnedCount();

    public long getCreatedCount();

    public long getDestroyedCount();

    public long getDestroyedByEvictorCount();

    public long getDestroyedByBorrowValidationCount();

    public long getMeanActiveTimeMillis();

    public long getMeanIdleTimeMillis();

    public long getMeanBorrowWaitTimeMillis();

    public long getMaxBorrowWaitTimeMillis();

    public String getCreationStackTrace();

    public int getNumWaiters();

    public Map<String, Integer> getNumWaitersByKey();

    public Map<String, List<DefaultPooledObjectInfo>> listAllObjects();
}

