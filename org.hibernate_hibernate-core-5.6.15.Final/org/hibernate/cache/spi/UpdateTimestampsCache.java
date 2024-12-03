/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.Set;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

@Deprecated
public interface UpdateTimestampsCache {
    public TimestampsRegion getRegion();

    public void preInvalidate(Serializable[] var1, SharedSessionContractImplementor var2) throws CacheException;

    public void invalidate(Serializable[] var1, SharedSessionContractImplementor var2) throws CacheException;

    public boolean isUpToDate(Set<Serializable> var1, Long var2, SharedSessionContractImplementor var3) throws CacheException;

    public void clear() throws CacheException;

    public void destroy();
}

