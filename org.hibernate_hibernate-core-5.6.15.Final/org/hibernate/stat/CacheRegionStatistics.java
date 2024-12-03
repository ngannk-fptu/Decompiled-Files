/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.io.Serializable;

public interface CacheRegionStatistics
extends Serializable {
    public static final long NO_EXTENDED_STAT_SUPPORT_RETURN = Long.MIN_VALUE;

    public String getRegionName();

    public long getPutCount();

    public long getHitCount();

    public long getMissCount();

    public long getElementCountInMemory();

    public long getElementCountOnDisk();

    public long getSizeInMemory();
}

