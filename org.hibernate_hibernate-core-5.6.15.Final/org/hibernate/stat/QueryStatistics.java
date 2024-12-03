/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.io.Serializable;

public interface QueryStatistics
extends Serializable {
    public long getExecutionCount();

    public long getExecutionRowCount();

    public long getExecutionAvgTime();

    public long getExecutionMaxTime();

    public long getExecutionMinTime();

    public long getExecutionTotalTime();

    public double getExecutionAvgTimeAsDouble();

    public long getCacheHitCount();

    public long getCacheMissCount();

    public long getCachePutCount();

    default public long getPlanCacheHitCount() {
        return 0L;
    }

    default public long getPlanCacheMissCount() {
        return 0L;
    }

    default public long getPlanCompilationTotalMicroseconds() {
        return 0L;
    }
}

