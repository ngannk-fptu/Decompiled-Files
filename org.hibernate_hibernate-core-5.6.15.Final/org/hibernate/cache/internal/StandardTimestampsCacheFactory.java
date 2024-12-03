/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.internal;

import org.hibernate.cache.internal.TimestampsCacheEnabledImpl;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.cache.spi.TimestampsCacheFactory;
import org.hibernate.cache.spi.TimestampsRegion;

public class StandardTimestampsCacheFactory
implements TimestampsCacheFactory {
    public static final StandardTimestampsCacheFactory INSTANCE = new StandardTimestampsCacheFactory();

    @Override
    public TimestampsCache buildTimestampsCache(CacheImplementor cacheManager, TimestampsRegion timestampsRegion) {
        return new TimestampsCacheEnabledImpl(timestampsRegion);
    }
}

