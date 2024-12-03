/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.io.Serializable;
import org.hibernate.stat.CacheableDataStatistics;

public interface EntityStatistics
extends CacheableDataStatistics,
Serializable {
    public long getDeleteCount();

    public long getInsertCount();

    public long getUpdateCount();

    public long getLoadCount();

    public long getFetchCount();

    public long getOptimisticFailureCount();
}

