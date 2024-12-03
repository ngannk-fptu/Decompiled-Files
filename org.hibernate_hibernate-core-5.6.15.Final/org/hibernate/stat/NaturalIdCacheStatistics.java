/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@Deprecated
public interface NaturalIdCacheStatistics
extends Serializable {
    public long getExecutionCount();

    public long getExecutionAvgTime();

    public long getExecutionMaxTime();

    public long getExecutionMinTime();

    public long getHitCount();

    public long getMissCount();

    public long getPutCount();

    public long getElementCountInMemory();

    public long getElementCountOnDisk();

    public long getSizeInMemory();

    default public Map getEntries() {
        return Collections.emptyMap();
    }
}

