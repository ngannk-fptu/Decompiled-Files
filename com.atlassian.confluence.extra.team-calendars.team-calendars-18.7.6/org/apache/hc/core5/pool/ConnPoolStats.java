/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.pool;

import org.apache.hc.core5.pool.PoolStats;

public interface ConnPoolStats<T> {
    public PoolStats getTotalStats();

    public PoolStats getStats(T var1);
}

