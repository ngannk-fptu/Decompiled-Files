/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 *  javax.persistence.TemporalType
 */
package org.hibernate.query.spi;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Incubating;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.SQLQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.QueryParameter;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.type.Type;

@Incubating
public interface NativeQueryImplementor<T>
extends QueryImplementor<T>,
NativeQuery<T> {
    public NativeQueryImplementor setCollectionKey(Serializable var1);

    @Override
    public NativeQueryImplementor<T> addScalar(String var1);

    @Override
    public NativeQueryImplementor<T> addScalar(String var1, Type var2);

    @Override
    public SQLQuery.RootReturn addRoot(String var1, String var2);

    @Override
    public NativeQueryImplementor<T> addEntity(String var1);

    @Override
    public NativeQueryImplementor<T> addEntity(String var1, String var2);

    @Override
    public NativeQueryImplementor<T> addEntity(String var1, String var2, LockMode var3);

    @Override
    public NativeQueryImplementor<T> addEntity(Class var1);

    @Override
    public NativeQueryImplementor<T> addEntity(String var1, Class var2);

    @Override
    public NativeQueryImplementor<T> addEntity(String var1, Class var2, LockMode var3);

    @Override
    public NativeQueryImplementor<T> addJoin(String var1, String var2);

    @Override
    public NativeQueryImplementor<T> addJoin(String var1, String var2, String var3);

    @Override
    public NativeQueryImplementor<T> addJoin(String var1, String var2, LockMode var3);

    @Override
    public NativeQueryImplementor<T> setHibernateFlushMode(FlushMode var1);

    @Override
    public NativeQueryImplementor<T> setCacheMode(CacheMode var1);

    @Override
    public NativeQueryImplementor<T> setCacheable(boolean var1);

    @Override
    public NativeQueryImplementor<T> setCacheRegion(String var1);

    @Override
    public NativeQueryImplementor<T> setTimeout(int var1);

    @Override
    public NativeQueryImplementor<T> setFetchSize(int var1);

    @Override
    public NativeQueryImplementor<T> setReadOnly(boolean var1);

    @Override
    public NativeQueryImplementor<T> setLockOptions(LockOptions var1);

    @Override
    public NativeQueryImplementor<T> setLockMode(String var1, LockMode var2);

    @Override
    public NativeQueryImplementor<T> setComment(String var1);

    @Override
    public NativeQueryImplementor<T> addQueryHint(String var1);

    @Override
    public <P> NativeQueryImplementor<T> setParameter(QueryParameter<P> var1, P var2);

    @Override
    public <P> NativeQueryImplementor<T> setParameter(Parameter<P> var1, P var2);

    @Override
    public NativeQueryImplementor<T> setParameter(String var1, Object var2);

    @Override
    public <P> NativeQueryImplementor<T> setParameter(QueryParameter<P> var1, P var2, Type var3);

    @Override
    public NativeQueryImplementor<T> setParameter(int var1, Object var2);

    @Override
    public NativeQueryImplementor<T> setParameter(String var1, Object var2, Type var3);

    @Override
    public NativeQueryImplementor<T> setParameter(int var1, Object var2, Type var3);

    @Override
    public <P> NativeQueryImplementor<T> setParameter(QueryParameter<P> var1, P var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> setParameter(String var1, Object var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> setParameter(int var1, Object var2, TemporalType var3);

    @Override
    public <P> NativeQueryImplementor<T> setParameterList(QueryParameter<P> var1, Collection<P> var2);

    @Override
    public NativeQueryImplementor<T> setParameterList(String var1, Collection var2);

    @Override
    public NativeQueryImplementor<T> setParameterList(String var1, Collection var2, Type var3);

    @Override
    public NativeQueryImplementor<T> setParameterList(String var1, Object[] var2, Type var3);

    @Override
    public NativeQueryImplementor<T> setParameterList(String var1, Object[] var2);

    @Override
    public NativeQueryImplementor<T> setProperties(Object var1);

    @Override
    public NativeQueryImplementor<T> setProperties(Map var1);

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> setParameter(String var1, Date var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> setParameter(String var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> setParameter(int var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> setParameter(int var1, Date var2, TemporalType var3);

    @Override
    public NativeQueryImplementor<T> addSynchronizedQuerySpace(String var1);

    @Override
    public NativeQueryImplementor<T> addSynchronizedEntityName(String var1) throws MappingException;

    @Override
    public NativeQueryImplementor<T> setFlushMode(FlushMode var1);

    @Override
    public NativeQueryImplementor<T> addSynchronizedEntityClass(Class var1) throws MappingException;
}

