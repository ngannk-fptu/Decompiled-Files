/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.io.Serializable;
import org.hibernate.stat.CacheableDataStatistics;

public interface CollectionStatistics
extends CacheableDataStatistics,
Serializable {
    public long getLoadCount();

    public long getFetchCount();

    public long getRecreateCount();

    public long getRemoveCount();

    public long getUpdateCount();
}

