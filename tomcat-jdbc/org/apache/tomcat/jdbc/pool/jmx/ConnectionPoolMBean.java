/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool.jmx;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;

public interface ConnectionPoolMBean
extends PoolConfiguration {
    public int getSize();

    public int getIdle();

    public int getActive();

    public int getNumIdle();

    public int getNumActive();

    public int getWaitCount();

    public long getBorrowedCount();

    public long getReturnedCount();

    public long getCreatedCount();

    public long getReleasedCount();

    public long getReconnectedCount();

    public long getRemoveAbandonedCount();

    public long getReleasedIdleCount();

    public void checkIdle();

    public void checkAbandoned();

    public void testIdle();

    public void purge();

    public void purgeOnReturn();

    public void resetStats();
}

