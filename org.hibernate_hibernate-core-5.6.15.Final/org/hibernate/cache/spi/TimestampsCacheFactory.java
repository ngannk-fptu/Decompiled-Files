/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.cache.spi.TimestampsRegion;

public interface TimestampsCacheFactory {
    public TimestampsCache buildTimestampsCache(CacheImplementor var1, TimestampsRegion var2);
}

