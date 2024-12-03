/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.type.Type;

@Deprecated
public interface BasicQueryContract<T extends BasicQueryContract> {
    @Deprecated
    default public BasicQueryContract setFlushMode(FlushMode flushMode) {
        this.setHibernateFlushMode(flushMode);
        return this;
    }

    public FlushMode getHibernateFlushMode();

    public T setHibernateFlushMode(FlushMode var1);

    public CacheMode getCacheMode();

    public T setCacheMode(CacheMode var1);

    public boolean isCacheable();

    public T setCacheable(boolean var1);

    public String getCacheRegion();

    public T setCacheRegion(String var1);

    public Integer getTimeout();

    public T setTimeout(int var1);

    public Integer getFetchSize();

    public T setFetchSize(int var1);

    public boolean isReadOnly();

    public T setReadOnly(boolean var1);

    @Deprecated
    public Type[] getReturnTypes();
}

