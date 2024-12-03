/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;

@Deprecated
public interface QueryCache {
    public void clear();

    public boolean put(QueryKey var1, Type[] var2, List var3, boolean var4, SharedSessionContractImplementor var5);

    public List get(QueryKey var1, Type[] var2, boolean var3, Set<Serializable> var4, SharedSessionContractImplementor var5);

    public void destroy();

    public QueryResultsRegion getRegion();
}

