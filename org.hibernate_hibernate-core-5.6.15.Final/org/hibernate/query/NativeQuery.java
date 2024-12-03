/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.Parameter
 *  javax.persistence.TemporalType
 */
package org.hibernate.query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.SQLQuery;
import org.hibernate.SynchronizeableQuery;
import org.hibernate.query.Query;
import org.hibernate.query.QueryParameter;
import org.hibernate.type.Type;

public interface NativeQuery<T>
extends Query<T>,
SQLQuery<T>,
SynchronizeableQuery<T> {
    @Override
    public NativeQuery<T> setFlushMode(FlushMode var1);

    @Override
    public NativeQuery<T> setResultSetMapping(String var1);

    @Override
    public <P> NativeQuery<T> setParameter(QueryParameter<P> var1, P var2);

    @Override
    public <P> NativeQuery<T> setParameter(Parameter<P> var1, P var2);

    @Override
    public <P> NativeQuery<T> setParameter(QueryParameter<P> var1, P var2, Type var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Object var2, Type var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Object var2, Type var3);

    @Override
    public <P> NativeQuery<T> setParameter(QueryParameter<P> var1, P var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Object var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Object var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(Parameter<Calendar> var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(Parameter<Date> var1, Date var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Object var2);

    @Override
    public NativeQuery<T> setParameter(String var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Date var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Object var2);

    @Override
    public NativeQuery<T> setParameter(int var1, Calendar var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Date var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(Parameter<Instant> var1, Instant var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(Parameter<LocalDateTime> var1, LocalDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(Parameter<ZonedDateTime> var1, ZonedDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(Parameter<OffsetDateTime> var1, OffsetDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, Instant var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, LocalDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, ZonedDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(String var1, OffsetDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, Instant var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, LocalDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, ZonedDateTime var2, TemporalType var3);

    @Override
    public NativeQuery<T> setParameter(int var1, OffsetDateTime var2, TemporalType var3);

    @Override
    public <P> NativeQuery<T> setParameterList(QueryParameter<P> var1, Collection<P> var2);

    @Override
    public NativeQuery<T> setParameterList(String var1, Collection var2);

    @Override
    public NativeQuery<T> setParameterList(String var1, Collection var2, Type var3);

    @Override
    public NativeQuery<T> setParameterList(String var1, Object[] var2, Type var3);

    @Override
    public NativeQuery<T> setParameterList(String var1, Object[] var2);

    @Override
    public NativeQuery<T> setProperties(Object var1);

    @Override
    public NativeQuery<T> setProperties(Map var1);

    @Override
    public NativeQuery<T> addSynchronizedQuerySpace(String var1);

    @Override
    public NativeQuery<T> addSynchronizedEntityName(String var1) throws MappingException;

    @Override
    public NativeQuery<T> addSynchronizedEntityClass(Class var1) throws MappingException;

    @Override
    public boolean isCallable();

    @Override
    public NativeQuery<T> addScalar(String var1);

    @Override
    public NativeQuery<T> addScalar(String var1, Type var2);

    @Override
    public SQLQuery.RootReturn addRoot(String var1, String var2);

    @Override
    public SQLQuery.RootReturn addRoot(String var1, Class var2);

    @Override
    public NativeQuery<T> addEntity(String var1);

    @Override
    public NativeQuery<T> addEntity(String var1, String var2);

    @Override
    public NativeQuery<T> addEntity(String var1, String var2, LockMode var3);

    @Override
    public NativeQuery<T> addEntity(Class var1);

    @Override
    public NativeQuery<T> addEntity(String var1, Class var2);

    @Override
    public NativeQuery<T> addEntity(String var1, Class var2, LockMode var3);

    @Override
    public SQLQuery.FetchReturn addFetch(String var1, String var2, String var3);

    @Override
    public NativeQuery<T> addJoin(String var1, String var2);

    @Override
    public NativeQuery<T> addJoin(String var1, String var2, String var3);

    @Override
    public NativeQuery<T> addJoin(String var1, String var2, LockMode var3);

    @Override
    public NativeQuery<T> setHibernateFlushMode(FlushMode var1);

    @Override
    public NativeQuery<T> setFlushMode(FlushModeType var1);

    @Override
    public NativeQuery<T> setCacheMode(CacheMode var1);

    @Override
    public NativeQuery<T> setCacheable(boolean var1);

    @Override
    public NativeQuery<T> setCacheRegion(String var1);

    @Override
    public NativeQuery<T> setTimeout(int var1);

    @Override
    public NativeQuery<T> setFetchSize(int var1);

    @Override
    public NativeQuery<T> setReadOnly(boolean var1);

    @Override
    public NativeQuery<T> setLockOptions(LockOptions var1);

    @Override
    public NativeQuery<T> setLockMode(String var1, LockMode var2);

    @Override
    public NativeQuery<T> setComment(String var1);

    @Override
    public NativeQuery<T> addQueryHint(String var1);

    @Override
    public NativeQuery<T> setMaxResults(int var1);

    @Override
    public NativeQuery<T> setFirstResult(int var1);

    @Override
    public NativeQuery<T> setHint(String var1, Object var2);

    @Override
    public NativeQuery<T> setLockMode(LockModeType var1);
}

