/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.spi;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.Query;
import org.hibernate.query.QueryProducer;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.query.spi.QueryImplementor;

public interface QueryProducerImplementor
extends QueryProducer {
    public SessionFactoryImplementor getFactory();

    public FlushMode getHibernateFlushMode();

    public CacheMode getCacheMode();

    @Override
    public QueryImplementor getNamedQuery(String var1);

    @Override
    public QueryImplementor createQuery(String var1);

    public <R> QueryImplementor<R> createQuery(String var1, Class<R> var2);

    @Override
    public Query createNamedQuery(String var1);

    public <R> QueryImplementor<R> createNamedQuery(String var1, Class<R> var2);

    @Override
    public NativeQueryImplementor createNativeQuery(String var1);

    public NativeQueryImplementor createNativeQuery(String var1, Class var2);

    @Override
    public NativeQueryImplementor createNativeQuery(String var1, String var2);

    @Override
    public NativeQueryImplementor getNamedNativeQuery(String var1);

    @Override
    default public NativeQueryImplementor getNamedSQLQuery(String name) {
        return (NativeQueryImplementor)QueryProducer.super.getNamedSQLQuery(name);
    }

    @Override
    default public NativeQueryImplementor createSQLQuery(String queryString) {
        return (NativeQueryImplementor)QueryProducer.super.createSQLQuery(queryString);
    }
}

