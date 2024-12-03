/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.util.List;
import java.util.Map;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObjectInfo;

public interface GenericKeyedObjectPoolMXBean<K> {
    public boolean getBlockWhenExhausted();

    public long getBorrowedCount();

    public long getCreatedCount();

    public String getCreationStackTrace();

    public long getDestroyedByBorrowValidationCount();

    public long getDestroyedByEvictorCount();

    public long getDestroyedCount();

    public boolean getFairness();

    public boolean getLifo();

    default public boolean getLogAbandoned() {
        return false;
    }

    public long getMaxBorrowWaitTimeMillis();

    public int getMaxIdlePerKey();

    public int getMaxTotal();

    public int getMaxTotalPerKey();

    public long getMaxWaitMillis();

    public long getMeanActiveTimeMillis();

    public long getMeanBorrowWaitTimeMillis();

    public long getMeanIdleTimeMillis();

    public long getMinEvictableIdleTimeMillis();

    public int getMinIdlePerKey();

    public int getNumActive();

    public Map<String, Integer> getNumActivePerKey();

    public int getNumIdle();

    public int getNumTestsPerEvictionRun();

    public int getNumWaiters();

    public Map<String, Integer> getNumWaitersByKey();

    default public boolean getRemoveAbandonedOnBorrow() {
        return false;
    }

    default public boolean getRemoveAbandonedOnMaintenance() {
        return false;
    }

    default public int getRemoveAbandonedTimeout() {
        return 0;
    }

    public long getReturnedCount();

    public boolean getTestOnBorrow();

    public boolean getTestOnCreate();

    public boolean getTestOnReturn();

    public boolean getTestWhileIdle();

    public long getTimeBetweenEvictionRunsMillis();

    default public boolean isAbandonedConfig() {
        return false;
    }

    public boolean isClosed();

    public Map<String, List<DefaultPooledObjectInfo>> listAllObjects();
}

