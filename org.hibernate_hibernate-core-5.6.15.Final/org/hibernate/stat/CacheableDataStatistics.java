/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.io.Serializable;

public interface CacheableDataStatistics
extends Serializable {
    public static final long NOT_CACHED_COUNT = Long.MIN_VALUE;

    public String getCacheRegionName();

    public long getCachePutCount();

    public long getCacheHitCount();

    public long getCacheMissCount();
}

