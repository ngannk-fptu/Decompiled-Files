/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.io.Serializable;
import org.hibernate.stat.CacheableDataStatistics;

public interface NaturalIdStatistics
extends CacheableDataStatistics,
Serializable {
    public long getExecutionCount();

    public long getExecutionAvgTime();

    public long getExecutionMaxTime();

    public long getExecutionMinTime();
}

