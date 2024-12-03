/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.util.Set;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObjectInfo;

public interface GenericObjectPoolMXBean {
    public boolean getBlockWhenExhausted();

    public long getBorrowedCount();

    public long getCreatedCount();

    public String getCreationStackTrace();

    public long getDestroyedByBorrowValidationCount();

    public long getDestroyedByEvictorCount();

    public long getDestroyedCount();

    public String getFactoryType();

    public boolean getFairness();

    public boolean getLifo();

    public boolean getLogAbandoned();

    public long getMaxBorrowWaitTimeMillis();

    public int getMaxIdle();

    public int getMaxTotal();

    public long getMaxWaitMillis();

    public long getMeanActiveTimeMillis();

    public long getMeanBorrowWaitTimeMillis();

    public long getMeanIdleTimeMillis();

    public long getMinEvictableIdleTimeMillis();

    public int getMinIdle();

    public int getNumActive();

    public int getNumIdle();

    public int getNumTestsPerEvictionRun();

    public int getNumWaiters();

    public boolean getRemoveAbandonedOnBorrow();

    public boolean getRemoveAbandonedOnMaintenance();

    public int getRemoveAbandonedTimeout();

    public long getReturnedCount();

    public boolean getTestOnBorrow();

    public boolean getTestOnCreate();

    public boolean getTestOnReturn();

    public boolean getTestWhileIdle();

    public long getTimeBetweenEvictionRunsMillis();

    public boolean isAbandonedConfig();

    public boolean isClosed();

    public Set<DefaultPooledObjectInfo> listAllObjects();
}

