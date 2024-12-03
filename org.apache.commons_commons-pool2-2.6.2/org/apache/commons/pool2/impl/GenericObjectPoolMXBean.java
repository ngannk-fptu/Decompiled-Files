/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.util.Set;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;

public interface GenericObjectPoolMXBean {
    public boolean getBlockWhenExhausted();

    public boolean getFairness();

    public boolean getLifo();

    public int getMaxIdle();

    public int getMaxTotal();

    public long getMaxWaitMillis();

    public long getMinEvictableIdleTimeMillis();

    public int getMinIdle();

    public int getNumActive();

    public int getNumIdle();

    public int getNumTestsPerEvictionRun();

    public boolean getTestOnCreate();

    public boolean getTestOnBorrow();

    public boolean getTestOnReturn();

    public boolean getTestWhileIdle();

    public long getTimeBetweenEvictionRunsMillis();

    public boolean isClosed();

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

    public boolean isAbandonedConfig();

    public boolean getLogAbandoned();

    public boolean getRemoveAbandonedOnBorrow();

    public boolean getRemoveAbandonedOnMaintenance();

    public int getRemoveAbandonedTimeout();

    public String getFactoryType();

    public Set<DefaultPooledObjectInfo> listAllObjects();
}

