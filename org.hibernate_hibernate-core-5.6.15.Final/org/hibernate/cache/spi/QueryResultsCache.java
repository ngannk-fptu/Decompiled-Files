/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

public interface QueryResultsCache
extends QueryCache {
    @Override
    public QueryResultsRegion getRegion();

    @Override
    default public void clear() throws CacheException {
        this.getRegion().clear();
    }

    public boolean put(QueryKey var1, List var2, Type[] var3, SharedSessionContractImplementor var4) throws HibernateException;

    public List get(QueryKey var1, Set<Serializable> var2, Type[] var3, SharedSessionContractImplementor var4) throws HibernateException;

    public List get(QueryKey var1, String[] var2, Type[] var3, SharedSessionContractImplementor var4) throws HibernateException;

    @Override
    default public boolean put(QueryKey key, Type[] returnTypes, List result, boolean isNaturalKeyLookup, SharedSessionContractImplementor session) {
        return this.put(key, result, returnTypes, session);
    }

    @Override
    default public List get(QueryKey key, Type[] returnTypes, boolean isNaturalKeyLookup, Set<Serializable> spaces, SharedSessionContractImplementor session) {
        return this.get(key, spaces, returnTypes, session);
    }

    @Override
    default public void destroy() {
    }
}

